package net.treset.minecraft_server_discord_bot.upload;

import net.treset.minecraft_server_discord_bot.config.GoogleDriveConfig;
import net.treset.minecraft_server_discord_bot.exception.UploadException;

import java.io.File;

public interface UploadService {
    String name();
    void upload(File source, String targetPath, String mimeType) throws UploadException;

    static GoogleDriveUploadService googleDrive(GoogleDriveConfig config) throws UploadException {
        return new GoogleDriveUploadService(config);
    }
}
