package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.tools.FormatTools;

import java.io.IOException;

public class CommunicationConfig extends BaseConfig {
    public final String server_host;
    public final int server_port;
    public final String server_secret;
    public final int rpc_startup_delay;
    public final boolean use_ssl;

    public CommunicationConfig(String config) throws IOException {
        super(config);

        server_host = load("server_host");
        server_port = FormatTools.stringToInt(load("server_port"));
        server_secret = load("server_secret");
        rpc_startup_delay = FormatTools.stringToInt(load("rpc_startup_delay"), 5);
        use_ssl = FormatTools.stringToBoolean(load("use_ssl"));

        if(server_host == null || server_port == -1 || server_secret == null) {
            throw new IOException("Invalid communication config. Options 'server_host', 'server_port' and 'server_secret' must be set.");
        }
        if(rpc_startup_delay < 0) {
            throw new IOException("Invalid communication config. Option 'rpc_startup_delay' must be >= 0");
        }
    }
}
