package net.treset.minecraft_server_discord_bot.commands;

import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.function.FunctionConfig;

public interface Commands {
    Command<FunctionConfig.Active> ACTIVE = new ActiveCommand(() -> Config.get().functions.active);
    Command<FunctionConfig.AutoBackup> AUTOBACKUP = new AutoBackupCommand(() -> Config.get().functions.autoBackup);
    Command<FunctionConfig.Backups> BACKUPS = new BackupsCommand(() -> Config.get().functions.backups);
    Command<FunctionConfig.Connection> CONNECTION = new ConnectionCommand(() -> Config.get().functions.connection);
    Command<FunctionConfig.CreateBackup> CREATE_BACKUP = new CreateBackupCommand(() -> Config.get().functions.createBackup);
    Command<FunctionConfig.Details> DETAILS = new DetailsCommand(() -> Config.get().functions.details);
    Command<FunctionConfig.Join> JOIN = new JoinCommand(() -> Config.get().functions.join);
    Command<FunctionConfig.Members> MEMBERS = new MembersCommand(() -> Config.get().functions.members);
    Command<FunctionConfig.Online> ONLINE = new OnlineCommand(() -> Config.get().functions.online);
    Command<FunctionConfig.Ping> PING = new PingCommand(() -> Config.get().functions.ping);
    Command<FunctionConfig.Reload> RELOAD = new ReloadConfigCommand(() -> Config.get().functions.reload);
    Command<FunctionConfig.Restart> RESTART = new RestartServerCommand(() -> Config.get().functions.restart);
    Command<FunctionConfig.RunCommand> RUN_COMMAND = new RunCommandCommand(() -> Config.get().functions.runCommand);
    Command<FunctionConfig.Say> SAY = new SayCommand(() -> Config.get().functions.say);
    Command<FunctionConfig.Start> START = new StartServerCommand(() -> Config.get().functions.start);
    Command<FunctionConfig.Stop> STOP = new StopServerCommand(() -> Config.get().functions.stop);
}

