package net.treset.minecraft_server_discord_bot.config.event;

import dev.treset.mcdl.servermanagement.vanilla.RpcMethods;
import dev.treset.mcdl.servermanagement.vanilla.types.RpcMessage;
import dev.treset.mcdl.servermanagement.vanilla.types.RpcPlayer;
import dev.treset.mcdl.servermanagement.vanilla.types.RpcSystemMessage;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.ValidatableConfig;
import net.treset.minecraft_server_discord_bot.config.message.Message;
import net.treset.minecraft_server_discord_bot.config.message.MessageTemplate;
import net.treset.minecraft_server_discord_bot.config.message.MessageTemplates;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.schedulers.EventScheduler;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;
import net.treset.minecraft_server_discord_bot.server.data.RpcAdvancement;
import net.treset.minecraft_server_discord_bot.server.data.RpcDeath;
import net.treset.minecraft_server_discord_bot.upload.UploadService;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class EventConfig<C> extends ValidatableConfig {
    public EventCondition discord;
    public EventCondition game;
    public boolean isActivity;
    public Message<C> message;
    public List<String> channels = List.of("default");

    public transient Set<GuildMessageChannel> jdaChannels;

    public EventConfig(EventCondition discord, EventCondition game, String message) {
        this.discord = discord;
        this.game = game;
        this.message = new Message<>(message);
    }

    public String message(C source) {
        return message.get(source);
    }

    public void send(C source, EventDiscordOutput output, boolean success) {
        if(isActivity) {
            EventScheduler.eventOccurred();
        }

        String result = message(source);
        if(discord.shouldSend(success)) {
            output.output(result, jdaChannels);
        }
        if(game.shouldSend(success) && ManagementClient.get() != null && ManagementClient.get().isConnected()) {
            ManagementClient.get().send(
                    RpcMethods.Server.SYSTEM_MESSAGE,
                    new RpcSystemMessage(null, false, new RpcMessage(null, null, "[Discman] " + result)),
                    r -> {},
                    e -> Logger.warn(e, "Failed to send message '%s' to server", result)
            );
        }
    }

    public void send(C source, EventDiscordOutput output) {
        send(source, output, true);
    }

    public void send(C source, boolean success) {
        send(source, new EventDiscordOutput.Message());
    }

    public void send(C source) {
        send(source, true);
    }

    @Override
    public List<String> prefix() {
        return List.of("notification");
    }

    public void validate(Config config, MessageTemplate<C> template) throws ConfigException {
        message.validate(template);

        for(String c : channels) {
            if(!config.discord.jdaChannels.containsKey(c)) {
                throw new ConfigException("Channel '" + c + "' is referenced in an event but is not defined in 'discord.channels'.");
            }
        }
        jdaChannels = channels.stream()
                .map(c -> config.discord.jdaChannels.get(c))
                .collect(Collectors.toSet());
    }

    public static class DateTime extends EventConfig<Object> {
        public DateTime(EventCondition discord, EventCondition game, String message) {
            super(discord, game, message);
        }

        public String message() {
            return message(null);
        }

        public void send(EventDiscordOutput output, boolean success) {
            send(null, output, success);
        }

        public void send(EventDiscordOutput output) {
            send(output, true);
        }

        public void send(boolean success) {
            send(new EventDiscordOutput.Message(), success);
        }

        public void send() {
            send(true);
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.DATE_TIME);
        }
    }

    public static class Always extends EventConfig.DateTime {
        public Always(String message) {
            super(EventCondition.always, EventCondition.always, message);
        }
    }

    public static class Never extends EventConfig.DateTime{
        public Never(String message) {
            super(EventCondition.never, EventCondition.never, message);
        }
    }

    public static class AlwaysAndNever extends EventConfig.DateTime {
        public AlwaysAndNever(String message) {
            super(EventCondition.always, EventCondition.never, message);
        }
    }

    public static class NeverAndAlways extends EventConfig.DateTime {
        public NeverAndAlways(String message) {
            super(EventCondition.never, EventCondition.always, message);
        }
    }

    public static class Online extends AlwaysAndNever {
        public Online() {
            super("Hi, I'm online now.");
        }
    }

    public static class Joined extends EventConfig<RpcPlayer> {
        public Joined() {
            super(EventCondition.always, EventCondition.never, "{name} joined the game.");
            isActivity = true;
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.PLAYER);
        }
    }

    public static class Left extends EventConfig<RpcPlayer> {
        public Left() {
            super(EventCondition.always, EventCondition.never, "{name} left the game.");
            isActivity = true;
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.PLAYER);
        }
    }

    public static class Advancement extends EventConfig<RpcAdvancement> {
        public Advancement() {
            super(EventCondition.always, EventCondition.never, "{message}.");
            isActivity = true;
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.ADVANCEMENT);
        }
    }

    public static class Death extends EventConfig<RpcDeath> {
        public Death() {
            super(EventCondition.always, EventCondition.never, "{message}.");
            isActivity = true;
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.DEATH);
        }
    }

    public static class Started extends Never {
        public Started() {
            super("Server started.");
            isActivity = true;
        }
    }

    public static class StartFailed extends AlwaysAndNever {
        public StartFailed() {
            super("Failed to start server.");
            isActivity = true;
        }
    }

    public static class Stopping extends Never {
        public Stopping() {
            super("Server is stopping...");
            isActivity = true;
        }
    }

    public static class Stopped extends Never {
        public Stopped() {
            super("Server stopped.");
            isActivity = true;
        }
    }

    public static class BackupNotConfigured extends Always {
        public BackupNotConfigured() {
            super("Backup is not configured.");
        }
    }

    public static class BackupPreparationFailed extends Always {
        public BackupPreparationFailed() {
            super("Failed to prepare for backup. Aborting!");
        }
    }

    public static class BackupPreparationUndoFailed extends Always {
        public BackupPreparationUndoFailed() {
            super("Failed to undo backup preparation. The server may not save correctly!");
        }
    }

    public static class BackupServerStarting extends AlwaysAndNever {
        public BackupServerStarting() {
            super("Starting server after backup...");
        }
    }

    public static class BackupServerStarted extends Always {
        public BackupServerStarted() {
            super("Started server after backup.");
        }
    }

    public static class BackupStarted extends Always {
        public BackupStarted() {
            super("Creating backup...");
        }
    }

    public static class BackupCompleted extends Always {
        public BackupCompleted() {
            super("Backup created.");
        }
    }

    public static class BackupFailed extends Always {
        public BackupFailed() {
            super("Failed to create backup!");
        }
    }

    public static class BackupUploadDisabled extends Always {
        public BackupUploadDisabled() {
            super("Created backup successfully. Upload is disabled.");
        }
    }

    public static class BackupUploading extends EventConfig<UploadService> {

        public BackupUploading() {
            super(EventCondition.always, EventCondition.always, "Uploading backup to {service}...");
        }
        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.UPLOAD_SERVICE);
        }

    }

    public static class BackupUploadFailed extends EventConfig<UploadService> {
        public BackupUploadFailed() {
            super(EventCondition.always, EventCondition.always, "Failed to upload backup to {service}. The local backup was created.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.UPLOAD_SERVICE);
        }
    }

    public static class BackupWhileRunningAnnouncement extends EventConfig<Duration> {
        public BackupWhileRunningAnnouncement() {
            super(EventCondition.never, EventCondition.always, "Creating backup in {duration}.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.DURATION);
        }
    }

    public static class BackupRestartAnnouncement extends EventConfig<Duration> {
        public BackupRestartAnnouncement() {
            super(EventCondition.never, EventCondition.always, "Restarting server in {duration}.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.DURATION);
        }
    }

    public static class BackupAnnouncing extends EventConfig<Duration> {
        public BackupAnnouncing() {
            super(EventCondition.always, EventCondition.never, "Announcing backup to players (performing in {duration}).");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.DURATION);
        }
    }

    public static class BackupAutosaveDisabled extends NeverAndAlways {
        public BackupAutosaveDisabled() {
            super("Disabled autosave for backup.");
        }
    }

    public static class BackupAutosaveEnabled extends NeverAndAlways {
        public BackupAutosaveEnabled() {
            super("Enabled autosave after backup.");
        }
    }

    public static class BackupSaving extends NeverAndAlways {
        public BackupSaving() {
            super("Saving before backup...");
        }
    }

    public static class BackupSaved extends NeverAndAlways {
        public BackupSaved() {
            super("Save complete.");
        }
    }

    public static class BackupServerStopping extends AlwaysAndNever {
        public BackupServerStopping() {
            super("Stopping server for backup...");
        }
    }
    public static class BackupServerStopped extends AlwaysAndNever {
        public BackupServerStopped() {
            super("Stopped server for backup.");
        }
    }

    public static class Inactive extends EventConfig<Duration> {
        public Inactive() {
            super(EventCondition.always, EventCondition.never, "The server has been inactive for {duration}. Consider stopping it.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            message.validate(MessageTemplates.DURATION);
        }
    }

    public static class CrashDetected extends AlwaysAndNever {
        public CrashDetected() {
            super("Unexpected server stop detected. Confirming server has stopped...");
            isActivity = true;
        }
    }

    public static class CrashConfirmed extends AlwaysAndNever {
        public CrashConfirmed() {
            super("Unexpected stop confirmed.");
            isActivity = true;
        }
    }

    public static class CrashTooMany extends AlwaysAndNever {
        public CrashTooMany() {
            super("Too many crashes recently. Not attempting to restart.");
            isActivity = true;
        }
    }

    public static class CrashRestarting extends AlwaysAndNever {
        public CrashRestarting() {
            super("Attempting to restart server...");
            isActivity = true;
        }
    }

    public static class CrashRestartFailed extends AlwaysAndNever {
        public CrashRestartFailed() {
            super("Failed to restart server after crash.");
            isActivity = true;
        }
    }

    public static class CrashStarted extends AlwaysAndNever {
        public CrashStarted() {
            super("Restarted server after crash.");
            isActivity = true;
        }
    }
}
