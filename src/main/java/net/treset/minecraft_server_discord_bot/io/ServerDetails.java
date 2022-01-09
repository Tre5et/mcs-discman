package net.treset.minecraft_server_discord_bot.io;

import net.treset.minecraft_server_discord_bot.tools.ConfigTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ServerDetails {
    public String server;
    public String version;
    public List<String> mods = new ArrayList<>();
    public List<String> members = new ArrayList<>();
    public String backupLocation;
    public String url;
    public String admin;

    public ServerDetails(String source) {
        this.server = ConfigTools.findConfigOption(source, "server", false, false);
        this.version = ConfigTools.findConfigOption(source, "version", false, false);
        this.backupLocation = ConfigTools.findConfigOption(source, "backup_location", false, true);
        this.url = ConfigTools.findConfigOption(source, "url", false, false);
        this.admin = ConfigTools.findConfigOption(source, "admin", false, false);

        String[] modsArray = ConfigTools.findConfigOption(source, "mods", true, true).split(",");
        this.mods = new LinkedList<>(Arrays.asList(modsArray));
        this.mods.removeIf(""::equals);
        String[] membersArray = ConfigTools.findConfigOption(source, "members", true, true).split(",");
        this.members = new LinkedList<>(Arrays.asList(membersArray));
        this.members.removeIf(""::equals);
    }
}
