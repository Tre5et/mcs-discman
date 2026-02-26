package net.treset.minecraft_server_discord_bot.server;

import dev.treset.mcdl.servermanagement.exception.RpcCommunicationException;
import dev.treset.mcdl.servermanagement.vanilla.RpcMethods;
import dev.treset.mcdl.servermanagement.vanilla.RpcNotifications;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.exception.ServerOperationException;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.system.FileHandler;
import net.treset.minecraft_server_discord_bot.upload.GoogleDriveClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.function.Consumer;
import java.util.function.Function;

public class BackupHandler {
    public static void execute(Mode mode, Function<TemporalAccessor, String> nameProvider, Consumer<String> outputConsumer) {
        try {
            prepare(mode);
        } catch (ServerOperationException e) {
            outputConsumer.accept("Failed to prepare server for backup, aborting!");
            Logger.error(e, "Failed to prepare server for backup");
            try {
                undoPrepare(mode);
            } catch (ServerOperationException e1) {
                outputConsumer.accept(e1.getMessage());
                Logger.error(e1, "Failed to undo backup preparation after preparation fail");
            }
            return;
        }
        outputConsumer.accept("Creating backup... (this may take a few minutes)");

        String fileName = nameProvider.apply(LocalDateTime.now()) + ".zip";

        try {
            FileHandler.zipFile(Config.server.world_path, Config.server.backup_path + fileName);
        } catch (IOException e) {
            outputConsumer.accept("Failed to create local backup. The backup was not created.");
            Logger.error(e, "Failed to create backup");
            try {
                undoPrepare(mode);
            } catch (ServerOperationException e1) {
                outputConsumer.accept(e1.getMessage());
                Logger.error(e1, "Failed to undo backup preparation after fail");
            }
            return;
        }
        if(!Config.drive.enabled) {
            outputConsumer.accept("Created backup successfully. Upload is disabled.");
            try {
                undoPrepare(mode);
            } catch (ServerOperationException e1) {
                outputConsumer.accept(e1.getMessage());
                Logger.error(e1, "Failed to undo backup preparation after success");
            }
            return;
        }

        outputConsumer.accept("Created backup successfully. Uploading to Google Drive... (this may take a few minutes)");

        if(GoogleDriveClient.uploadFile(Config.server.backup_path + fileName, fileName, "application/x-zip-compressed", Config.drive.drive_folder_id) != null) {
            outputConsumer.accept("Uploaded backup successfully.");
        } else {
            outputConsumer.accept("Failed to upload backup. The local backup was created successfully.");
        }
    }

    private static void prepare(Mode mode) throws ServerOperationException {
        switch (mode) {
            case RESTART -> stopServer();
            case WHILE_RUNNING -> disableAutoSaveServer();
        }
    }

    private static void undoPrepare(Mode mode) throws ServerOperationException {
        switch (mode) {
            case RESTART -> startServer();
            case WHILE_RUNNING -> enableAutoSaveServer();
        }
    }

    private static void stopServer() throws ServerOperationException {
        if(!ServerActions.isRunning()) {
            return;
        }
        if(!ServerActions.stopServer()) {
            throw new ServerOperationException("Failed to stop server");
        }
    }

    private static void disableAutoSaveServer() throws ServerOperationException {
        if(!ServerActions.isRunning()) {
            return;
        }
        try {
            Boolean autosave = ManagementClient.get().request(RpcMethods.ServerSettings.AUTOSAVE_SET, false);
            if(autosave != false) {
                throw new ServerOperationException("Failed to disable autosave: response: " + autosave + "; expected: false");
            }
            ManagementClient.get().awaitNotification(
                    RpcNotifications.Server.saved(),
                    () -> {
                        Boolean save = ManagementClient.get().request(RpcMethods.Server.SAVE, true);
                        if(!save) {
                            throw new RpcCommunicationException("Failed to initiate server save");
                        }
                    }
            );
        } catch (RpcCommunicationException e) {
            throw new ServerOperationException("Failed to disable autosave", e);
        }
    }

    private static void startServer() throws ServerOperationException {
        if(ServerActions.isRunning()) {
            Logger.warn("Server is already running after backup with stop");
        }
        ServerActions.awaitServerStarted();
    }

    private static void enableAutoSaveServer() throws ServerOperationException {
        if(!ServerActions.isRunning()) {
            return;
        }
        try {
            Boolean autosave = ManagementClient.get().request(RpcMethods.ServerSettings.AUTOSAVE_SET, true);
            if(autosave != true) {
                throw new ServerOperationException("Failed to enable autosave: response: " + autosave + "; expected: true");
            }
        } catch (RpcCommunicationException e) {
            throw new ServerOperationException("Failed to enable autosave", e);
        }
    }

    public enum Mode {
        RESTART,
        WHILE_RUNNING
    }
}
