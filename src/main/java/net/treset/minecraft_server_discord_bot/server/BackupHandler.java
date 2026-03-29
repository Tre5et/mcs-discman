package net.treset.minecraft_server_discord_bot.server;

import dev.treset.mcdl.servermanagement.exception.RpcCommunicationException;
import dev.treset.mcdl.servermanagement.vanilla.RpcMethods;
import dev.treset.mcdl.servermanagement.vanilla.RpcNotifications;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.event.EventConfig;
import net.treset.minecraft_server_discord_bot.config.event.EventDiscordOutput;
import net.treset.minecraft_server_discord_bot.exception.ServerOperationException;
import net.treset.minecraft_server_discord_bot.exception.UploadException;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.system.FileHandler;
import net.treset.minecraft_server_discord_bot.upload.UploadService;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class BackupHandler {
    public static void execute(Mode backupMode, Function<TemporalAccessor, String> nameProvider, EventDiscordOutput output, boolean skipNotifications) {
        if(Config.get().backup == null) {
            Config.get().events.backupNotConfigured.send(output);
            return;
        }

        Mode mode = skipNotifications ? backupMode : sendNotifications(backupMode, output);

        try {
            prepare(mode, output);
        } catch (ServerOperationException e) {
            Logger.error(e, "Failed to prepare server for backup");
            Config.get().events.backupPreparationFailed.send(output);
            try {
                undoPrepare(mode, output);
            } catch (ServerOperationException e1) {
                Logger.error(e1, "Failed to undo backup preparation after preparation fail");
                Config.get().events.backupPreparationUndoFailed.send(output);
            }
            return;
        }
        Config.get().events.backupStarted.send(output);

        String fileName = nameProvider.apply(LocalDateTime.now()) + ".zip";

        try {
            FileHandler.zipFile(Config.get().server.worldPath, Config.get().backup.path + fileName);
        } catch (IOException e) {
            Logger.error(e, "Failed to create backup");
            Config.get().events.backupFailed.send(output);
            try {
                undoPrepare(mode, output);
            } catch (ServerOperationException e1) {
                Logger.error(e1, "Failed to undo backup preparation after fail");
                Config.get().events.backupPreparationUndoFailed.send(output);
            }
            return;
        }
        if(Config.get().backup.uploadService() == null) {
            Config.get().events.backupCreatedLocal.send(output);
        } else {
            UploadService service = Config.get().backup.uploadService();
            Config.get().events.backupUploading.send(service, output);
            new Thread(() -> {
                try {
                    service.upload(new File(Config.get().backup.path + fileName), fileName, "application/x-zip-compressed");
                    Config.get().events.backupCompleted.send(output);
                } catch (UploadException e){
                    Logger.error(e, "Failed to upload backup with service %s.", service.name());
                    Config.get().events.backupUploadFailed.send(service, output);
                }
            }).start();
        }

        try {
            undoPrepare(mode, output);
        } catch (ServerOperationException e1) {
            Logger.error(e1, "Failed to undo backup preparation after success");
            Config.get().events.backupPreparationUndoFailed.send(output);
        }
    }

    private static Mode sendNotifications(Mode mode, EventDiscordOutput output) {
        if(!Config.get().backup.notifyIf.shouldNotify(mode)) {
            return mode;
        }

        List<Duration> durations = Config.get().backup.notificationOffsets();
        if(durations.isEmpty()) {
            return mode;
        }
        List<Duration> sortedDurations = durations.stream().sorted(Comparator.reverseOrder()).toList();

        Config.get().events.backupAnnouncing.send(sortedDurations.get(0), output);
        List<DurationDelay> delays = new ArrayList<>(sortedDurations.size());
        for(int i = 0; i < sortedDurations.size() - 1; i++) {
            delays.add(new DurationDelay(sortedDurations.get(i), sortedDurations.get(i).toMillis() - sortedDurations.get(i+1).toMillis()));
        }
        delays.add(new DurationDelay(sortedDurations.get(sortedDurations.size()-1), sortedDurations.get(sortedDurations.size()-1).toMillis()));

        EventConfig<Duration> announcement = mode == Mode.RESTART ? Config.get().events.backupRestartAnnouncement : Config.get().events.backupWhileRunningAnnouncement;
        for(DurationDelay delay : delays) {
            announcement.send(delay.total, output);
            try {
                Thread.sleep(delay.untilNext());
            } catch (InterruptedException e) {
                Logger.error("Failed to wait between notifications", e);
            }
        }
        return mode;
    }

    private static void prepare(Mode mode, EventDiscordOutput output) throws ServerOperationException {
        switch (mode) {
            case RESTART -> stopServer(output);
            case WHILE_RUNNING -> disableAutoSaveServer(output);
        }
    }

    private static void undoPrepare(Mode mode, EventDiscordOutput output) throws ServerOperationException {
        switch (mode) {
            case RESTART -> startServer(output);
            case WHILE_RUNNING -> enableAutoSaveServer(output);
        }
    }

    private static void stopServer(EventDiscordOutput output) throws ServerOperationException {
        if(!ServerActions.isRunning()) {
            return;
        }
        Config.get().events.backupServerStopping.send(output);
        ServerActions.stopServer();
        try {
            Thread.sleep(Config.get().server.restartDelay * 1000L);
        } catch (InterruptedException e) {
            throw new ServerOperationException("Failed to wait after stopping server");
        }
        Config.get().events.backupServerStopped.send(output);
    }

    private static void disableAutoSaveServer(EventDiscordOutput output) throws ServerOperationException {
        if(!ServerActions.isRunning()) {
            return;
        }
        try {
            Config.get().events.backupAutosaveDisabled.send(output);
            Boolean autosave = ManagementClient.get().request(RpcMethods.ServerSettings.AUTOSAVE_SET, false);
            if(autosave != false) {
                throw new ServerOperationException("Failed to disable autosave: response: " + autosave + "; expected: false");
            }
            Config.get().events.backupSaving.send(output);
            ManagementClient.get().awaitNotification(
                    RpcNotifications.Server.saved(),
                    () -> {
                        Boolean save = ManagementClient.get().request(RpcMethods.Server.SAVE, true);
                        if(!save) {
                            throw new RpcCommunicationException("Failed to initiate server save");
                        }
                    },
                    Config.get().server.saveTimeout * 1000L
            );
            Config.get().events.backupSaved.send(output);
        } catch (RpcCommunicationException e) {
            throw new ServerOperationException("Failed to disable autosave", e);
        }
    }

    private static void startServer(EventDiscordOutput output) throws ServerOperationException {
        if(ServerActions.isRunning()) {
            Logger.warn("Server is already running after backup with stop");
        }
        Config.get().events.backupServerStarting.send(output);
        ServerActions.startServer();
        Config.get().events.backupServerStarted.send(output);
    }

    private static void enableAutoSaveServer(EventDiscordOutput output) throws ServerOperationException {
        if(!ServerActions.isRunning()) {
            return;
        }
        try {
            Boolean autosave = ManagementClient.get().request(RpcMethods.ServerSettings.AUTOSAVE_SET, true);
            if(autosave != true) {
                throw new ServerOperationException("Failed to enable autosave: response: " + autosave + "; expected: true");
            }
            Config.get().events.backupAutosaveEnabled.send(output);
        } catch (RpcCommunicationException e) {
            throw new ServerOperationException("Failed to enable autosave", e);
        }
    }

    private record DurationDelay(
            Duration total,
            long untilNext
    ) {}

    public enum Mode {
        RESTART,
        WHILE_RUNNING
    }
}
