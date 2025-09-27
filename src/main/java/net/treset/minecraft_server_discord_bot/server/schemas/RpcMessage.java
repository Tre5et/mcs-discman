package net.treset.minecraft_server_discord_bot.server.schemas;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public record RpcMessage(
    String jsonrpc,
    Integer id,
    Object result,
    RpcError error,
    String method,
    List<Object> params
) implements RpcNotification, RpcResponse {
    private static final Gson GSON = new Gson();

    public boolean isResponse() {
        return Objects.equals(jsonrpc, "2.0") && id != null && (result != null || error != null);
    }

    public boolean isNotification() {
        return Objects.equals(jsonrpc, "2.0") && method != null;
    }

    public static RpcMessage fromJson(String json) throws IOException {
        try {
            return GSON.fromJson(json, RpcMessage.class);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e.getCause());
        }
    }
}
