package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.system.Formatter;

import java.io.IOException;
import java.util.regex.Pattern;

public class ServerConfig extends BaseConfig {
    public final String server_path;
    public final String world_name;
    public final String backup_path;
    public final String start_command;
    public final int restart_delay;
    public final int save_timeout;
    public final int stop_timeout;
    public final int backup_hour;
    public final String backup_hour_formatted;
    public final boolean backup_enabled;
    public final int backup_timeout;
    public final boolean log_no_backup;
    public final boolean auto_restart;
    public final int inactivity_reminder;
    public final boolean inactivity_reminder_enabled;

    public final String world_path;


    public ServerConfig(String config) throws IOException {
        super(config);

        String srv_path = load("server_path");
        world_name = load("world_name");
        String bu_path = load("backup_path");
        start_command = load("start_command");

        if(srv_path == null || world_name == null || bu_path == null || start_command == null) {
            throw new IOException("Invalid server config. Options 'server_path', 'world_name', 'backup_path' and 'start_command' must all be set.");
        }

        if(Formatter.matchRegex(srv_path, Pattern.compile("(/$)", Pattern.MULTILINE)).isEmpty()) { //account for / at the end
            this.server_path = srv_path + "/";
        } else this.server_path = srv_path;
        this.world_path = server_path + world_name;

        if(Formatter.matchRegex(bu_path, Pattern.compile("(/$)", Pattern.MULTILINE)).isEmpty()) { //account for / at the end
            this.backup_path = bu_path + "/";
        } else this.backup_path = bu_path;

        restart_delay = Formatter.stringToInt(load("restart_delay"), 5);
        save_timeout = Formatter.stringToInt(load("save_timeout"), 10);
        stop_timeout = Formatter.stringToInt(load("stop_timeout"), 30);

        backup_hour = Formatter.stringToInt(load("backup_hour"));
        backup_hour_formatted = String.format("%02d", backup_hour);
        backup_enabled = backup_hour >= 0 && backup_hour < 24;
        backup_timeout = Formatter.stringToInt(load("backup_timeout"), 1200);
        log_no_backup = Formatter.stringToBoolean(load("log_no_backup"));
        auto_restart = Formatter.stringToBoolean(load("auto_restart"));
        inactivity_reminder = Formatter.stringToInt(load("inactivity_reminder"));
        inactivity_reminder_enabled = inactivity_reminder > 0;
    }
}
