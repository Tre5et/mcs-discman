package net.treset.minecraft_server_discord_bot.server;

import net.treset.minecraft_server_discord_bot.config.Config;

import java.io.IOException;

public class ConnectionManager {
    public static boolean isConnected() {
        if(!WebsocketClient.hasInstance()) return false;
        return WebsocketClient.getInstance().isOpen();
    }

    public static void connect() throws IOException {
        new WebsocketClient(
                Config.communication.server_host,
                Config.communication.server_port,
                Config.communication.use_ssl,
                Config.communication.server_secret,
                RpcMessager::handleMessage
        );
    }

    public static boolean disconnect() throws IOException {
        if(isConnected()) {
            try {
                WebsocketClient.getInstance().closeBlocking();
                return isConnected();
            } catch (InterruptedException e) {
                throw new IOException("Failed to wait for server disconnection", e);
            }
        }
        return true;
    }

    public static void forceDisconnect() {
        if(isConnected()) {
            WebsocketClient.getInstance().closeConnection(-1, "Forced close by user");
        }
    }

    public static void send(String message) throws IOException {
        if(!isConnected()) connect();
        WebsocketClient.getInstance().send(message);
    }

    public static boolean isRunning() {
        if(isConnected()) return true;
        try {
            connect();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
