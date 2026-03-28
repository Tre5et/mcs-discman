package net.treset.minecraft_server_discord_bot.config.function;

import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.ValidatableConfig;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.util.List;

public class FunctionsConfig extends ValidatableConfig {
    public FunctionConfig.Active active = new FunctionConfig.Active();
    public FunctionConfig.AutoBackup autoBackup = new FunctionConfig.AutoBackup();
    public FunctionConfig.Backups backups = new FunctionConfig.Backups();
    public FunctionConfig.Connection connection = new FunctionConfig.Connection();
    public FunctionConfig.CreateBackup createBackup = new FunctionConfig.CreateBackup();
    public FunctionConfig.Details details = new FunctionConfig.Details();
    public FunctionConfig.Join join = new FunctionConfig.Join();
    public FunctionConfig.Members members = new FunctionConfig.Members();
    public FunctionConfig.Online online = new FunctionConfig.Online();
    public FunctionConfig.Ping ping = new FunctionConfig.Ping();
    public FunctionConfig.Reload reload = new FunctionConfig.Reload();
    public FunctionConfig.Restart restart = new FunctionConfig.Restart();
    public FunctionConfig.RunCommand runCommand = new FunctionConfig.RunCommand();
    public FunctionConfig.Say say = new FunctionConfig.Say();
    public FunctionConfig.Start start = new FunctionConfig.Start();
    public FunctionConfig.Stop stop = new FunctionConfig.Stop();

    @Override
    public List<String> prefix() {
        return List.of("functions");
    }

    @Override
    public void validate(Config newConfig) throws ConfigException {
        active.validate(newConfig);
        autoBackup.validate(newConfig);
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
