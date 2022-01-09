package net.treset.minecraft_server_discord_bot.tools;


import net.treset.minecraft_server_discord_bot.DiscordBot;

import java.util.concurrent.TimeUnit;

public class MiscTools {
    public static void timeout(int ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
            DiscordBot.LOGGER.info("Timeout WARNING: timeout interrupted");
        }
    }
}
