package net.treset.minecraft_server_discord_bot.server;

import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.schemas.RpcResponse;
import net.treset.minecraft_server_discord_bot.system.ConsoleHandler;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerActions {
    public static boolean isServerRunning() {
        return ConnectionManager.isConnected();
    }

    public static void startServer() {
        ConnectionManager.forceDisconnect();
        String cmd = Config.server.start_command;
        ConsoleHandler.executeCommand(cmd);
        new Thread(() -> {
            try {
                Thread.sleep(Config.communication.rpc_startup_delay * 1000L);
            } catch (InterruptedException e) {
                Logger.error("Waiting for RPC startup after server start interrupted.");
            }
            if(!ConnectionManager.isConnected()) {
                try {
                    ConnectionManager.connect();
                } catch (IOException e) {
                    DiscordBot.sendText("Failed to connect to server after starting! (perhaps the server didn't start correctly?)", MessageOrigin.RPC);
                }
            }
        }).start();
    }

    public static boolean stopServer() {
        PermanentOperations.isStopExpected = true;
        AtomicBoolean success2 = new AtomicBoolean(false);
        boolean success = RpcMessager.awaitNotification(() -> {
            try {
                success2.set(RpcMessager.request("minecraft:server/stop").isResult(true));
            } catch (IOException e) {
                Logger.warn(e,"Failed to initiate server stop!");
            }
        }, "minecraft:notification/server/saved", Config.server.stop_timeout * 1000L) != null;
        if(!success2.get()) {
            PermanentOperations.isStopExpected = false;
            Logger.warn("Failed to initiate server stop!");
            return false;
        }
        if(!success) {
            PermanentOperations.isStopExpected = false;
            Logger.warn("Failed to get server save confirmation after stopping!");
            return false;
        }
        return true;
    }

    public static boolean prepareServerForBackup() {
        if(ConnectionManager.isRunning()) {
            try {
                RpcResponse res = RpcMessager.request("minecraft:serversettings/autosave/set", false);
                boolean success = res.isResult(false);
                if (!success) {
                    Logger.warn("Failed to disable saving before backup");
                    return false;
                }

                AtomicBoolean success2 = new AtomicBoolean(false);
                success = RpcMessager.awaitNotification(() -> {
                    try {
                        success2.set(RpcMessager.request("minecraft:server/save", true).isResult(true));
                    } catch (IOException e) {
                        Logger.warn(e, "Failed to execute backup preparation");
                        success2.set(false);
                    }
                }, "minecraft:notification/server/saved", Config.server.save_timeout * 1000L) != null;
                if (!success2.get()) {
                    Logger.warn("Failed to initiate save before backup");
                    return false;
                }
                if (!success) {
                    Logger.warn("Failed to get save confirmation before backup");
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
        if(ConnectionManager.isRunning()) {
            try {
                boolean success = RpcMessager.request("minecraft:serversettings/autosave/set", true).isResult(true);
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
