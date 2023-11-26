package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.networking.CommunicationManager;
import net.treset.minecraft_server_discord_bot.tools.ConfigTools;
import net.treset.minecraft_server_discord_bot.tools.FormatTools;

import java.io.IOException;
import java.util.List;

public class OnlineCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;

        CommunicationManager.tryUpdatePlayers();

        try {
            ConfigTools.loadDetails();
        } catch (IOException e) {
            MessageManager.log("Failed to load players.", LogLevel.ERROR, e);
            event.getHook().sendMessage("Failed to load players, try again.").queue();
        }

        List<String> players = ConfigTools.PLAYERS;

        output = String.format("There %s online%s", (players.size() == 1) ? "is **1** player" : "are **" + players.size() + "** players",  (players.isEmpty()) ? "." : ":**\n" + FormatTools.formatList(players, "\n") + "**");

        event.getHook().sendMessage(output).queue();

        MessageManager.log("Handled.", LogLevel.INFO);
    }
}
