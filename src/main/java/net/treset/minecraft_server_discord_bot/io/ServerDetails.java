package net.treset.minecraft_server_discord_bot.io;

import net.treset.minecraft_server_discord_bot.tools.ConfigTools;
import net.treset.minecraft_server_discord_bot.tools.DataTools;
import net.treset.minecraft_server_discord_bot.tools.ServerType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ServerDetails {
    public String server;
    private ServerType serverType;
    public String version;
    public List<String> mods = new ArrayList<>();
    public List<String> members = new ArrayList<>();
    public String backupLocation;
    public String url;
    public String admin;

    public ServerDetails(String source) throws IOException {
        if(ConfigTools.CONFIG.AUTODETECT_DETAILS) {
            this.serverType = DataTools.loadServerType();
            if (!this.serverType.isCertain()) {
                this.server = ConfigTools.findConfigOption(source, "server", false, true);
            }
            if(this.server == null || this.server.isBlank()) {
                this.server = this.serverType.toString();
            }
        } else {
            this.server = ConfigTools.findConfigOption(source, "server", false, false);
        }

        if(ConfigTools.CONFIG.AUTODETECT_DETAILS) {
            this.version = DataTools.loadVersion();
            if( this.version.equals("??")) {
                this.version = ConfigTools.findConfigOption(source, "version", false, true);
            }
        }
        else {
            this.version = ConfigTools.findConfigOption(source, "version", false, false);
        }

        this.backupLocation = ConfigTools.findConfigOption(source, "backup_location", false, true);
        this.url = ConfigTools.findConfigOption(source, "url", false, false);
        this.admin = ConfigTools.findConfigOption(source, "admin", false, false);

        if(ConfigTools.CONFIG.AUTODETECT_DETAILS && (this.serverType == ServerType.FABRIC || this.serverType == ServerType.FABRIC_L)) {
            this.mods = DataTools.loadFabricMods();
        }
        if(this.mods == null || this.mods.isEmpty()) {
            String[] modsArray = ConfigTools.findConfigOption(source, "mods", true, true).split(",");
            this.mods = new LinkedList<>(Arrays.asList(modsArray));
            this.mods.removeIf(""::equals);
        }

        if(ConfigTools.CONFIG.AUTODETECT_DETAILS) {
            this.members = DataTools.loadWhitelist();
        }
        if(this.members == null || this.members.isEmpty()) {
            String[] membersArray = ConfigTools.findConfigOption(source, "members", true, true).split(",");
            this.members = new LinkedList<>(Arrays.asList(membersArray));
            this.members.removeIf(""::equals);
        }
    }
}
