package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ConnectionManager;

import java.io.IOException;
import java.util.Objects;

public class ConnectionCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;

        if(DiscordBot.isModerator(event)) {
            String type = Objects.requireNonNull(event.getOption("action")).getAsString();
            switch (type) {
                case "status" -> output = getStatus();
                case "open" -> output = openConnection();
                case "close" -> output = closeConnection(event);
                default -> output = "not found";
            }
        } else {
            output = "You don't have permission to do that.";
            Logger.info("Handled. No permission.");
        }

        event.getHook().sendMessage(output).queue();
    }

    private static String getStatus() {
        if(ConnectionManager.isConnected()) {
            return "A connection with the server is open.";
        }
        return "No connection is open.";
    }

    private static String openConnection() {
        if(ConnectionManager.isConnected()) {
            return "The connection is already open. Close it first.";
        }
        try {
            ConnectionManager.connect();
        } catch (IOException e) {
            return "Failed to connect to server. Try again.";
        }
        return "Connected to server.";
    }

    private static String closeConnection(SlashCommandEvent event) {
        if(!ConnectionManager.isConnected()) {
            return "No connection is open. Open one first.";
        }

        boolean force = false;
        if(event.getOption("action") != null) {
            force = Objects.requireNonNull(event.getOption("action")).getAsBoolean();
        }

        if(force) {
            ConnectionManager.forceDisconnect();
            return "Forcefully closed connection.";
        }

        try {
            if(ConnectionManager.disconnect()) {
                return "Connection closed successfully.";
            }
            return "Failed to close the connection. Try again.";
        } catch (IOException e) {
            return "Failed to close the connection. Try again.";
        }
    }
}
