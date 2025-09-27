package net.treset.minecraft_server_discord_bot.rpc.data;

import java.io.IOException;
import java.util.Map;

public record RpcStatus(
        boolean started,
        RpcVersion version
) {
    public static RpcStatus from(Object o) throws IOException {
        if(!(o instanceof Map<?,?> m)) {
            throw new IOException("Invalid status data format: " + o);
        }
        if(m.containsKey("started") && m.get("started") instanceof Boolean && m.containsKey("version")) {
            return new RpcStatus(
                    (boolean)m.get("started"),
                    RpcVersion.from(m.get("version"))
            );
        }
        throw new IOException("Missing fields for status: " + m);
    }
}
