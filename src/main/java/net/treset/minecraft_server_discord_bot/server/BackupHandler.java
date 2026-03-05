package net.treset.minecraft_server_discord_bot.server;

import dev.treset.mcdl.servermanagement.exception.RpcCommunicationException;
import dev.treset.mcdl.servermanagement.vanilla.RpcMethods;
import dev.treset.mcdl.servermanagement.vanilla.RpcNotifications;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.exception.ServerOperationException;
import net.treset.minecraft_server_discord_bot.exception.UploadException;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.logging.OutputConsumer;
import net.treset.minecraft_server_discord_bot.logging.OutputType;
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
    public static void execute(Mode backupMode, Function<TemporalAccessor, String> nameProvider, OutputConsumer outputConsumer, boolean skipNotifications) {
        if(Config.get().backup == null) {
            outputConsumer.accept(OutputType.IMPORTANT, "Backup is not configured");
            return;
        }

        Mode mode = skipNotifications ? backupMode : sendNotifications(backupMode, outputConsumer);

        try {
            prepare(mode, outputConsumer);
        } catch (ServerOperationException e) {
            outputConsumer.accept(OutputType.IMPORTANT, "Failed to prepare server for backup, aborting!");
            Logger.error(e, "Failed to prepare server for backup");
            try {
                undoPrepare(mode, outputConsumer);
            } catch (ServerOperationException e1) {
                outputConsumer.accept(OutputType.IMPORTANT, e1.getMessage());
                Logger.error(e1, "Failed to undo backup preparation after preparation fail");
            }
            return;
        }
        outputConsumer.accept(OutputType.ALL, "Creating backup...");

        String fileName = nameProvider.apply(LocalDateTime.now()) + ".zip";

        try {
            FileHandler.zipFile(Config.get().server.worldPath, Config.get().backup.path + fileName);
        } catch (IOException e) {
            outputConsumer.accept(OutputType.IMPORTANT, "Failed to create local backup. The backup was not created.");
            Logger.error(e, "Failed to create backup");
            try {
                undoPrepare(mode, outputConsumer);
            } catch (ServerOperationException e1) {
                outputConsumer.accept(OutputType.IMPORTANT, e1.getMessage());
                Logger.error(e1, "Failed to undo backup preparation after fail");
            }
            return;
        }
        if(Config.get().backup.uploadService() == null) {
            outputConsumer.accept(OutputType.ALL, "Created backup successfully. Upload is disabled.");
        } else {
            UploadService service = Config.get().backup.uploadService();
            outputConsumer.accept(OutputType.ALL, "Created backup successfully. Uploading to " + service.name() + "... (this may take a few minutes)");
            new Thread(() -> {
                try {
                    service.upload(new File(Config.get().backup.path + fileName), fileName, "application/x-zip-compressed");
                    outputConsumer.accept(OutputType.ALL, "Uploaded backup successfully.");
                } catch (UploadException e){
                    Logger.error(e, "Failed to upload backup with service %s.", service.name());
                    outputConsumer.accept(OutputType.ALL, "Failed to upload backup. The local backup was created successfully.");
                }
            }).start();
        }

        try {
            undoPrepare(mode, outputConsumer);
        } catch (ServerOperationException e1) {
            outputConsumer.accept(OutputType.IMPORTANT, e1.getMessage());
            Logger.error(e1, "Failed to undo backup preparation after success");
        }
    }

    private static Mode sendNotifications(Mode mode, OutputConsumer outputConsumer) {
        if(!Config.get().backup.notifyIf.shouldNotify(mode)) {
            return mode;
        }

        List<Duration> durations = Config.get().backup.notificationOffsets();
        if(durations.isEmpty()) {
            return mode;
        }
        List<Duration> sortedDurations = durations.stream().sorted(Comparator.reverseOrder()).toList();

        outputConsumer.accept(OutputType.META, "Notifying players about backup (will take " + durationString(sortedDurations.get(0)) + ")");
        List<NamedDelay> delays = new ArrayList<>(sortedDurations.size());
        for(int i = 0; i < sortedDurations.size() - 1; i++) {
            delays.add(new NamedDelay(durationString(sortedDurations.get(i)), sortedDurations.get(i).toMillis() - sortedDurations.get(i+1).toMillis()));
        }
        delays.add(new NamedDelay(durationString(sortedDurations.get(sortedDurations.size()-1)), sortedDurations.get(sortedDurations.size()-1).toMillis()));

        String prefix = mode == Mode.RESTART ? "Restarting server" : "Creating backup";
        for(NamedDelay delay : delays) {
            outputConsumer.accept(OutputType.PLAYERS, String.format("%s in %s...", prefix, delay.name()));
            try {
                Thread.sleep(delay.delayMs());
            } catch (InterruptedException e) {
                outputConsumer.accept(OutputType.IMPORTANT, "Failed to wait between notifications");
                Logger.error("Failed to wait between notifications", e);
            }
        }
        return mode;
    }

    private static void prepare(Mode mode, OutputConsumer outputConsumer) throws ServerOperationException {
        switch (mode) {
            case RESTART -> stopServer(outputConsumer);
            case WHILE_RUNNING -> disableAutoSaveServer(outputConsumer);
        }
    }

    private static void undoPrepare(Mode mode, OutputConsumer outputConsumer) throws ServerOperationException {
        switch (mode) {
            case RESTART -> startServer(outputConsumer);
            case WHILE_RUNNING -> enableAutoSaveServer(outputConsumer);
        }
    }

    private static void stopServer(OutputConsumer outputConsumer) throws ServerOperationException {
        if(!ServerActions.isRunning()) {
            return;
        }
        outputConsumer.accept(OutputType.ALL, "Stopping for backup...");
        ServerActions.stopServer();
        try {
            Thread.sleep(Config.get().server.restartDelay * 1000L);
        } catch (InterruptedException e) {
            throw new ServerOperationException("Failed to wait after stopping server");
        }
    }

    private static void disableAutoSaveServer(OutputConsumer outputConsumer) throws ServerOperationException {
        if(!ServerActions.isRunning()) {
            return;
        }
        try {
            outputConsumer.accept(OutputType.PLAYERS, "Disabled autosave for backup");
            Boolean autosave = ManagementClient.get().request(RpcMethods.ServerSettings.AUTOSAVE_SET, false);
            if(autosave != false) {
                throw new ServerOperationException("Failed to disable autosave: response: " + autosave + "; expected: false");
            }
            outputConsumer.accept(OutputType.PLAYERS, "Saving before backup...");
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
            outputConsumer.accept(OutputType.PLAYERS, "Save completed");
        } catch (RpcCommunicationException e) {
            throw new ServerOperationException("Failed to disable autosave", e);
        }
    }

    private static void startServer(OutputConsumer outputConsumer) throws ServerOperationException {
        if(ServerActions.isRunning()) {
            Logger.warn("Server is already running after backup with stop");
        }
        outputConsumer.accept(OutputType.META, "Starting server after backup...");
        ServerActions.startServer();
        outputConsumer.accept(OutputType.META, "Started server after backup");
    }

    private static void enableAutoSaveServer(OutputConsumer outputConsumer) throws ServerOperationException {
        if(!ServerActions.isRunning()) {
            return;
        }
        try {
            Boolean autosave = ManagementClient.get().request(RpcMethods.ServerSettings.AUTOSAVE_SET, true);
            if(autosave != true) {
                throw new ServerOperationException("Failed to enable autosave: response: " + autosave + "; expected: true");
            }
            outputConsumer.accept(OutputType.PLAYERS, "Enabled autosave after backup");
        } catch (RpcCommunicationException e) {
            throw new ServerOperationException("Failed to enable autosave", e);
        }
    }

    private static String durationString(Duration duration) {
        int seconds = duration.toSecondsPart();
        int minutes = duration.toMinutesPart();
        int hours = duration.toHoursPart();

        if(hours == 0 && minutes == 0) {
            return String.format("%d seconds", seconds);
        }
        List<String> output = new ArrayList<>();

        if(hours != 0) {
            output.add(String.format("%d hours", hours));
        }
        if(minutes != 0 || seconds != 0) {
            output.add(String.format("%d minutes", minutes));
        }
        if(seconds != 0) {
            output.add(String.format("%d seconds", seconds));
        }
        return String.join(" ", output);
    }

    private record NamedDelay(
            String name,
            long delayMs
    ) {}

    public enum Mode {
        RESTART,
        WHILE_RUNNING
    }
}
