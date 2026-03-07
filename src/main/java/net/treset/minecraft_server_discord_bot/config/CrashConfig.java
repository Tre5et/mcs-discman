package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.util.List;

public class CrashConfig extends ValidatableConfig {
    public int maxRetries = 5;
    public int recentTimeout = 3600;

    @Override
    public List<String> prefix() {
        return List.of("crash");
    }

    @Override
    public void validate(Config newConfig) throws ConfigException {

    }
}
