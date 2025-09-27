package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.tools.FormatTools;

import java.io.IOException;
import java.util.regex.Pattern;

public class ServerConfig extends BaseConfig {
    public final String server_path;
    public final String world_name;
    public final String backup_path;
    public final String start_command;
    public final int save_timeout;
    public final int stop_timeout;

    public final String world_path;


    public ServerConfig(String config) throws IOException {
        super(config);

        String srv_path = load("server_path");
        world_name = load("world_name");
        String bu_path = load("backup_path");
        start_command = load("start_command");
        save_timeout = FormatTools.stringToInt(load("save_timeout"), 10);
        stop_timeout = FormatTools.stringToInt(load("stop_timeout"), 30);

        if(srv_path == null || world_name == null || bu_path == null || start_command == null) {
            throw new IOException("Invalid server config. Options 'server_path', 'world_name', 'backup_path' and 'start_command' must all be set.");
        }

        if(FormatTools.matchRegex(srv_path, Pattern.compile("(/$)", Pattern.MULTILINE)).isEmpty()) { //account for / at the end
            this.server_path = srv_path + "/";
        } else this.server_path = srv_path;
        this.world_path = server_path + world_name;

        if(FormatTools.matchRegex(bu_path, Pattern.compile("(/$)", Pattern.MULTILINE)).isEmpty()) { //account for / at the end
            this.backup_path = bu_path + "/";
        } else this.backup_path = bu_path;
    }
}
