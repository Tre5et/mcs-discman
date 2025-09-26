package net.treset.minecraft_server_discord_bot.rpc.schemas;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public record RpcRequest(
    int id,
    String method,
    List<Object> params
) {
    public static final Gson GSON = new Gson();

    public String serialize() {
        return GSON.toJson(this);
    }

    public static RpcRequest create(int id, String method, Object... params) {
        return new RpcRequest(
                id,
                method,
                Arrays.asList(params)
        );
    }
}
