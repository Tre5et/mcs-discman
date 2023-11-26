package net.treset.minecraft_server_discord_bot.io;

import net.treset.minecraft_server_discord_bot.tools.ConfigTools;
import net.treset.minecraft_server_discord_bot.tools.FormatTools;

public class PermanentOperationsConfig {
    public boolean LOGGING_ENABLED;
    public boolean START_LOGGING;
    public boolean JOIN_LOGGING;
    public boolean FULL_LOGGING;
    public boolean BACKUP_ENABLED;
    public String BACKUP_HOUR;
    public int BACKUP_TIMEOUT;
    public boolean CRASH_CHECK_ENABLED;
    public boolean LOG_NO_BACKUP;
    public int INACTIVITY_REMINDER_ENABLED;
    
    public PermanentOperationsConfig(String source) {
        this.JOIN_LOGGING = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "join_leave_logging", false, false), false);
        this.START_LOGGING = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "start_logging", false, false), false);
        this.FULL_LOGGING = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "full_console_logging", false, false), false);

        this.LOGGING_ENABLED =
                this.JOIN_LOGGING ||
                        this.START_LOGGING ||
                        this.FULL_LOGGING;

        this.BACKUP_ENABLED = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "autobackup", false, false), true);
        this.BACKUP_HOUR = ConfigTools.findConfigOption(source, "autobackup_hour", false, false);
        if(BACKUP_HOUR == null || BACKUP_HOUR.isBlank()) {
            BACKUP_HOUR = "01";
        }
        this.BACKUP_TIMEOUT = FormatTools.stringToInt(ConfigTools.findConfigOption(source, "autobackup_timeout", false, true), 1200);
        this.CRASH_CHECK_ENABLED = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "crash_check", false, false), false);
        this.LOG_NO_BACKUP = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "log_no_backup", false, false), true);
        this.INACTIVITY_REMINDER_ENABLED = FormatTools.stringToInt(ConfigTools.findConfigOption(source, "inactivity_reminder", false, false), 0);
    }
}
