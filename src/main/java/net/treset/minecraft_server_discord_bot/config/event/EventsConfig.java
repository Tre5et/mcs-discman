package net.treset.minecraft_server_discord_bot.config.event;

import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.ValidatableConfig;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.util.List;

public class EventsConfig extends ValidatableConfig {
    public EventConfig.Online online = new EventConfig.Online();
    public EventConfig.Joined joined = new EventConfig.Joined();
    public EventConfig.Left left = new EventConfig.Left();
    public EventConfig.Advancement advancement = new EventConfig.Advancement();
    public EventConfig.Death death = new EventConfig.Death();
    public EventConfig.Started started = new EventConfig.Started();
    public EventConfig.Stopping stopping = new EventConfig.Stopping();
    public EventConfig.Stopped stopped = new EventConfig.Stopped();
    public EventConfig.BackupStarted backupStarted = new EventConfig.BackupStarted();
    public EventConfig.BackupCompleted backupCompleted = new EventConfig.BackupCompleted();
    public EventConfig.BackupFailed backupFailed = new EventConfig.BackupFailed();
    public EventConfig.BackupUploading backupUploading = new EventConfig.BackupUploading();
    public EventConfig.BackupUploadFailed backupUploadFailed = new EventConfig.BackupUploadFailed();
    public EventConfig.BackupWhileRunningAnnouncement backupWhileRunningAnnouncement = new EventConfig.BackupWhileRunningAnnouncement();
    public EventConfig.BackupRestartAnnouncement backupRestartAnnouncement = new EventConfig.BackupRestartAnnouncement();
    public EventConfig.BackupAnnouncing backupAnnouncing = new EventConfig.BackupAnnouncing();
    public EventConfig.BackupAutosaveDisabled backupAutosaveDisabled = new EventConfig.BackupAutosaveDisabled();
    public EventConfig.BackupAutosaveEnabled backupAutosaveEnabled = new EventConfig.BackupAutosaveEnabled();
    public EventConfig.BackupSaving backupSaving = new EventConfig.BackupSaving();
    public EventConfig.BackupSaved backupSaved = new EventConfig.BackupSaved();

    @Override
    public List<String> prefix() {
        return List.of("notifications");
    }

    @Override
    public void validate(Config config) throws ConfigException {
        online.validate(config);
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
