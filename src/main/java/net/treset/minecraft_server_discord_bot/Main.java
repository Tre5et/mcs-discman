package net.treset.minecraft_server_discord_bot;

import net.dv8tion.jda.api.JDA;
import net.treset.minecraft_server_discord_bot.networking.DataReciever;

import java.io.IOException;

public class  Main {

    public static JDA jda;

    public static void main(String[] args) {
        //DiscordBot.init();

        try {
            DataReciever.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
