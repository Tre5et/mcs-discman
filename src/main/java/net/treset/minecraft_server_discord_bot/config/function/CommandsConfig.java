package net.treset.minecraft_server_discord_bot.config.function;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.treset.minecraft_server_discord_bot.commands.Command;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.ValidatableConfig;
import net.treset.minecraft_server_discord_bot.config.message.Message;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class CommandsConfig extends ValidatableConfig {
    public Message.Default messageUnknown = new Message.Default("This command is not supported.");
    public Message.Default messageDenied = new Message.Default("You are not allowed to do that.");
    public Message.Default messageDisabled = new Message.Default("This command is not enabled.");

    public CommandConfig.Active active = new CommandConfig.Active();
    public CommandConfig.Backups backups = new CommandConfig.Backups();
    public CommandConfig.Connection connection = new CommandConfig.Connection();
    public CommandConfig.CreateBackup createBackup = new CommandConfig.CreateBackup();
    public CommandConfig.Details details = new CommandConfig.Details();
    public CommandConfig.Join join = new CommandConfig.Join();
    public CommandConfig.Members members = new CommandConfig.Members();
    public CommandConfig.Online online = new CommandConfig.Online();
    public CommandConfig.Ping ping = new CommandConfig.Ping();
    public CommandConfig.Reload reload = new CommandConfig.Reload();
    public CommandConfig.Restart restart = new CommandConfig.Restart();
    public CommandConfig.RunCommand runCommand = new CommandConfig.RunCommand();
    public CommandConfig.Say say = new CommandConfig.Say();
    public CommandConfig.Start start = new CommandConfig.Start();
    public CommandConfig.Stop stop = new CommandConfig.Stop();
    public CommandConfig.Reminder reminder = new CommandConfig.Reminder();

    public transient Map<String, Command<?>> commands;

    public void handleCommand(SlashCommandInteraction event) {
        event.deferReply().queue();

        if(commands.containsKey(event.getName())) {
            commands.get(event.getName()).handle(event);
        } else {
            Logger.warn("Received unknown command '" + event.getName() + "'.");
            event.getHook().sendMessage(messageUnknown.get()).queue();
        }
    }

    @Override
    public List<String> prefix() {
        return List.of("functions");
    }

    @Override
    public void validate(Config newConfig) throws ConfigException {
        messageUnknown.validate();
        messageDenied.validate();
        messageDisabled.validate();

        Set<Command<?>> updatedCommands = new HashSet<>();
        updatedCommands.add(active.validateAndGet(newConfig));
        updatedCommands.add(backups.validateAndGet(newConfig));
        updatedCommands.add(connection.validateAndGet(newConfig));
        updatedCommands.add(createBackup.validateAndGet(newConfig));
        updatedCommands.add(details.validateAndGet(newConfig));
        updatedCommands.add(join.validateAndGet(newConfig));
        updatedCommands.add(members.validateAndGet(newConfig));
        updatedCommands.add(online.validateAndGet(newConfig));
        updatedCommands.add(ping.validateAndGet(newConfig));
        updatedCommands.add(reload.validateAndGet(newConfig));
        updatedCommands.add(restart.validateAndGet(newConfig));
        updatedCommands.add(runCommand.validateAndGet(newConfig));
        updatedCommands.add(say.validateAndGet(newConfig));
        updatedCommands.add(start.validateAndGet(newConfig));
        updatedCommands.add(stop.validateAndGet(newConfig));
        updatedCommands.add(reminder.validateAndGet(newConfig));

        Map<Command<?>, CommandData> commands = updatedCommands.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(c -> c, Command::data));

        this.commands = commands.entrySet().stream()
                .collect(Collectors.toMap(c -> c.getValue().getName(), Map.Entry::getKey));

        newConfig.discord.jdaGuild.updateCommands()
                .addCommands(commands.values()).queue();
    }
}
