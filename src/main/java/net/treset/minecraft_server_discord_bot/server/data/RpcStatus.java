package net.treset.minecraft_server_discord_bot.server.data;

import dev.treset.mcdl.servermanagement.request.RpcResponse;

import java.io.IOException;

public class RpcStatus {
    boolean started;
    RpcVersion version;

    public boolean isStarted() {
        return started;
    }

    public RpcVersion getVersion() {
        return version;
    }

    public static RpcStatus from(RpcResponse res) throws IOException {
        if(!res.hasResult()) {
            throw new IOException("Failed to get status: Error: " + res.error().message());
        }
        return res.resultAs(RpcStatus.class);
    }
}
