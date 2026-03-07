package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.config.notification.NotificationConfig;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.util.List;

public class NotificationsConfig extends ValidatableConfig {
    public NotificationConfig.Joined joined = new NotificationConfig.Joined();
    public NotificationConfig.Left left = new NotificationConfig.Left();
    public NotificationConfig.Advancement advancement = new NotificationConfig.Advancement();
    public NotificationConfig.Death death = new NotificationConfig.Death();
    public NotificationConfig.Started started = new NotificationConfig.Started();
    public NotificationConfig.Stopping stopping = new NotificationConfig.Stopping();
    public NotificationConfig.Stopped stopped = new NotificationConfig.Stopped();
    public NotificationConfig.BackupStarted backupStarted = new NotificationConfig.BackupStarted();
    public NotificationConfig.BackupCompleted backupCompleted = new NotificationConfig.BackupCompleted();
    public NotificationConfig.BackupFailed backupFailed = new NotificationConfig.BackupFailed();
    public NotificationConfig.BackupUploading backupUploading = new NotificationConfig.BackupUploading();
    public NotificationConfig.BackupUploadFailed backupUploadFailed = new NotificationConfig.BackupUploadFailed();
    public NotificationConfig.BackupWhileRunningAnnouncement backupWhileRunningAnnouncement = new NotificationConfig.BackupWhileRunningAnnouncement();
    public NotificationConfig.BackupRestartAnnouncement backupRestartAnnouncement = new NotificationConfig.BackupRestartAnnouncement();
    public NotificationConfig.BackupAnnouncing backupAnnouncing = new NotificationConfig.BackupAnnouncing();
    public NotificationConfig.BackupAutosaveDisabled backupAutosaveDisabled = new NotificationConfig.BackupAutosaveDisabled();
    public NotificationConfig.BackupAutosaveEnabled backupAutosaveEnabled = new NotificationConfig.BackupAutosaveEnabled();
    public NotificationConfig.BackupSaving backupSaving = new NotificationConfig.BackupSaving();
    public NotificationConfig.BackupSaved backupSaved = new NotificationConfig.BackupSaved();

    @Override
    public List<String> prefix() {
        return List.of("notifications");
    }

    @Override
    public void validate(Config config) throws ConfigException {
        joined.validate(config);
        left.validate(config);
        advancement.validate(config);
        death.validate(config);
        started.validate(config);
        stopping.validate(config);
        stopped.validate(config);
        backupStarted.validate(config);
        backupCompleted.validate(config);
        backupFailed.validate(config);
        backupUploading.validate(config);
        backupUploadFailed.validate(config);
        backupWhileRunningAnnouncement.validate(config);
        backupRestartAnnouncement.validate(config);
        backupAnnouncing.validate(config);
        backupAutosaveDisabled.validate(config);
        backupAutosaveEnabled.validate(config);
        backupSaving.validate(config);
        backupSaved.validate(config);
    }
}
