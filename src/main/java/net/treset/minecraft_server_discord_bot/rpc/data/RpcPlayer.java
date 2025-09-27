package net.treset.minecraft_server_discord_bot.rpc.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record RpcPlayer(
        String name,
        String id
) {
    public static RpcPlayer from(Object o) throws IOException {
        if(!(o instanceof Map<?,?> p)) {
            throw new IOException("Invalid player data format: " + o);
        }
        if(p.containsKey("name") && p.containsKey("id")) {
            return new RpcPlayer(p.get("name").toString(), p.get("id").toString());
        }
        throw new IOException("Missing fields for player: " + p);
    }

    public static List<RpcPlayer> fromList(Object o) throws IOException {
        if(!(o instanceof List<?> l)) {
            throw new IOException("Unexpected format for player response: " + o);
        }

        List<RpcPlayer> list = new ArrayList<>();
        for(Object p : l) {
            list.add(from(p));
        }
        return list;
    }

}
