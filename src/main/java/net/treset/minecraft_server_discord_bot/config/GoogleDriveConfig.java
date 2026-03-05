package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.exception.ConfigException;
import net.treset.minecraft_server_discord_bot.exception.UploadException;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.upload.GoogleDriveUploadService;
import net.treset.minecraft_server_discord_bot.upload.UploadService;

import java.io.File;
import java.util.List;

public class GoogleDriveConfig extends Validatable implements UploadServiceConfig {
    public String folderId;
    public File credentialsFile;

    private transient GoogleDriveUploadService uploadService = null;

    @Override
    public List<String> prefix() {
        return List.of("backup", "upload", "googleDrive");
    }

    @Override
    public void validate() throws ConfigException {
        requireAllOrNone(p(folderId, "folderId"), p(credentialsFile, "credentialsFile"));
        try {
            uploadService = UploadService.googleDrive(this);
        } catch (UploadException e) {
            Logger.error(e, "Failed to initialize google drive upload service.");
        }
    }

    @Override
    public GoogleDriveUploadService service() {
        return uploadService;
    }
}
