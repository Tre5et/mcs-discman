package net.treset.minecraft_server_discord_bot.rpc;

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
                MessageHandler::handleMessage
        );
    }

    public static void disconnect() {
        if(isConnected()) {
            WebsocketClient.getInstance().close();
        }
    }

    public static void send(String message) throws IOException {
        if(!isConnected()) connect();
        if(!isConnected()) throw new IOException("Unable to send message, connection is closed and couldn't be opened.");
        WebsocketClient.getInstance().send(message);
    }
}
