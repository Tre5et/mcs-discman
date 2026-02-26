package net.treset.minecraft_server_discord_bot.server.data;

import dev.treset.mcdl.servermanagement.vanilla.types.RpcPlayer;

public record RpcDeath(
        RpcPlayer player,
        RpcText message
) {}

