package net.treset.minecraft_server_discord_bot.config.function;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.treset.minecraft_server_discord_bot.commands.Command;
import net.treset.minecraft_server_discord_bot.commands.Commands;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.ValidatableConfig;
import net.treset.minecraft_server_discord_bot.config.message.Message;
import net.treset.minecraft_server_discord_bot.config.message.MessageTemplates;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CommandConfig extends ValidatableConfig {
    public boolean enabled;
    public List<String> allowedRoles;
    public List<String> allowedChannels = List.of("default");
    public Message.Default disabledMessage;
    public Message.Default deniedMessage;
    public transient Set<Role> allowedJdaRoles;
    public transient Set<MessageChannel> allowedJdaChannels;
    
    public abstract CommandData commandData();
    public abstract Command<?> command();

    public CommandConfig(boolean enabled, List<String> allowedRoles) {
        this.enabled = enabled;
        this.allowedRoles = allowedRoles;
    }

    public boolean isAllowed(Member member) {
        if (!enabled || member == null) return false;
        if (allowedJdaRoles == null || allowedJdaRoles.isEmpty()) return true;

        return member.getRoles().stream()
                .anyMatch(role -> hasMatchingId(allowedJdaRoles, role.getIdLong()));
    }

    public boolean isCorrectChannel(MessageChannel channel) {
        if (channel == null) return false;
        if (allowedJdaChannels == null || allowedJdaChannels.isEmpty()) return true;

        return hasMatchingId(allowedJdaChannels, channel.getIdLong());
    }

    private static boolean hasMatchingId(Set<? extends ISnowflake> entries, long id) {
        return entries.stream().anyMatch(entry -> entry.getIdLong() == id);
    }

    @Override
    public List<String> prefix() {
        return List.of("feature");
    }

    @Override
    public void validate(Config newConfig) throws ConfigException {
        if(enabled && allowedRoles != null) {
            for(String role : allowedRoles) {
                if (!newConfig.discord.jdaRoles.containsKey(role)) {
                    throw new ConfigException("Role '" + role + "' is referenced for a function but is not defined in 'discord.roles'.");
                }
            }
            allowedJdaRoles = allowedRoles.stream()
                    .map(r -> newConfig.discord.jdaRoles.get(r))
                    .collect(Collectors.toSet());
        }

        for(String channel: allowedChannels) {
            if(!newConfig.discord.jdaChannels.containsKey(channel)) {
                throw new ConfigException("Channel '" + channel + "' is referenced for a function but is not defined in 'discord.channels'.");
            }
        }
        allowedJdaChannels = allowedChannels.stream()
                .map(c -> newConfig.discord.jdaChannels.get(c))
                .collect(Collectors.toSet());

        if(disabledMessage == null) disabledMessage = newConfig.commands.messageDisabled;
        disabledMessage.validate();
        if(deniedMessage == null) deniedMessage = newConfig.commands.messageDenied;
        deniedMessage.validate();
    }
    
    public Map.Entry<CommandData, Command<?>> validateAndGet(Config newConfig) throws ConfigException {
        validate(newConfig);
        return enabled ? Map.entry(commandData(), command()) : null;
    }

    public static abstract class EnabledAndAll extends CommandConfig {
        public EnabledAndAll() {
            super(true, null);
        }
    }

    public static abstract class DisabledAndAll extends CommandConfig {
        public DisabledAndAll() {
            super(false, null);
        }
    }

    public static abstract class EnabledAndModerator extends CommandConfig {
        public EnabledAndModerator() {
            super(true, List.of("moderator"));
        }
    }

    public static abstract class DisabledAndModerator extends CommandConfig {
        public DisabledAndModerator() {
            super(false, List.of("moderator"));
        }
    }

    public static class Active extends EnabledAndAll {
        public Message.Default messageActive = new Message.Default("The server is running");
        public Message.Default messageInactive = new Message.Default("The server is not running");

        @Override
        public CommandData commandData() {
            return new CommandData("active", "Check if the server is running!");
        }

        @Override
        public Command<?> command() {
            return Commands.ACTIVE;
        }

        @Override
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messageActive.validate();
            messageInactive.validate();
        }
    }

    public static class Backups extends EnabledAndAll {
        public Message<MessageTemplates.BackupsContext> messageLocationAndAdmin = new Message<>("Backups are available at **{location}**. For more information contact **{admin}**.");
        public Message<MessageTemplates.BackupsContext> messageLocation = new Message<>("Backups are available at **{location}**.");
        public Message<MessageTemplates.BackupsContext> messageAdmin = new Message<>("For more information contact **{admin}**.");
        public Message.Default messageNone = new Message.Default("No information about backups is configured.");

        @Override
        public CommandData commandData() {
            return new CommandData("backups", "See where to find backups!");
        }

        @Override
        public Command<?> command() {
            return Commands.BACKUPS;
        }

        @Override
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messageLocationAndAdmin.validate(MessageTemplates.BACKUPS);
            messageLocation.validate(MessageTemplates.BACKUPS);
            messageAdmin.validate(MessageTemplates.BACKUPS);
            messageNone.validate();
        }
    }

    public static class Connection extends EnabledAndModerator {
        public Message.Default messageStatusOpen = new Message.Default("A connection with the server is open.");
        public Message.Default messageStatusClosed = new Message.Default("No connection is open.");
        public Message.Default messageOpenAlreadyOpen = new Message.Default("The connection is already open. Close it first.");
        public Message.Default messageOpenFailed = new Message.Default("Failed to connect to server. Try again.");
        public Message.Default messageOpenSuccess = new Message.Default("Connected to server.");
        public Message.Default messageCloseNoConnection = new Message.Default("No connection is open. Open one first.");
        public Message.Default messageCloseForced = new Message.Default("Forcefully closed connection.");
        public Message.Default messageCloseSuccess = new Message.Default("Connection closed successfully.");
        public Message.Default messageCloseFailed = new Message.Default("Failed to close the connection. Try again.");
        public Message.Default messageUnknownAction = new Message.Default("Action not found.");

        @Override
        public CommandData commandData() {
            return new CommandData("connection", "Manage connection to server mod! [Moderator only]")
                    .addOptions(new OptionData(OptionType.STRING, "action", "The thing to do.", true)
                            .addChoice("status", "status").addChoice("open", "open").addChoice("close", "close"))
                    .addOption(OptionType.BOOLEAN, "force", "Force closing the connection; Does nothing if another action than close is selected", false);
        }

        @Override
        public Command<?> command() {
            return Commands.CONNECTION;
        }

        @Override
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messageStatusOpen.validate();
            messageStatusClosed.validate();
            messageOpenAlreadyOpen.validate();
            messageOpenFailed.validate();
            messageOpenSuccess.validate();
            messageCloseNoConnection.validate();
            messageCloseForced.validate();
            messageCloseSuccess.validate();
            messageCloseFailed.validate();
            messageUnknownAction.validate();
        }
    }

    public static class CreateBackup extends EnabledAndModerator {
        public Message.Default messageInvalidMode = new Message.Default("Invalid mode");

        @Override
        public CommandData commandData() {
            return new CommandData("createbackup", "Create a backup! [Moderator only]").addOptions(
                    new OptionData(OptionType.STRING, "mode", "The backup mode to use.", true)
                            .addChoice("restart", "restart").addChoice("while running", "while-running"),
                    new OptionData(OptionType.BOOLEAN, "skip-notify", "Don't notify the players and create the backup instantly")
            );
        }

        @Override
        public Command<?> command() {
            return Commands.CREATE_BACKUP;
        }

        @Override
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messageInvalidMode.validate();
        }
    }

    public static class Details extends EnabledAndAll {
        public Message.Default messageFailed = new Message.Default("Failed to get details!");
        public Message<MessageTemplates.DetailsContext> messageVersion = new Message<>("The server is running version **{version}**.");

        @Override
        public CommandData commandData() {
            return new CommandData("details", "See details about the server!");
        }

        @Override
        public Command<?> command() {
            return Commands.DETAILS;
        }

        @Override
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messageFailed.validate();
            messageVersion.validate(MessageTemplates.DETAILS);
        }
    }

    public static class Join extends EnabledAndAll {
        public Message<MessageTemplates.JoinContext> messageJoin = new Message<>("Join the server using the address **{url}**. You must be member to join. Type ``/members`` for more details.");
        public Message.Default messageMissingInfo = new Message.Default("No information about how to join the server is configured.");

        @Override
        public CommandData commandData() {
            return new CommandData("join", "See how to join the server!");
        }

        @Override
        public Command<?> command() {
            return Commands.JOIN;
        }

        @Override
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messageJoin.validate(MessageTemplates.JOIN);
            messageMissingInfo.validate();
        }
    }

    public static class Members extends EnabledAndAll {
        public Message.Default messageFailed = new Message.Default("Failed to get members!");
        public Message.Default messageNoMembers = new Message.Default("There are no current members.");
        public Message<MessageTemplates.MembersContext> messageMembers = new Message<>("Current members are: **{list}**.");
        public Message<MessageTemplates.MembersContext> messageContactAdmin = new Message<>("To become a member contact **{admin}**.");

        @Override
        public CommandData commandData() {
            return new CommandData("members", "See the current members of the server!");
        }

        @Override
        public Command<?> command() {
            return Commands.MEMBERS;
        }

        @Override
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messageFailed.validate();
            messageNoMembers.validate();
            messageMembers.validate(MessageTemplates.MEMBERS);
            messageContactAdmin.validate(MessageTemplates.MEMBERS);
        }
    }

    public static class Online extends EnabledAndAll {
        public Message.Default messageFailed = new Message.Default("Failed to get players!");
        public Message.Default messageNoPlayers = new Message.Default("There are no players online.");
        public Message<MessageTemplates.OnlineContext> messageSinglePlayer = new Message<>("There is **1** player online:**\n{list}**");
        public Message<MessageTemplates.OnlineContext> messageMultiplePlayers = new Message<>("There are **{count}** players online:**\n{list}**");

        @Override
        public CommandData commandData() {
            return new CommandData("online", "See who is currently online!");
        }

        @Override
        public Command<?> command() {
            return Commands.ONLINE;
        }

        @Override
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messageFailed.validate();
            messageNoPlayers.validate();
            messageSinglePlayer.validate(MessageTemplates.ONLINE);
            messageMultiplePlayers.validate(MessageTemplates.ONLINE);
        }
    }

    public static class Ping extends EnabledAndModerator {
        public Message.Default messagePong = new Message.Default("Pong!");

        @Override
        public CommandData commandData() {
            return new CommandData("ping", "See if the bot is online!");
        }

        @Override
        public Command<?> command() {
            return Commands.PING;
        }

        @Override
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messagePong.validate();
        }
    }

    public static class Reload extends EnabledAndModerator {
        public Message.Default messageReloaded = new Message.Default("Reloaded config.");
        public Message.Default messageFailed = new Message.Default("Failed to reload config.");

        @Override
        public CommandData commandData() {
            return new CommandData("reloadconfig", "Reloads the configuration from a file! [Moderator only]");
        }

        @Override
        public Command<?> command() {
            return Commands.RELOAD;
        }

        @Override
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messageReloaded.validate();
            messageFailed.validate();
        }
    }

    public static class Restart extends EnabledAndModerator {
        public Message.Default messageStopping = new Message.Default("Stopping the server for a restart...");
        public Message.Default messageStopFailed = new Message.Default("Server stop failed.");
        public Message.Default messageStopped = new Message.Default("Server stopped, restarting... (this may take a few minutes)");
        public Message.Default messageRestarting = new Message.Default("Restarting... (this may take a few minutes)");
        public Message.Default messageRestarted = new Message.Default("Server restarted!");
        public Message.Default messageRestartFailed = new Message.Default("Failed to restart server");

        @Override
        public CommandData commandData() {
            return new CommandData("restartserver", "Restart the server! [Moderator only]");
        }

        @Override
        public Command<?> command() {
            return Commands.RESTART;
        }

        @Override
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messageStopping.validate();
            messageStopFailed.validate();
            messageStopped.validate();
            messageRestarting.validate();
            messageRestarted.validate();
            messageRestartFailed.validate();
        }
    }

    public static class RunCommand extends EnabledAndModerator {
        public Message<MessageTemplates.RunCommandContext> messageSuccess = new Message<>("Success: {response}");
        public Message<MessageTemplates.RunCommandContext> messageInvalid = new Message<>("Invalid command: {response}");
        public Message<MessageTemplates.RunCommandContext> messageNoResponse = new Message<>("Ran: {response}");
        public Message.Default messageFailed = new Message.Default("Failed to run command.");
        public Message.Default messageRequestFailed = new Message.Default("Failed to request command execution.");

        @Override
        public CommandData commandData() {
            return new CommandData("runcommand", "Run a command on the server! [Moderator only]")
                    .addOption(OptionType.STRING, "command", "The command to run.", true);
        }

        @Override
        public Command<?> command() {
            return Commands.RUN_COMMAND;
        }

        @Override
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messageSuccess.validate(MessageTemplates.RUN_COMMAND);
            messageInvalid.validate(MessageTemplates.RUN_COMMAND);
            messageNoResponse.validate(MessageTemplates.RUN_COMMAND);
            messageFailed.validate();
            messageRequestFailed.validate();
        }
    }

    public static class Say extends EnabledAndModerator {
        @Override
        public CommandData commandData() {
            return new CommandData("say", "Make the bot say something! [Moderator only]")
                    .addOption(OptionType.STRING, "message", "The message the bot will say.", true);
        }

        @Override
        public Command<?> command() {
            return Commands.SAY;
        }
    }

    public static class Start extends EnabledAndModerator {
        public Message.Default messageAlreadyRunning = new Message.Default("Server is already running.");
        public Message.Default messageStarting = new Message.Default("Starting the server... (this may take a few minutes)");
        public Message.Default messageStarted = new Message.Default("Started!");
        public Message.Default messageStartFailed = new Message.Default("Failed to start server");

        @Override
        public CommandData commandData() {
            return new CommandData("startserver", "Start the server! [Moderator only]");
        }

        @Override
        public Command<?> command() {
            return Commands.START;
        }

        @Override
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messageAlreadyRunning.validate();
            messageStarting.validate();
            messageStarted.validate();
            messageStartFailed.validate();
        }
    }

    public static class Stop extends EnabledAndModerator {
        public Message.Default messageAlreadyStopped = new Message.Default("Server is already stopped.");
        public Message.Default messageStopping = new Message.Default("Stopping the server...");
        public Message.Default messageStopFailed = new Message.Default("Failed to stop server.");
        public Message.Default messageStopped = new Message.Default("Server stopped.");

        @Override
        public CommandData commandData() {
            return new CommandData("stopserver", "Start the server! [Moderator only]");
        }

        @Override
        public Command<?> command() {
            return Commands.STOP;
        }

        @Override
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messageAlreadyStopped.validate();
            messageStopping.validate();
            messageStopFailed.validate();
            messageStopped.validate();
        }
    }
}
