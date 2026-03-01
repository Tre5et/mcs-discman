package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.util.List;

public class DiscordConfig extends Validatable {
    public String token;
    public String guildId;
    public String messageChannelId;
    public String moderatorRoleId;
    public int updateInterval = 10;
    public boolean debug = false;
    public String admin;


    @Override
    public List<String> prefix() {
        return List.of("discord");
    }

    @Override
    public void validate() throws ConfigException {
        require(token, "token");
        require(guildId, "guildId");
        require(messageChannelId, "messageChannelId");
        require(moderatorRoleId, "moderatorRoleId");
    }
}
