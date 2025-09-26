package net.treset.minecraft_server_discord_bot.rpc;

import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.rpc.schemas.*;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MessageHandler {
    private static final int RESPONSE_TIMEOUT = 10_000;
    private static final Random RANDOM = new Random();

    private static final Map<Integer, ResponseHandler> responseHandlers = new HashMap<>();
    private static final Map<String, Consumer<RpcNotification>> notificationHandlers = new HashMap<>();

    public static void handleMessage(String content) {
        try {
            RpcMessage message = RpcMessage.fromJson(content);
            if(message.isResponse()) {
                handleResponse(message);
                return;
            }
            if(message.isNotification()) {
                handleNotification(message);
                return;
            }
            handleUnexpected(content, null);
        } catch (IOException e) {
            handleUnexpected(content, e);
        }
    }

    public static int send(String method, BiConsumer<Object, RpcError> responseCallback, Object... params) throws IOException {
        int id = generateUniqueId();
        responseHandlers.put(id, new ResponseHandler(responseCallback));
        ConnectionManager.send(constructMessage(id, method, params));
        return id;
    }

    public static int send(String method, Object... params) throws IOException {
        return send(method, (r,e) -> {}, params);
    }

    public static void addNotificationHandler(String method, Consumer<RpcNotification> handler) {
        notificationHandlers.put(method, handler);
    }

    private static String constructMessage(int id, String method, Object... params) {
        return RpcRequest.create(id, method, params).serialize();
    }

    private static int generateUniqueId() {
        int id = RANDOM.nextInt();
        while(responseHandlers.containsKey(id)) {
            id = RANDOM.nextInt();
        }
        return id;
    }

    private static void handleResponse(RpcResponse response) {
        ResponseHandler handler = responseHandlers.get(response.id());
        if(handler != null) {
            responseHandlers.remove(response.id());
            handler.callback.accept(response.result(), response.error());
        } else {
            DiscordBot.LOGGER.warn("Response with no handler for id = {}; result = {}; error = {}", response.id(), response.result(), response.error());
        }

        purgeOldResponseHandlers();
    }

    private static void purgeOldResponseHandlers() {
        for(Map.Entry<Integer, ResponseHandler> entry : responseHandlers.entrySet()) {
            if(entry.getValue().timeoutTime < System.currentTimeMillis()) {
                DiscordBot.LOGGER.warn("Response for id = {} took longer than 10 seconds, assuming lost", entry.getKey());
                responseHandlers.remove(entry.getKey());
                entry.getValue().callback.accept(null, new RpcError(-1, "Timed out", "No response after 10 seconds"));
            }
        }
    }

    private static void handleNotification(RpcNotification notification) {
        if(notificationHandlers.containsKey(notification.method())) {
            notificationHandlers.get(notification.method()).accept(notification);
        }
    }

    private static void handleUnexpected(String content, Exception e) {
        DiscordBot.LOGGER.warn("Unexpected RPC message: '{}'", content, e);
    }

    public static class ResponseHandler {
        private final long timeoutTime = System.currentTimeMillis() + RESPONSE_TIMEOUT;
        private final BiConsumer<Object, RpcError> callback;

        public ResponseHandler(BiConsumer<Object, RpcError> callback) {
            this.callback = callback;
        }
    }
}
