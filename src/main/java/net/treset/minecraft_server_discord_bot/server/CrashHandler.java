package net.treset.minecraft_server_discord_bot.server;

import dev.treset.mcdl.servermanagement.exception.RpcCommunicationException;
import dev.treset.mcdl.servermanagement.vanilla.RpcMethods;
import dev.treset.mcdl.servermanagement.vanilla.RpcNotifications;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
import net.treset.minecraft_server_discord_bot.exception.ServerOperationException;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class CrashHandler {
    private static boolean isStopExpected = false;
    private static final AtomicBoolean handlingStop = new AtomicBoolean(false);
    private static final Object handlerLock = new Object() {};
    private static List<LocalDateTime> lastCrashes = new ArrayList<>();

    public static void expectStop() {
        isStopExpected = true;
    }

    public static void unexpectStop() {
        isStopExpected = false;
    }

    public static void processStopping() {
        synchronized (handlerLock) {
            if (handlingStop.get() || Config.get().crash == null) return;
            handlingStop.set(true);
        }

        if(isStopExpected) {
            handlingStop.set(false);
            unexpectStop();
            return;
        }
        LocalDateTime crashTime = LocalDateTime.now();
        DiscordBot.sendText("Unexpected stop detected, confirming server has stopped...", MessageOrigin.SCHEDULE);
        while(ServerActions.isRunning()) {
            try {
                ManagementClient.get().awaitNotification(
                        RpcNotifications.Server.saved(),
                        () -> ManagementClient.get().send(
                                RpcMethods.Server.STATUS,
                                r -> {},
                                e -> Logger.warn("Failed to send status request on crash")
                        ),
                        1000L
                );
            } catch (RpcCommunicationException e) {
                Logger.warn(e, "Failed to wait for crash stop");
            }
        }
        DiscordBot.sendText("Unexpected stop confirmed.", MessageOrigin.SCHEDULE);
        lastCrashes = lastCrashes.stream().filter(t -> Duration.between(t, crashTime).toSeconds() <= Config.get().crash.recentTimeout).collect(Collectors.toCollection(ArrayList::new));
        lastCrashes.add(crashTime);
        if(lastCrashes.size() > Config.get().crash.maxRetries) {
            DiscordBot.sendText("Too many crashes recently, not attempting to restart.", MessageOrigin.SCHEDULE);
            return;
        }
        DiscordBot.sendText("Attempting to restart server...", MessageOrigin.SCHEDULE);
        try {
            Thread.sleep(Config.get().server.restartDelay * 1000L);
        } catch (InterruptedException e) {
            Logger.warn(e, "Failed to wait for server restart delay when handling crash");
        }
        try {
            ServerActions.startServer();
        } catch (ServerOperationException e) {
            Logger.error(e, "Failed to start server after crash");
            DiscordBot.sendText("Failed to restart server after crash.", MessageOrigin.SCHEDULE);
            handlingStop.set(false);
            return;
        }
        DiscordBot.sendText("Started server after crash.", MessageOrigin.SCHEDULE);
        handlingStop.set(false);
    }
}
