package net.treset.minecraft_server_discord_bot.config.notification;

import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.ValidatableConfig;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.util.ArrayList;
import java.util.List;

public abstract class NotificationConfig<C extends NotificationContextTemplate> extends ValidatableConfig {
    public NotificationCondition discord;
    public NotificationCondition game;
    public String message;
    public transient String formattableMessage;
    public transient List<String> formatKeys;

    public NotificationConfig(NotificationCondition discord, NotificationCondition game, String message) {
        this.discord = discord;
        this.game = game;
        this.message = message;
    }

    public String message(NotificationContext<C> context) {
        Object[] values = formatKeys.stream().map(context::value).toArray(String[]::new);
        return String.format(formattableMessage, values);
    }

    @Override
    public List<String> prefix() {
        return List.of("notification");
    }

    public void validate(Config config, C template) throws ConfigException {
        if(message == null || !message.contains("{")) {
            formattableMessage = message;
            formatKeys = List.of();
            return;
        }

        StringBuilder messageFormat = new StringBuilder();
        formatKeys = new ArrayList<>();
        int i = 0;
        while(i >= 0) {
            int startIndex = message.indexOf('{', i);
            if(startIndex < 0) {
                messageFormat.append(message, i, message.length());
                break;
            }
            if(startIndex > 0 && message.charAt(startIndex-1) == '\\') continue;
            int endIndex = message.indexOf('}', startIndex);
            if(endIndex < 0) {
                throw new ConfigException("Message format replacement is not closed");
            }
            String key = message.substring(startIndex+1, endIndex);
            if(!template.hasKey(key)) {
                throw new ConfigException("Key '" + key + "' is defined in notification message but is not supported. Supported are '" + String.join(", ", template.availableKeys()) + "'.");
            }
            messageFormat.append(message, i, startIndex);
            messageFormat.append("%s");
            formatKeys.add(message.substring(startIndex+1, endIndex));
            i = endIndex + 1;
        }
        formattableMessage = messageFormat.toString();
    }

    public static class DateTime extends NotificationConfig<NotificationContextTemplate.DateTime> {
        public DateTime(NotificationCondition discord, NotificationCondition game, String message) {
            super(discord, game, message);
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, new NotificationContextTemplate.DateTime());
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

    public static class Joined extends NotificationConfig<NotificationContextTemplate.Player> {
        public Joined() {
            super(NotificationCondition.always, NotificationCondition.never, "{name} joined the game.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, new NotificationContextTemplate.Player());
        }
    }

    public static class Left extends NotificationConfig<NotificationContextTemplate.Player> {
        public Left() {
            super(NotificationCondition.always, NotificationCondition.never, "{name} left the game.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, new NotificationContextTemplate.Player());
        }
    }

    public static class Advancement extends NotificationConfig<NotificationContextTemplate.Advancement> {
        public Advancement() {
            super(NotificationCondition.always, NotificationCondition.never, "{message}.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, new NotificationContextTemplate.Advancement());
        }
    }

    public static class Death extends NotificationConfig<NotificationContextTemplate.Death> {
        public Death() {
            super(NotificationCondition.always, NotificationCondition.never, "{message}.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, new NotificationContextTemplate.Death());
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

    public static class BackupUploading extends NotificationConfig<NotificationContextTemplate.UploadService> {

        public BackupUploading() {
            super(NotificationCondition.always, NotificationCondition.always, "Uploading backup to {service}...");
        }
        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, new NotificationContextTemplate.UploadService());
        }

    }

    public static class BackupUploadFailed extends NotificationConfig<NotificationContextTemplate.UploadService> {
        public BackupUploadFailed() {
            super(NotificationCondition.always, NotificationCondition.always, "Failed to upload backup to {service}. The local backup was created.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, new NotificationContextTemplate.UploadService());
        }
    }

    public static class BackupWhileRunningAnnouncement extends NotificationConfig<NotificationContextTemplate.Countdown> {
        public BackupWhileRunningAnnouncement() {
            super(NotificationCondition.never, NotificationCondition.always, "Creating backup in {timeRemaining}.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, new NotificationContextTemplate.Countdown());
        }
    }

    public static class BackupRestartAnnouncement extends NotificationConfig<NotificationContextTemplate.Countdown> {
        public BackupRestartAnnouncement() {
            super(NotificationCondition.never, NotificationCondition.always, "Restarting server in in {timeRemaining}.");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, new NotificationContextTemplate.Countdown());
        }
    }

    public static class BackupAnnouncing extends NotificationConfig<NotificationContextTemplate.Countdown> {
        public BackupAnnouncing() {
            super(NotificationCondition.never, NotificationCondition.always, "Announcing backup to players (performing in {timeRemaining}).");
        }

        @Override
        public void validate(Config config) throws ConfigException {
            super.validate(config, new NotificationContextTemplate.Countdown());
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
