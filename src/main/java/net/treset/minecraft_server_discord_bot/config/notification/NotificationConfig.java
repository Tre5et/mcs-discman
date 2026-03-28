package net.treset.minecraft_server_discord_bot.config.notification;

import dev.treset.mcdl.servermanagement.vanilla.types.RpcPlayer;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.ValidatableConfig;
import net.treset.minecraft_server_discord_bot.config.message.Message;
import net.treset.minecraft_server_discord_bot.config.message.MessageTemplate;
import net.treset.minecraft_server_discord_bot.config.message.MessageTemplates;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;
import net.treset.minecraft_server_discord_bot.server.data.RpcAdvancement;
import net.treset.minecraft_server_discord_bot.server.data.RpcDeath;
import net.treset.minecraft_server_discord_bot.upload.UploadService;

import java.time.Duration;
import java.util.List;

public abstract class NotificationConfig<C> extends ValidatableConfig {
    public NotificationCondition discord;
    public NotificationCondition game;
    public Message<C> message;

    public NotificationConfig(NotificationCondition discord, NotificationCondition game, String message) {
        this.discord = discord;
        this.game = game;
        this.message = new Message<>(message);
    }

    public String message(C source) {
        return message.get(source);
    }

    @Override
    public List<String> prefix() {
        return List.of("notification");
    }

    public void validate(Config config, MessageTemplate<C> template) throws ConfigException {
        message.validate(template);
    }

    public static class DateTime extends NotificationConfig<Object> {
        public DateTime(NotificationCondition discord, NotificationCondition game, String message) {
            super(discord, game, message);
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.DATE_TIME);
        }
    }

    public static class Always extends NotificationConfig.DateTime {
        public Always(String message) {
            super(NotificationCondition.always, NotificationCondition.always, message);
        }
    }

    public static class Never extends NotificationConfig.DateTime{
        public Never(String message) {
            super(NotificationCondition.never, NotificationCondition.never, message);
        }
    }

    public static class AlwaysAndNever extends NotificationConfig.DateTime {
        public AlwaysAndNever(String message) {
            super(NotificationCondition.always, NotificationCondition.never, message);
        }
    }

    public static class NeverAndAlways extends NotificationConfig.DateTime {
        public NeverAndAlways(String message) {
            super(NotificationCondition.always, NotificationCondition.never, message);
        }
    }

    public static class Joined extends NotificationConfig<RpcPlayer> {
        public Joined() {
            super(NotificationCondition.always, NotificationCondition.never, "{name} joined the game.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.PLAYER);
        }
    }

    public static class Left extends NotificationConfig<RpcPlayer> {
        public Left() {
            super(NotificationCondition.always, NotificationCondition.never, "{name} left the game.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.PLAYER);
        }
    }

    public static class Advancement extends NotificationConfig<RpcAdvancement> {
        public Advancement() {
            super(NotificationCondition.always, NotificationCondition.never, "{message}.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.ADVANCEMENT);
        }
    }

    public static class Death extends NotificationConfig<RpcDeath> {
        public Death() {
            super(NotificationCondition.always, NotificationCondition.never, "{message}.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.DEATH);
        }
    }

    public static class Started extends Never {
        public Started() {
            super("Server started.");
        }
    }

    public static class Stopping extends Never {
        public Stopping() {
            super("Server is stopping...");
        }
    }

    public static class Stopped extends Never {
        public Stopped() {
            super("Server stopped.");
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

    public static class BackupUploading extends NotificationConfig<UploadService> {

        public BackupUploading() {
            super(NotificationCondition.always, NotificationCondition.always, "Uploading backup to {service}...");
        }
        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.UPLOAD_SERVICE);
        }

    }

    public static class BackupUploadFailed extends NotificationConfig<UploadService> {
        public BackupUploadFailed() {
            super(NotificationCondition.always, NotificationCondition.always, "Failed to upload backup to {service}. The local backup was created.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.UPLOAD_SERVICE);
        }
    }

    public static class BackupWhileRunningAnnouncement extends NotificationConfig<Duration> {
        public BackupWhileRunningAnnouncement() {
            super(NotificationCondition.never, NotificationCondition.always, "Creating backup in {duration}.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.DURATION);
        }
    }

    public static class BackupRestartAnnouncement extends NotificationConfig<Duration> {
        public BackupRestartAnnouncement() {
            super(NotificationCondition.never, NotificationCondition.always, "Restarting server in in {duration}.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, MessageTemplates.DURATION);
        }
    }

    public static class BackupAnnouncing extends NotificationConfig<Duration> {
        public BackupAnnouncing() {
            super(NotificationCondition.never, NotificationCondition.always, "Announcing backup to players (performing in {duration}).");
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
}
