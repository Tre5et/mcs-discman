package net.treset.minecraft_server_discord_bot.server;

import dev.treset.mcdl.servermanagement.ManagementHandler;
import dev.treset.mcdl.servermanagement.ServerManagementDL;
import net.treset.minecraft_server_discord_bot.config.Config;

import java.io.IOException;

public class ManagementClient {
    private static final ManagementHandler handler = ServerManagementDL.createHandler(
            Config.communication.server_host,
            Config.communication.server_port,
            Config.communication.use_ssl,
            Config.communication.server_secret
    );
    private static boolean initialized = false;

    public static void init() throws IOException {
        try {
            handler.connect();
        } catch (IOException e) {
            initialized = true;
            throw e;
        }
        initialized = true;
    }

    public static ManagementHandler get() {
        if(!initialized) {
            throw new IllegalStateException("Handler not initialized");
        }
        return handler;
    }
}
