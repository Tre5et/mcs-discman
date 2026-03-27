package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.util.List;
import java.util.Map;

public class DiscordConfig extends ValidatableConfig {
    public String token;
    public String guildId;
    public String messageChannelId;
    public Map<String, String> roles;
    public int updateInterval = 10;
    public boolean debug = false;
    public String admin;


    @Override
    public List<String> prefix() {
        return List.of("discord");
    }

    @Override
    public void validate(Config newConfig) throws ConfigException {
        require(token, "token");
        require(guildId, "guildId");
        require(messageChannelId, "messageChannelId");
    }
}
