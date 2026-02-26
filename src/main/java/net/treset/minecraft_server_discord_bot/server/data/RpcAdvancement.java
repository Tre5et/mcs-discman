package net.treset.minecraft_server_discord_bot.server.data;


import dev.treset.mcdl.servermanagement.vanilla.types.RpcPlayer;

public record RpcAdvancement(
        RpcPlayer player,
        RpcText message,
        String identifier,
        RpcText title,
        RpcText description,
        RpcText toast,
        Integer color
) {}
