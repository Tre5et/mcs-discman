package net.treset.minecraft_server_discord_bot.io;

import net.treset.minecraft_server_discord_bot.tools.ConfigTools;
import net.treset.minecraft_server_discord_bot.tools.FormatTools;

public class PermanentOperationsConfig {
    public boolean isLoggingEnabled = false;
    public boolean isStartLoggingEnabled = false;
    public boolean isJoinLoggingEnabled = false;
    public boolean isLoggingFull = false;
    public boolean isBackupEnabled = true;
    public String backupHour = "01";
    public boolean isCrashCheckEnabled = false;
    
    public PermanentOperationsConfig(String source) {
        this.isJoinLoggingEnabled = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "join_leave_logging", false, false));
        this.isStartLoggingEnabled = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "start_logging", false, false));
        this.isLoggingFull = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "full_console_logging", false, false));

        this.isLoggingEnabled =
                this.isJoinLoggingEnabled ||
                        this.isStartLoggingEnabled ||
                        this.isLoggingFull;

        this.isBackupEnabled = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "autobackup", false, false));
        this.backupHour = ConfigTools.findConfigOption(source, "autobackup_hour", false, false);
        this.isCrashCheckEnabled = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "crash_check", false, false));
    }
}
