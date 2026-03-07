package net.treset.minecraft_server_discord_bot.config.backup;

import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.ValidatableConfig;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;
import net.treset.minecraft_server_discord_bot.upload.UploadService;

import java.util.List;

public class UploadConfig extends ValidatableConfig implements UploadServiceConfig {
    public GoogleDriveConfig googleDrive;

    @Override
    public List<String> prefix() {
        return List.of("backup", "upload");
    }

    @Override
    public void validate(Config newConfig) throws ConfigException {
        requireExactly(1, p(googleDrive, "googleDrive"));
        if(googleDrive != null) {
            googleDrive.validate(newConfig);
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
