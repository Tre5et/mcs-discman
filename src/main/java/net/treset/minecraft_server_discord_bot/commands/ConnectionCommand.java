package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.networking.ConnectionManager;
import net.treset.minecraft_server_discord_bot.tools.DiscordTools;

import java.util.Objects;

public class ConnectionCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";

        if(DiscordTools.isModerator(event)) {
            String type = Objects.requireNonNull(event.getOption("action")).getAsString();
            switch (type) {
                case "status" -> output = getStatus();
                case "open" -> output = openConnection();
                case "close" -> output = closeConnection();
                default -> output = "not found";
            }
        } else {
            output = "You don't have permission to do that.";
            MessageManager.log("Handled. No permission.", LogLevel.INFO);
        }

        event.getHook().sendMessage(output).queue();
    }

    private static String getStatus() {
        if(ConnectionManager.isConnected()) {
            return String.format("A connection is open with the id %s.", ConnectionManager.getSessionId());
        }
        if(ConnectionManager.isWaitingForConnection()) {
            return "The bot is waiting for a connection.";
        }
        return "No connection is open.";
    }

    private static String openConnection() {
        if(ConnectionManager.isConnected() || ConnectionManager.isWaitingForConnection()) {
            return "The connection is already open. Close it first.";
        }
        if(ConnectionManager.openConnection()) {
            return "Connection successfully opened.";
        }
        return "Failed to open the connection. Try again.";
    }

    private static String closeConnection() {
        if(!ConnectionManager.isConnected() && !ConnectionManager.isWaitingForConnection()) {
            return "No connection is open. Open one first.";
        }
        if(ConnectionManager.closeConnection(false, false)) {
            return "Connection closed successfully.";
        }
        return "Failed to close the connection. Try again.";
    }
}
