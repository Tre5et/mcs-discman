package net.treset.minecraft_server_discord_bot.server.data;

import java.util.List;

public record RpcText(
        String literal,
        String key,
        List<RpcText> args
) {}
