package net.treset.minecraft_server_discord_bot.server;

import dev.treset.mcdl.servermanagement.exception.RpcCommunicationException;
import dev.treset.mcdl.servermanagement.exception.RpcConnectionException;
import dev.treset.mcdl.servermanagement.vanilla.RpcMethods;
import dev.treset.mcdl.servermanagement.vanilla.RpcNotifications;
import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
import net.treset.minecraft_server_discord_bot.exception.ServerOperationException;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.system.ConsoleHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ServerActions {
    public static boolean isRunning() {
        if(ManagementClient.get().isConnected()) {
            return true;
        }
        try {
            ManagementClient.get().connect();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void startServer() {
        if(ManagementClient.get().isConnected()) {
            ManagementClient.get().forceDisconnect();
        }
        String cmd = Config.server.start_command;
        // TODO: get correct command
        try {
            ConsoleHandler.startProcess(List.of("cmd.exe", "/k", "start", "D:\\Hannes\\Coding\\gits\\mcs-discman-client\\gradlew.bat", "runServer"), "D:\\Hannes\\Coding\\gits\\mcs-discman-client\\");
        } catch (IOException e) {
            DiscordBot.sendText("Failed to execute server start", MessageOrigin.RPC);
        }
        new Thread(() -> {
            try {
                awaitServerStarted();
            } catch (ServerOperationException e) {
                DiscordBot.sendText("Failed to start server.", MessageOrigin.RPC);
            }
        }).start();
    }

    public static void awaitServerStarted() throws ServerOperationException {
        long timeout = Config.communication.rpc_startup_delay * 1000L;
        try {
            repeatTryConnect(timeout, 1000L);
        } catch (RpcConnectionException e) {
            throw new ServerOperationException("Failed to wait for server start", e);
        }
        AtomicBoolean started = new AtomicBoolean(false);
        try {
            ManagementClient.get().awaitNotification(
                    RpcNotifications.Server.started(),
                    () -> {
                        if(ManagementClient.get().request(RpcMethods.Server.STATUS).started() == true) {
                            started.set(true);
                            throw new Exception("Started already!");
                        }
                    },
                    timeout
            );

        } catch (RpcCommunicationException e) {
            if(!started.get()) {
                throw new ServerOperationException("Failed to start server in " + timeout + "ms", e);
            }
        }
    }

    private static void repeatTryConnect(long timeout, long retryTimeout) throws RpcConnectionException {
        Object waitLock = new Object() {};
        AtomicReference<RpcConnectionException> error = new AtomicReference<>();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> future = executorService.scheduleAtFixedRate(() -> {
            try {
                ManagementClient.get().connect();
            } catch (RpcConnectionException e) {
                error.set(e);
            }
            synchronized (waitLock) {
                waitLock.notify();
            }
        }, 0, retryTimeout, TimeUnit.MILLISECONDS);

        long endTime = System.currentTimeMillis() + timeout;
        long remainingTime;
        do {
            remainingTime = endTime - System.currentTimeMillis();
            try {
                synchronized (waitLock) {
                    waitLock.wait(remainingTime);
                }
            } catch (InterruptedException e) {
                future.cancel(true);
                throw new RpcConnectionException("Failed to wait for start", e);
            }
        } while(!ManagementClient.get().isConnected() && remainingTime > 0);

        future.cancel(true);
        if(!ManagementClient.get().isConnected()) {
            throw new RpcConnectionException("Failed to connect to server within " + timeout + "ms", error.get());
        }
    }

    public static boolean stopServer() {
        PermanentOperations.isStopExpected = true;
        AtomicBoolean success = new AtomicBoolean(false);
        try {
            ManagementClient.get().awaitNotification(
                    RpcNotifications.Server.saved(),
                    () -> {
                        try {
                            success.set(ManagementClient.get().request(RpcMethods.Server.STOP));
                        } catch (IOException e) {
                            Logger.warn(e, "Failed to initiate server stop!");
                        }
                    },
                    Config.server.stop_timeout * 1000L
            );
        } catch (IOException e) {
            PermanentOperations.isStopExpected = false;
            Logger.warn("Failed to get server save confirmation after stopping!");
            return false;
        }
        if(!success.get()) {
            PermanentOperations.isStopExpected = false;
            Logger.warn("Failed to initiate server stop!");
            return false;
        }
        return true;
    }
}
