package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.util.List;

public class ServerConfig extends ValidatableConfig {
    public String host;
    public int port;
    public String secret;
    public boolean ssl = false;
    public String worldPath;
    public List<String> startCommand;
    public String commandDirectory = ".";
    public int restartDelay = 5;
    public int saveTimeout = 10;
    public int stopTimeout = 30;
    public int startTimeout = 600;
    public String url;


    @Override
    public List<String> prefix() {
        return List.of("server");
    }

    @Override
    public void validate(Config newConfig) throws ConfigException {
        require(host, "host");
        require(port, "port");
        require(secret, "secret");
        require(worldPath, "worldPath");
        require(startCommand, "startCommand");
    }
}
