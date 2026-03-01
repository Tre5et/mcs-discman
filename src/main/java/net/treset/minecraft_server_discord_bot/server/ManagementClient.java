package net.treset.minecraft_server_discord_bot.server;

import dev.treset.mcdl.servermanagement.ManagementHandler;
import dev.treset.mcdl.servermanagement.ServerManagementDL;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.io.IOException;

public class ManagementClient {
    private static ManagementHandler handler = null;
    private static boolean initialized = false;

    public static void init() {
        if(handler != null) {
            try {
                if(handler.isConnected()) handler.disconnect();
            } catch (IOException e) {
                Logger.warn(e, "Failed to disconnect from old handler, forcing");
                handler.forceDisconnect();
            }
        }
        handler = ServerManagementDL.createHandler(
                Config.get().server.host,
                Config.get().server.port,
                Config.get().server.ssl,
                Config.get().server.secret
        );
        try {
            handler.connect();
        } catch (IOException ignored) {}
        initialized = true;
    }

    public static ManagementHandler get() {
        if(!initialized) {
            throw new IllegalStateException("Handler not initialized");
        }
        return handler;
    }
}
