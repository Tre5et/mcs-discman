package net.treset.minecraft_server_discord_bot;

import net.dv8tion.jda.api.JDA;
import net.treset.minecraft_server_discord_bot.networking.ConnectionManager;
import net.treset.minecraft_server_discord_bot.networking.CommunicationManager;
import net.treset.minecraft_server_discord_bot.networking.NetworkingManager;

public class  Main {

    public static JDA jda;

    public static void main(String[] args) {

        new Thread(DiscordBot::init).start();

        new Thread(NetworkingManager::init).start();
    }
}
