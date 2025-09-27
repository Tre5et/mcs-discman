package net.treset.minecraft_server_discord_bot.server;

import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.schemas.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class RpcMessager {
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

    public static int send(String method, Consumer<RpcResponse> responseCallback, Object... params) throws IOException {
        int id = generateUniqueId();
        responseHandlers.put(id, new ResponseHandler(method, params, responseCallback));
        try {
            ConnectionManager.send(constructMessage(id, method, params));
        } catch (IOException e) {
            responseHandlers.remove(id);
            throw e;
        }
        return id;
    }

    public static int send(String method, Object... params) throws IOException {
        return send(method, r -> {}, params);
    }

    public static RpcResponse request(String method, Object... params) throws IOException {
        Object lock = new Object();

        int id = generateUniqueId();

        AtomicReference<RpcResponse> res = new AtomicReference<>();
        responseHandlers.put(id, new ResponseHandler(method, params, r -> {
            synchronized (lock) {
                res.set(r);
                lock.notify();
            }
        }));


        synchronized (lock) {
            try {
                ConnectionManager.send(constructMessage(id, method, params));
            } catch (IOException e) {
                responseHandlers.remove(id);
                throw e;
            }
            try {
                lock.wait(10_000);
            } catch (InterruptedException e) {
                throw new IOException("Failed to wait for response", e);
            }
        }

        if(res.get() == null) {
            purgeOldResponseHandlers();
            return RpcResponse.Timeout(id);
        }

        return res.get();
    }

    public static void addNotificationHandler(String method, Consumer<RpcNotification> handler) {
        notificationHandlers.put(method, handler);
    }

    public static RpcNotification awaitNotification(String method, long timeoutMs) {
        return awaitNotification(() -> {}, method, timeoutMs);
    }

    public static RpcNotification awaitNotification(Runnable actionBefore, String method, long timeoutMs) {
        Object lock = new Object();

        AtomicReference<RpcNotification> result = new AtomicReference<>();
        Consumer<RpcNotification> prevHandler = notificationHandlers.get(method);

        addNotificationHandler(method, n -> {
            System.out.println("Got not " + method);
            result.set(n);
            synchronized (lock) {
                lock.notify();
            }
        });

        synchronized (lock) {
            actionBefore.run();
            try {
                lock.wait(timeoutMs);
            } catch (InterruptedException e) {
                Logger.warn("Interrupted while waiting for notification", e);
            }
        }

        if(result.get() == null) {
            return null;
        }

        if(prevHandler != null) prevHandler.accept(result.get());
        addNotificationHandler(method, prevHandler);
        return result.get();
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
            handler.callback.accept(response);
        } else {
            Logger.warn("Response with no handler for id = %d; result = %s; error = %s", response.id(), response.result(), response.error());
        }

        purgeOldResponseHandlers();
    }

    private static void purgeOldResponseHandlers() {
        for(Map.Entry<Integer, ResponseHandler> entry : responseHandlers.entrySet()) {
            if(entry.getValue().timeoutTime < System.currentTimeMillis()) {
                Logger.warn("Response for id = %d took longer than 10 seconds, assuming lost, method = %s, params = %s", entry.getKey(), entry.getValue().method, entry.getValue().params);
                responseHandlers.remove(entry.getKey());
                entry.getValue().callback.accept(RpcResponse.Timeout(entry.getKey()));
            }
        }
    }

    private static void handleNotification(RpcNotification notification) {
        System.out.println("proc not " + notification.method());
        if(notificationHandlers.containsKey(notification.method())) {
            notificationHandlers.get(notification.method()).accept(notification);
        }
    }

    private static void handleUnexpected(String content, Exception e) {
        Logger.warn(e,"Unexpected RPC message: '%s'", content);
    }

    public static class ResponseHandler {
        private final long timeoutTime = System.currentTimeMillis() + RESPONSE_TIMEOUT;
        private final String method;
        private final Object[] params;
        private final Consumer<RpcResponse> callback;

        public ResponseHandler(String method, Object[] params, Consumer<RpcResponse> callback) {
            this.method = method;
            this.params = params;
            this.callback = callback;
        }
    }
}
