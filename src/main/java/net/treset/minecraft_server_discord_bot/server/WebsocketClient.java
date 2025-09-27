package net.treset.minecraft_server_discord_bot.server;

import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.function.Consumer;

public class WebsocketClient extends WebSocketClient {
    private final Consumer<String> messageHandler;
    private boolean expectFailureOnStart = true;

    public WebsocketClient(String host, int port, boolean ssl, String secret, Consumer<String> messageHandler) throws IOException {
        super(
                URI.create((ssl ? "wss" : "ws") + "://" + host + ":" + port),
                Map.of("Authorization", "Bearer " + secret)
        );
        try {
            boolean connected = this.connectBlocking();
            if(!connected) {
                throw new IOException("Could not connect to server.");
            }
        } catch (InterruptedException e) {
            throw new IOException("Failed to connect to Server", e);
        }
        this.messageHandler = messageHandler;
        instance = this;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        DiscordBot.sendText("Connection to server established.", MessageOrigin.RPC);
        expectFailureOnStart = false;
    }

    @Override
    public void onMessage(String s) {
        messageHandler.accept(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        if(!expectFailureOnStart) {
            DiscordBot.sendText("Connection to server closed.", MessageOrigin.RPC);
        }
    }

    @Override
    public void onError(Exception e) {
        if(!expectFailureOnStart) {
            DiscordBot.sendText("Server connection error.", MessageOrigin.RPC);
            Logger.error(e, "Error in RPC connection");
        }
    }

    private static WebSocketClient instance = null;
    public static boolean hasInstance() {
        return instance != null;
    }
    public static WebSocketClient getInstance() {
        return instance;
    }
}
