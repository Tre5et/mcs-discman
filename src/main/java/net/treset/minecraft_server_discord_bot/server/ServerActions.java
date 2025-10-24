package net.treset.minecraft_server_discord_bot.server;

import dev.treset.mcdl.servermanagement.request.RpcResponse;
import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.system.ConsoleHandler;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

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
        ManagementClient.get().forceDisconnect();
        String cmd = Config.server.start_command;
        ConsoleHandler.executeCommand(cmd);
        new Thread(() -> {
            try {
                Thread.sleep(Config.communication.rpc_startup_delay * 1000L);
            } catch (InterruptedException e) {
                Logger.error("Waiting for RPC startup after server start interrupted.");
            }
            if(!ManagementClient.get().isConnected()) {
                try {
                    ManagementClient.get().connect();
                } catch (IOException e) {
                    DiscordBot.sendText("Failed to connect to server after starting! (perhaps the server didn't start correctly?)", MessageOrigin.RPC);
                }
            }
        }).start();
    }

    public static boolean stopServer() {
        PermanentOperations.isStopExpected = true;
        AtomicBoolean success = new AtomicBoolean(false);
        try {
            ManagementClient.get().awaitNotification(
                    "minecraft:notification/server/saved",
                    Config.server.stop_timeout * 1000L,
                    () -> {
                        try {
                            success.set(ManagementClient.get().request("minecraft:server/stop").resultAsBoolean());
                        } catch (IOException e) {
                            Logger.warn(e, "Failed to initiate server stop!");
                        }
                    }
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

    public static boolean prepareServerForBackup() {
        if(isRunning()) {
            try {
                RpcResponse res = ManagementClient.get().request("minecraft:serversettings/autosave/set", false);
                boolean success = res.resultAsBoolean();
                if (!success) {
                    Logger.warn("Failed to disable saving before backup");
                    return false;
                }

                AtomicBoolean success2 = new AtomicBoolean(false);
                try {
                    ManagementClient.get().awaitNotification(
                            "minecraft:notification/server/saved",
                            Config.server.save_timeout * 1000L,
                            () -> {
                                try {
                                    success2.set(ManagementClient.get().request("minecraft:server/save", true).resultAsBoolean());
                                } catch (IOException e) {
                                    Logger.warn(e, "Failed to execute backup preparation");
                                    success2.set(false);
                                }
                            }
                    );
                } catch (IOException e) {
                    Logger.warn("Failed to get save confirmation before backup");
                    return false;
                }
                if (!success2.get()) {
                    Logger.warn("Failed to initiate save before backup");
                    return false;
                }
            } catch (IOException e) {
                Logger.warn(e, "Failed to execute backup preparation");
                return false;
            }
        }
        return true;
    }

    public static boolean undoBackupPreparation() {
        if(isRunning()) {
            try {
                boolean success = ManagementClient.get().request("minecraft:serversettings/autosave/set", true).resultAsBoolean();
                if (!success) {
                    Logger.warn("Failed to enable saving after backup");
                    return false;
                }
            } catch (IOException e) {
                Logger.warn(e, "Failed to undo backup preparation");
                return false;
            }
        }
        return true;
    }
}
