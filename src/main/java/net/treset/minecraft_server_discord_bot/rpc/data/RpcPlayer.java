package net.treset.minecraft_server_discord_bot.rpc.data;

import net.treset.minecraft_server_discord_bot.rpc.schemas.RpcResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RpcPlayer {
    public static String extractPlayer(Object o) throws IOException {
        if(!(o instanceof Map<?,?> p)) {
            throw new IOException("Invalid player data format: " + o);
        }
        if(p.containsKey("name")) {
            return p.get("name").toString();
        } else {
            throw new IOException("Missing name for player: " + o);
        }
    }

    public static List<String> extractPlayersFromResponse(RpcResponse response) throws IOException {
        if(!(response.result() instanceof List<?> l)) {
            throw new IOException("Unexpected format for player response: " + response.result());
        }

        List<String> list = new ArrayList<>();
        for(Object o : l) {
            list.add(extractPlayer(o));
        }
        return list;
    }

}
