package net.treset.minecraft_server_discord_bot.server.schemas;

public record RpcError(
        int code,
        String message,
        Object data
) {}
