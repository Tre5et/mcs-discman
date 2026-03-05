package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.exception.ConfigException;
import net.treset.minecraft_server_discord_bot.upload.UploadService;

import java.util.List;

public class UploadConfig extends Validatable implements UploadServiceConfig {
    public GoogleDriveConfig googleDrive;

    @Override
    public List<String> prefix() {
        return List.of("backup", "upload");
    }

    @Override
    public void validate() throws ConfigException {
        requireExactly(1, p(googleDrive, "googleDrive"));
        if(googleDrive != null) {
            googleDrive.validate();
        }
    }

    @Override
    public UploadService service() {
        if(googleDrive != null && googleDrive.service() != null) {
            return googleDrive.service();
        }
        return null;
    }
}
