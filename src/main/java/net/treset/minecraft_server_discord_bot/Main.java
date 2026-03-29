package net.treset.minecraft_server_discord_bot;

import net.dv8tion.jda.api.JDA;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;

import java.io.IOException;

public class  Main {

    public static JDA jda;

    public static void main(String[] args) {
        try {
            Config.load();
        } catch (ConfigException e) {
            Logger.error(e, "Failed to load config.");
            System.exit(1);
        }

        Config.get().events.online.sendToDiscord();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ManagementClient.get().disconnect();
            } catch (IOException e) {
                ManagementClient.get().forceDisconnect();
            }
        }));

        //new Thread(PermanentOperations::permanentLoop).start();
    }
}
