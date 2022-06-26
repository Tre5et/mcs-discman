package net.treset.minecraft_server_discord_bot.tools;


import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;

import java.util.concurrent.TimeUnit;

public class MiscTools {
    public static void timeout(int ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            MessageManager.log("Timeout interrupted. -> Stacktrace.", LogLevel.ERROR);
            e.printStackTrace();
        }
    }
}
