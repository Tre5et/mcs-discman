package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.util.List;

public class UploadConfig extends Validatable {
    public GoogleDriveConfig googleDrive;

    @Override
    public List<String> prefix() {
        return List.of("backup", "upload");
    }

    @Override
    public void validate() throws ConfigException {
        requireAny(p(googleDrive, "googleDrive"));
        if(googleDrive != null) {
            googleDrive.validate();
        }
    }
}
