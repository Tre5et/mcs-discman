package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.io.File;
import java.util.List;

public class GoogleDriveConfig extends Validatable {
    public String folderId;
    public File credentialsFile;

    public void setCredentialsFile(String file) {
        credentialsFile = new File(file);
    }

    @Override
    public List<String> prefix() {
        return List.of("backup", "upload", "googleDrive");
    }

    @Override
    public void validate() throws ConfigException {
        requireAllOrNone(p(folderId, "folderId"), p(credentialsFile, "credentialsFile"));
    }
}
