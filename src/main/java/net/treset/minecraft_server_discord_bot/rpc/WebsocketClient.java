package net.treset.minecraft_server_discord_bot.rpc;

import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.messaging.MessageOrigin;
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
        MessageManager.sendText("Connection to server established.", MessageOrigin.RPC);
        expectFailureOnStart = false;
    }

    @Override
    public void onMessage(String s) {
        messageHandler.accept(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        if(!expectFailureOnStart) {
            MessageManager.sendText("Connection to server closed.", MessageOrigin.RPC);
        }
    }

    @Override
    public void onError(Exception e) {
        if(!expectFailureOnStart) {
            MessageManager.sendText("Server connection error.", MessageOrigin.RPC);
            MessageManager.log("Error in RPC connection", LogLevel.ERROR, e);
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
