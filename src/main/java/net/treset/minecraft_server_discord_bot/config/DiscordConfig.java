package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.tools.FormatTools;

import java.io.IOException;

public class DiscordConfig extends BaseConfig {
    public final String token;
    public final String guild_id;
    public final String message_channel_id;
    public final String moderator_role_id;
    public final int update_interval;
    public final boolean debug;


    public DiscordConfig(String config) throws IOException {
        super(config);

        token = load("token");
        guild_id = load("guild_id");
        message_channel_id = load("message_channel_id");
        moderator_role_id = load("moderator_role_id");

        if(token == null || guild_id == null || message_channel_id == null ||  moderator_role_id == null) {
            throw new IOException("Invalid discord config. Options 'token', 'guild_id', 'message_channel_id' and 'moderator_role_id' must be set.");
        }

        update_interval = FormatTools.stringToInt(load("update_interval"), 10);
        debug = FormatTools.stringToBoolean(load("debug"));
    }
}
