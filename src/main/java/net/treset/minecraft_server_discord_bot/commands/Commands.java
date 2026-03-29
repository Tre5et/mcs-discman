package net.treset.minecraft_server_discord_bot.commands;

import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;

public interface Commands {
    Command<CommandConfig.Active> ACTIVE = new ActiveCommand(() -> Config.get().commands.active);
    Command<CommandConfig.Backups> BACKUPS = new BackupsCommand(() -> Config.get().commands.backups);
    Command<CommandConfig.Connection> CONNECTION = new ConnectionCommand(() -> Config.get().commands.connection);
    Command<CommandConfig.CreateBackup> CREATE_BACKUP = new CreateBackupCommand(() -> Config.get().commands.createBackup);
    Command<CommandConfig.Details> DETAILS = new DetailsCommand(() -> Config.get().commands.details);
    Command<CommandConfig.Join> JOIN = new JoinCommand(() -> Config.get().commands.join);
    Command<CommandConfig.Members> MEMBERS = new MembersCommand(() -> Config.get().commands.members);
    Command<CommandConfig.Online> ONLINE = new OnlineCommand(() -> Config.get().commands.online);
    Command<CommandConfig.Ping> PING = new PingCommand(() -> Config.get().commands.ping);
    Command<CommandConfig.Reload> RELOAD = new ReloadConfigCommand(() -> Config.get().commands.reload);
    Command<CommandConfig.Restart> RESTART = new RestartServerCommand(() -> Config.get().commands.restart);
    Command<CommandConfig.RunCommand> RUN_COMMAND = new RunCommandCommand(() -> Config.get().commands.runCommand);
    Command<CommandConfig.Say> SAY = new SayCommand(() -> Config.get().commands.say);
    Command<CommandConfig.Start> START = new StartServerCommand(() -> Config.get().commands.start);
    Command<CommandConfig.Stop> STOP = new StopServerCommand(() -> Config.get().commands.stop);
}

