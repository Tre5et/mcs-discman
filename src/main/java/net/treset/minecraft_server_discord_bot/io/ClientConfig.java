package net.treset.minecraft_server_discord_bot.io;

import net.treset.minecraft_server_discord_bot.tools.ConfigTools;
import net.treset.minecraft_server_discord_bot.tools.FormatTools;

public class ClientConfig {
    public boolean CLIENT_ENABLED;
    public int PORT;
    public boolean DEATH_LOGGING;
    public boolean OVERRIDE_CONSOLE_READER;

    public ClientConfig(String source) {
        this.CLIENT_ENABLED = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "enable_client", false, false));
        this.PORT = FormatTools.stringToInt(ConfigTools.findConfigOption(source, "port", false, true));
        this.DEATH_LOGGING = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "death_logging", false, true));
        this.OVERRIDE_CONSOLE_READER = FormatTools.stringToBoolean(ConfigTools.findConfigOption(source, "override_console", false, true));
    }
}
