package net.treset.minecraft_server_discord_bot;

import net.dv8tion.jda.api.JDA;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
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

        try {
            DiscordBot.initialize();
        } catch (IOException e) {
            Logger.error(e, "Failed to initialize discord client.");
            System.exit(2);
        }

        DiscordBot.sendText("Hi, I'm online now.", MessageOrigin.SCHEDULE);

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
