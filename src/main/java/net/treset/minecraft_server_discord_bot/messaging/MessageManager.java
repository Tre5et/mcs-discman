package net.treset.minecraft_server_discord_bot.messaging;

import net.treset.minecraft_server_discord_bot.DiscordBot;

public class MessageManager {
    private static void sendMessageToDiscord(String message) {
        DiscordBot.BOT_CHANNEL.sendMessage(message).queue();
    }

    public static void sendText(String text, MessageOrigin org) {
        sendMessageToDiscord(text);

        log(MessageManager.class.getSimpleName(), String.format("Sent message \"%s\" %s.", text, org.getMessage()), LogLevel.INFO);
    }

    public static void sendJoin(String player, MessageOrigin org) {
        sendMessageToDiscord(String.format("%s joined the game.", player));

        log(MessageManager.class.getSimpleName(), String.format("Sent join of player %s %s.", player, org.getMessage()), LogLevel.INFO);
    }

    public static void sendLeave(String player, MessageOrigin org) {
        sendMessageToDiscord(String.format("%s left the game.", player));

        log(MessageManager.class.getSimpleName(), String.format("Sent leave of player %s %s.", player, org.getMessage()), LogLevel.INFO);
    }

    public static void sendDeath(String message, MessageOrigin org) {
        sendMessageToDiscord(message + ".");

        log(MessageManager.class.getSimpleName(), String.format("Sent death \"%s\" %s.", message, org.getMessage()), LogLevel.INFO);
    }

    public static void log(String origin, String message, LogLevel level) {
        String msg = origin + ": " + message;
        switch(level) {
            case DEBUG -> DiscordBot.LOGGER.debug(msg);
            case INFO -> DiscordBot.LOGGER.info(msg);
            case WARN -> DiscordBot.LOGGER.warn(msg);
            case ERROR -> DiscordBot.LOGGER.error(msg);
        }
    }
}
