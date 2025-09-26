package net.treset.minecraft_server_discord_bot.rpc.schemas;

public interface RpcResponse {
    String jsonrpc();
    Integer id();
    Object result();
    RpcError error();
}
