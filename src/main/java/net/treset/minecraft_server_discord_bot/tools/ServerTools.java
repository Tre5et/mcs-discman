package net.treset.minecraft_server_discord_bot.tools;

import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.messaging.MessageOrigin;
import net.treset.minecraft_server_discord_bot.rpc.ConnectionManager;
import net.treset.minecraft_server_discord_bot.rpc.MessageHandler;
import net.treset.minecraft_server_discord_bot.rpc.schemas.RpcResponse;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerTools {
    public static boolean isServerRunning() {
        return ConnectionManager.isConnected();
    }

    public static void startServer() {
        ConnectionManager.forceDisconnect();
        String cmd = Config.server.start_command;
        ExecTools.executeCommand(cmd);
        new Thread(() -> {
            try {
                Thread.sleep(Config.communication.rpc_startup_delay * 1000L);
            } catch (InterruptedException e) {
                DiscordBot.LOGGER.error("Waiting for RPC startup after server start interrupted.");
            }
            if(!ConnectionManager.isConnected()) {
                try {
                    ConnectionManager.connect();
                } catch (IOException e) {
                    MessageManager.sendText("Failed to connect to server after starting! (perhaps the server didn't start correctly?)", MessageOrigin.RPC);
                }
            }
        }).start();
    }

    public static boolean stopServer() {
        PermanentOperations.isStopExpected = true;
        AtomicBoolean success2 = new AtomicBoolean(false);
        boolean success = MessageHandler.awaitNotification(() -> {
            try {
                success2.set(MessageHandler.request("minecraft:server/stop").isResult(true));
            } catch (IOException e) {
                DiscordBot.LOGGER.warn("Failed to initiate server stop!", e);
            }
        }, "minecraft:notification/server/saved", Config.server.stop_timeout * 1000L) != null;
        if(!success2.get()) {
            PermanentOperations.isStopExpected = false;
            DiscordBot.LOGGER.warn("Failed to initiate server stop!");
            return false;
        }
        if(!success) {
            PermanentOperations.isStopExpected = false;
            DiscordBot.LOGGER.warn("Failed to get server save confirmation after stopping!");
            return false;
        }
        return true;
    }

    public static boolean prepareServerForBackup() {
        if(ConnectionManager.isRunning()) {
            try {
                RpcResponse res = MessageHandler.request("minecraft:serversettings/autosave/set", false);
                boolean success = res.isResult(false);
                if (!success) {
                    DiscordBot.LOGGER.warn("Failed to disable saving before backup");
                    return false;
                }

                AtomicBoolean success2 = new AtomicBoolean(false);
                success = MessageHandler.awaitNotification(() -> {
                    try {
                        success2.set(MessageHandler.request("minecraft:server/save", true).isResult(true));
                    } catch (IOException e) {
                        DiscordBot.LOGGER.warn("Failed to execute backup preparation", e);
                        success2.set(false);
                    }
                }, "minecraft:notification/server/saved", Config.server.save_timeout * 1000L) != null;
                if (!success2.get()) {
                    DiscordBot.LOGGER.warn("Failed to initiate save before backup");
                    return false;
                }
                if (!success) {
                    DiscordBot.LOGGER.warn("Failed to get save confirmation before backup");
                    return false;
                }
            } catch (IOException e) {
                DiscordBot.LOGGER.warn("Failed to execute backup preparation", e);
                return false;
            }
        }
        return true;
    }

    public static boolean undoBackupPreparation() {
        if(ConnectionManager.isRunning()) {
            try {
                boolean success = MessageHandler.request("minecraft:serversettings/autosave/set", true).isResult(true);
                if (!success) {
                    DiscordBot.LOGGER.warn("Failed to enable saving after backup");
                    return false;
                }
            } catch (IOException e) {
                DiscordBot.LOGGER.warn("Failed to undo backup preparation", e);
                return false;
            }
        }
        return true;
    }
}
