package net.treset.minecraft_server_discord_bot.config.function;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.ValidatableConfig;
import net.treset.minecraft_server_discord_bot.config.message.Message;
import net.treset.minecraft_server_discord_bot.config.message.MessageTemplates;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FunctionConfig extends ValidatableConfig {
    public boolean enabled;
    public List<String> allowedRoles;
    public Message.Default disabledMessage = new Message.Default("This feature is not enabled.");
    public Message.Default deniedMessage = new Message.Default("You don't have permission to do that.");
    public transient Set<String> allowedRolesIds = null;

    public FunctionConfig(boolean enabled, List<String> allowedRoles) {
        this.enabled = enabled;
        this.allowedRoles = allowedRoles;
    }

    public boolean isAllowed(Member member) {
        if (!enabled) return false;
        if (allowedRolesIds == null || allowedRolesIds.isEmpty()) return true;

        return member.getRoles().stream()
                .map(ISnowflake::getId)
                .anyMatch(allowedRolesIds::contains);
    }

    @Override
    public List<String> prefix() {
        return List.of("feature");
    }

    @Override
    public void validate(Config newConfig) throws ConfigException {
        if(enabled && allowedRoles != null) {
            for(String role : allowedRoles) {
                if (!newConfig.discord.roles.containsKey(role)) {
                    throw new ConfigException("Role '" + role + "' is referenced for a function but is not defined in 'discord.roles'.");
                }
            }
            allowedRolesIds = allowedRoles.stream()
                    .map(r -> newConfig.discord.roles.get(r))
                    .collect(Collectors.toSet());
        }
        disabledMessage.validate();
        deniedMessage.validate();
    }

    public static class EnabledAndAll extends FunctionConfig {
        public EnabledAndAll() {
            super(true, null);
        }
    }

    public static class DisabledAndAll extends FunctionConfig {
        public DisabledAndAll() {
            super(false, null);
        }
    }

    public static class EnabledAndModerator extends FunctionConfig {
        public EnabledAndModerator() {
            super(true, List.of("moderator"));
        }
    }

    public static class DisabledAndModerator extends FunctionConfig {
        public DisabledAndModerator() {
            super(false, List.of("moderator"));
        }
    }

    public static class Active extends EnabledAndAll {
        public Message.Default messageActive = new Message.Default("The server is running");
        public Message.Default messageInactive = new Message.Default("The server is not running");

        @Override
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messageActive.validate();
            messageInactive.validate();
        }
    }

    public static class AutoBackup extends EnabledAndModerator {
        public Message.Default messageEnabled = new Message.Default("Enabled Auto-Backup");
        public Message.Default messageDisabled = new Message.Default("Disabled Auto-Backup");
        public Message.Default messageAlreadyEnabled = new Message.Default("Auto-Backup is already enabled.");
        public Message.Default messageAlreadyDisabled = new Message.Default("Auto-Backup is already disabled.");

        @Override
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messageEnabled.validate();
            messageDisabled.validate();
            messageAlreadyEnabled.validate();
            messageAlreadyDisabled.validate();
        }
    }

    public static class Backups extends EnabledAndAll {
        public Message<MessageTemplates.BackupsContext> messageLocationAndAdmin = new Message<>("Backups are available at **{location}**. For more information contact **{admin}**.");
        public Message<MessageTemplates.BackupsContext> messageLocation = new Message<>("Backups are available at **{location}**.");
        public Message<MessageTemplates.BackupsContext> messageAdmin = new Message<>("For more information contact **{admin}**.");
        public Message.Default messageNone = new Message.Default("No information about backups is configured.");

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
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messageInvalidMode.validate();
        }
    }

    public static class Details extends EnabledAndAll {
        public Message.Default messageFailed = new Message.Default("Failed to get details!");
        public Message<MessageTemplates.DetailsContext> messageVersion = new Message<>("The server is running version **{version}**.");

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
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messagePong.validate();
        }
    }

    public static class Reload extends EnabledAndModerator {
        public Message.Default messageReloaded = new Message.Default("Reloaded config.");
        public Message.Default messageFailed = new Message.Default("Failed to reload config.");

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
    }

    public static class Start extends EnabledAndModerator {
        public Message.Default messageAlreadyRunning = new Message.Default("Server is already running.");
        public Message.Default messageStarting = new Message.Default("Starting the server... (this may take a few minutes)");
        public Message.Default messageStarted = new Message.Default("Started!");
        public Message.Default messageStartFailed = new Message.Default("Failed to start server");

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
        public void validate(Config newConfig) throws ConfigException {
            super.validate(newConfig);
            messageAlreadyStopped.validate();
            messageStopping.validate();
            messageStopFailed.validate();
            messageStopped.validate();
        }
    }
}
