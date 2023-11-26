package net.treset.minecraft_server_discord_bot.messaging;

import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.tools.ConfigTools;

public class MessageManager {
    private static void sendMessageToDiscord(String message) {
        DiscordBot.BOT_CHANNEL.sendMessage(message).queue();
    }

    public static void sendText(String text, MessageOrigin org) {
        sendMessageToDiscord(text);

        log(String.format("Sent message \"%s\" %s.", text, org.getMessage()), LogLevel.INFO);
    }

    public static void sendStarted(MessageOrigin org) {
        sendMessageToDiscord("Server started.");

        log(String.format("Sent server start %s.", org.getMessage()), LogLevel.INFO);

        PermanentOperations.setSomethingHappened();
    }

    public static void sendStopped(MessageOrigin org) {
        sendMessageToDiscord("Server stopped.");

        log(String.format("Sent server stop %s.", org.getMessage()), LogLevel.INFO);

        PermanentOperations.setSomethingHappened();
    }

    public static void sendJoin(String player, MessageOrigin org) {
        if(!ConfigTools.PERMA_CONFIG.JOIN_LOGGING || !ConfigTools.CLIENT_CONFIG.OVERRIDE_CONSOLE_READER) return;

        sendMessageToDiscord(String.format("%s joined the game.", player));

        log(String.format("Sent join of player %s %s.", player, org.getMessage()), LogLevel.INFO);

        ConfigTools.addPlayer(player);

        PermanentOperations.setSomethingHappened();
    }

    public static void sendLeave(String player, MessageOrigin org) {
        if(!ConfigTools.PERMA_CONFIG.JOIN_LOGGING || !ConfigTools.CLIENT_CONFIG.OVERRIDE_CONSOLE_READER) return;

        sendMessageToDiscord(String.format("%s left the game.", player));

        log(String.format("Sent leave of player %s %s.", player, org.getMessage()), LogLevel.INFO);

        ConfigTools.removePlayer(player);

        PermanentOperations.setSomethingHappened();
    }

    public static void sendDeath(String message, MessageOrigin org) {
        if(!ConfigTools.CLIENT_CONFIG.DEATH_LOGGING) return;

        sendMessageToDiscord(message + ".");

        log(String.format("Sent death \"%s\" %s.", message, org.getMessage()), LogLevel.INFO);

        PermanentOperations.setSomethingHappened();
    }

    public static void sendAdvancement(String message, MessageOrigin org) {
        if(!ConfigTools.CLIENT_CONFIG.ADVANCEMENT_LOGGING) return;

        message = message.replaceAll("[\\[\\]]", "\"");

        sendMessageToDiscord(message + ".");

        log(String.format("Sent advancement \"%s\" %s.", message, org.getMessage()), LogLevel.INFO);

        PermanentOperations.setSomethingHappened();
    }

    public static void log(String message, LogLevel level) {
        log(message, level, null);
    }

    public static void log(String message, LogLevel level, Exception e) {
        String org = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().getSimpleName();
        String msg = org + ": " + message;
        switch(level) {
            case DEBUG -> {
                if(ConfigTools.CONFIG.DEBUG) {
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
