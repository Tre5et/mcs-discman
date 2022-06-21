package net.treset.minecraft_server_discord_bot;

import net.dv8tion.jda.api.JDA;
import net.treset.minecraft_server_discord_bot.networking.ConnectionManager;
import net.treset.minecraft_server_discord_bot.networking.DataReciever;

public class  Main {

    public static JDA jda;

    public static void main(String[] args) {
        //DiscordBot.init();

        if(!ConnectionManager.init()) return;
        if(!ConnectionManager.establishConnection()) return;
        DataReciever.handleData();
    }
}
