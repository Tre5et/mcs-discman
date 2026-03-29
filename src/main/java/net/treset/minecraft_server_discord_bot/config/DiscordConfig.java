package net.treset.minecraft_server_discord_bot.config;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordConfig extends ValidatableConfig {
    public String token;
    public Long guild;
    public Map<String, Long> channels;
    public Map<String, Long> roles;
    public boolean debug = false;
    public String admin;

    public transient Guild jdaGuild;
    public transient Map<String, MessageChannel> jdaChannels;
    public transient Map<String, Role> jdaRoles;

    @Override
    public List<String> prefix() {
        return List.of("discord");
    }

    @Override
    public void validate(Config newConfig) throws ConfigException {
        require(token, "token");
        require(guild, "guild");
        require(channels.get("default"), "channels.default");
        require(roles.get("moderator"), "roles.moderator");

        if(DiscordBot.JDA == null) {
            try {
                DiscordBot.initClient(token);
            } catch (LoginException | InterruptedException e) {
                throw new ConfigException("Failed to create discord client", e);
            }
        }

        jdaGuild = DiscordBot.JDA.getGuildById(guild);
        if(jdaGuild == null) throw new ConfigException("Could not find guild for id '" + guild + "'");

        jdaChannels = new HashMap<>();
        for(Map.Entry<String, Long> e : channels.entrySet()) {
            MessageChannel c = DiscordBot.JDA.getTextChannelById(e.getValue());
            if(c == null) throw new ConfigException("Could not find channel '" + e.getKey() + "' with id '" + e.getValue() + "'.");
            jdaChannels.put(e.getKey(), c);
        }

        jdaRoles = new HashMap<>();
        for(Map.Entry<String, Long> e : roles.entrySet()) {
            Role r = DiscordBot.JDA.getRoleById(e.getValue());
            if(r == null) throw new ConfigException("Could not find role '" + e.getKey() + "' with id '" + e.getValue() + "'.");
            jdaRoles.put(e.getKey(), r);
        }
    }
}
