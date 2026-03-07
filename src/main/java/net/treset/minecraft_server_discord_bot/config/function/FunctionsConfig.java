package net.treset.minecraft_server_discord_bot.config.function;

import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.ValidatableConfig;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.util.List;

public class FunctionsConfig extends ValidatableConfig {
    public FunctionConfig.EnabledAndAll active = new FunctionConfig.EnabledAndAll();
    public FunctionConfig.EnabledAndAll backups = new FunctionConfig.EnabledAndAll();
    public FunctionConfig.EnabledAndModerator connection = new FunctionConfig.EnabledAndModerator();
    public FunctionConfig.EnabledAndModerator createBackup = new FunctionConfig.EnabledAndModerator();
    public FunctionConfig.EnabledAndAll details = new FunctionConfig.EnabledAndAll();
    public FunctionConfig.EnabledAndAll join = new FunctionConfig.EnabledAndAll();
    public FunctionConfig.EnabledAndAll members = new FunctionConfig.EnabledAndAll();
    public FunctionConfig.EnabledAndAll online = new FunctionConfig.EnabledAndAll();
    public FunctionConfig.EnabledAndModerator ping = new FunctionConfig.EnabledAndModerator();
    public FunctionConfig.EnabledAndModerator reload = new FunctionConfig.EnabledAndModerator();
    public FunctionConfig.EnabledAndModerator restart = new FunctionConfig.EnabledAndModerator();
    public FunctionConfig.EnabledAndModerator runCommand = new FunctionConfig.EnabledAndModerator();
    public FunctionConfig.EnabledAndModerator say = new FunctionConfig.EnabledAndModerator();
    public FunctionConfig.EnabledAndModerator start = new FunctionConfig.EnabledAndModerator();
    public FunctionConfig.EnabledAndModerator stop = new FunctionConfig.EnabledAndModerator();

    @Override
    public List<String> prefix() {
        return List.of("functions");
    }

    @Override
    public void validate(Config newConfig) throws ConfigException {
        active.validate(newConfig);
        backups.validate(newConfig);
        connection.validate(newConfig);
        createBackup.validate(newConfig);
        details.validate(newConfig);
        join.validate(newConfig);
        members.validate(newConfig);
        online.validate(newConfig);
        ping.validate(newConfig);
        reload.validate(newConfig);
        restart.validate(newConfig);
        runCommand.validate(newConfig);
        say.validate(newConfig);
        start.validate(newConfig);
        stop.validate(newConfig);
    }
}
