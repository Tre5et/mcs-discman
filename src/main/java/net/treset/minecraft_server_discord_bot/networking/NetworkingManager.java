package net.treset.minecraft_server_discord_bot.networking;

import net.treset.minecraft_server_discord_bot.DiscordBot;

public class NetworkingManager {

    public static void init() {
        if(!ConnectionManager.init()) return;

        if(!ConnectionManager.openConnection()) return;

        CommunicationManager.handleData();
    }

    public static void sendMessageToDiscord(String message) {
        DiscordBot.BOT_CHANNEL.sendMessage(message).queue();
    }
}
