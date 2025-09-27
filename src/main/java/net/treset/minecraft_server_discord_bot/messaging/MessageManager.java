package net.treset.minecraft_server_discord_bot.messaging;

import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.config.Config;

public class MessageManager {
    private static void sendMessageToDiscord(String message) {
        if(DiscordBot.BOT_CHANNEL == null) return;
        DiscordBot.BOT_CHANNEL.sendMessage(message).queue();
    }

    public static void sendText(String text, MessageOrigin org) {
        sendMessageToDiscord(text);

        log(String.format("Sent message \"%s\" %s.", text, org.getMessage()), LogLevel.INFO);
    }

    public static void log(String message, LogLevel level) {
        log(message, level, null);
    }

    public static void log(String message, LogLevel level, Exception e) {
        String org = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().getSimpleName();
        String msg = org + ": " + message;
        switch(level) {
            case DEBUG -> {
                if(Config.discord.debug) {
                    if (e == null) {
                        DiscordBot.LOGGER.debug(msg);
                    } else {
                        DiscordBot.LOGGER.debug(msg, e);
                    }
                }
            }
            case INFO -> {
                if (e == null) {
                    DiscordBot.LOGGER.info(msg);
                } else {
                    DiscordBot.LOGGER.info(msg, e);
                }
            }
            case WARN -> {
                if (e == null) {
                    DiscordBot.LOGGER.warn(msg);
                } else {
                    DiscordBot.LOGGER.warn(msg, e);
                }
            }
            case ERROR -> {
                if (e == null) {
                    DiscordBot.LOGGER.error(msg);
                } else {
                    DiscordBot.LOGGER.error(msg, e);
                }
            }
        }
    }
}
