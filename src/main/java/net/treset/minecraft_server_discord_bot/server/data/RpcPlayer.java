package net.treset.minecraft_server_discord_bot.server.data;

import com.google.gson.reflect.TypeToken;
import dev.treset.mcdl.servermanagement.request.RpcResponse;

import java.io.IOException;
import java.util.List;

public class RpcPlayer {
    private String name;
    private String id;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public static RpcPlayer from(RpcResponse res) throws IOException {
        if(!res.hasResult()) {
            throw new IOException("Failed to get player: Error: " + res.error().message());
        }
        return res.resultAs(RpcPlayer.class);
    }

    @SuppressWarnings("unchecked")
    public static List<RpcPlayer> fromList(RpcResponse res) throws IOException {
        if(!res.hasResult()) {
            throw new IOException("Failed to get player list: Error: " + res.error().message());
        }
        return (List<RpcPlayer>)res.resultAs(TypeToken.getParameterized(List.class, RpcPlayer.class));
    }
}
