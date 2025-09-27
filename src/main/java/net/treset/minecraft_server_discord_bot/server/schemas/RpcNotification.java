package net.treset.minecraft_server_discord_bot.server.schemas;

import java.util.List;

public interface RpcNotification {
    String jsonrpc();
    String method();
    List<Object> params();
}
