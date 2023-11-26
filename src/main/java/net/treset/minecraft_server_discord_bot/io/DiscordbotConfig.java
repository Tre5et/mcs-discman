package net.treset.minecraft_server_discord_bot.io;

import net.treset.minecraft_server_discord_bot.tools.ConfigTools;
import net.treset.minecraft_server_discord_bot.tools.FormatTools;

import java.util.regex.Pattern;

public class DiscordbotConfig {
    public String TOKEN;
    public String GUILD_ID;
    public String MESSAGE_CHANNEL_ID;
    public String MODERATOR_ID;
    public String SERVER_PATH;
    public String WORLD_NAME;
    public String LOG_PATH;
    public String WORLD_PATH;
    public String BACKUPS_PATH;
    public boolean AUTODETECT_DETAILS;
    public String ACTIVE_COMMAND;
    public String ACTIVE_KEY;
    public String START_COMMAND;
    public String RUN_COMMAND;
    public boolean DRIVE_UPLOAD;
    public String DRIVE_FOLDER_ID;
    public boolean DEBUG;
    
    public DiscordbotConfig(String source) {
        this.TOKEN = ConfigTools.findConfigOption(source, "token", false, false);
        this.GUILD_ID = ConfigTools.findConfigOption(source, "guild_id", false, false);
        this.MESSAGE_CHANNEL_ID = ConfigTools.findConfigOption(source, "message_channel_id", false, false);
        this.MODERATOR_ID = ConfigTools.findConfigOption(source, "moderator_role_id", false, false);
        this.SERVER_PATH = ConfigTools.findConfigOption(source, "server_path", false, false);
        this.WORLD_NAME = ConfigTools.findConfigOption(source, "world_name", false, false);
        this.BACKUPS_PATH = ConfigTools.findConfigOption(source, "backup_path", false, false);
        this.AUTODETECT_DETAILS = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "autodetect_details", false, false));
        this.ACTIVE_COMMAND = ConfigTools.findConfigOption(source, "active_command", false, false);
        this.ACTIVE_KEY = ConfigTools.findConfigOption(source, "active_key", false, false);
        this.START_COMMAND = ConfigTools.findConfigOption(source, "start_command", false, false);
        this.RUN_COMMAND = ConfigTools.findConfigOption(source, "runcommand_command", false, false);
        this.DRIVE_UPLOAD = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "auto_upload_enabled", false, false));
        this.DRIVE_FOLDER_ID = ConfigTools.findConfigOption(source, "upload_folder_id", false, true);
        this.DEBUG = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "debug", false, true, true), false);

        if(FormatTools.matchRegex(this.SERVER_PATH, Pattern.compile("(\\/$)", Pattern.MULTILINE)).isEmpty()) { //account for / at the end
            this.SERVER_PATH += "/";
        }
        this.LOG_PATH = this.SERVER_PATH + "logs/latest.log";
        this.WORLD_PATH = this.SERVER_PATH + this.WORLD_NAME;
    }
}
