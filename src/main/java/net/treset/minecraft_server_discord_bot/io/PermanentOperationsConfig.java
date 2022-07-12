package net.treset.minecraft_server_discord_bot.io;

import net.treset.minecraft_server_discord_bot.tools.ConfigTools;
import net.treset.minecraft_server_discord_bot.tools.FormatTools;

public class PermanentOperationsConfig {
    public boolean LOGGING_ENABLED = false;
    public boolean START_LOGGING = false;
    public boolean JOIN_LOGGING = false;
    public boolean FULL_LOGGING = false;
    public boolean BACKUP_ENABLED = true;
    public String BACKUP_HOUR = "01";
    public boolean CRASH_CHECK_ENABLED = false;
    public boolean LOG_NO_BACKUP = true;
    public int INACTIVITY_REMINDER_ENABLED = 0;
    
    public PermanentOperationsConfig(String source) {
        this.JOIN_LOGGING = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "join_leave_logging", false, false));
        this.START_LOGGING = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "start_logging", false, false));
        this.FULL_LOGGING = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "full_console_logging", false, false));

        this.LOGGING_ENABLED =
                this.JOIN_LOGGING ||
                        this.START_LOGGING ||
                        this.FULL_LOGGING;

        this.BACKUP_ENABLED = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "autobackup", false, false));
        this.BACKUP_HOUR = ConfigTools.findConfigOption(source, "autobackup_hour", false, false);
        this.CRASH_CHECK_ENABLED = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "crash_check", false, false));
        this.LOG_NO_BACKUP = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "log_no_backup", false, false));
        this.INACTIVITY_REMINDER_ENABLED = FormatTools.stringToInt(ConfigTools.findConfigOption(source, "inactivity_reminder", false, false));
    }
}
