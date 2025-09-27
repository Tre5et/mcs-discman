package net.treset.minecraft_server_discord_bot.config;

import java.io.IOException;

public class DriveConfig extends BaseConfig {
    public final String drive_folder_id;
    public final String drive_credentials_file;
    public final boolean enabled;

    public DriveConfig(String config) throws IOException {
        super(config);

        drive_folder_id = load("drive_folder_id");
        drive_credentials_file = load("drive_credentials_file");

        if(drive_folder_id == null ^ drive_credentials_file == null) {
            throw new IOException("Invalid drive config. Options 'drive_folder_id', 'drive_credentials_file' must both be eiter set or unset.");
        }

        enabled = drive_folder_id != null;
    }
}
