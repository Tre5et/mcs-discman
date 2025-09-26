package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.tools.FormatTools;

import java.io.IOException;

public class CommunicationConfig extends BaseConfig {
    public final String server_host;
    public final int server_port;
    public final String server_secret;
    public final boolean use_ssl;

    public CommunicationConfig(String config) throws IOException {
        super(config);

        server_host = load("server_host");
        server_port = FormatTools.stringToInt(load("server_port"));
        server_secret = load("server_secret");
        use_ssl = FormatTools.stringToBoolean(load("use_ssl"));

        if(server_host == null || server_port == -1 || server_secret == null && !isEnabled()) {
            throw new IOException("Invalid communication config. Options 'server_host', 'server_port' and 'server_secret' must all either be set or unset.");
        }
    }

    public boolean isEnabled() {
        return server_host != null && server_port != -1 && server_secret != null;
    }
}
