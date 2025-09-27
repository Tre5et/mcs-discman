package net.treset.minecraft_server_discord_bot.server.data;

import java.io.IOException;
import java.util.Map;

public record RpcVersion(
        double protocol,
        String name
) {
    public static RpcVersion from(Object o) throws IOException {
        if(!(o instanceof Map<?,?> m)) {
            throw new IOException("Invalid version data format: " + o);
        }
        if(m.containsKey("protocol") && m.get("protocol") instanceof Double && m.containsKey("name")) {
            return new RpcVersion((double) m.get("protocol"), m.get("name").toString());
        }
        throw new IOException("Missing fields for version: " + m);
    }
}
