package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.rpc.MessageHandler;
import net.treset.minecraft_server_discord_bot.rpc.data.RpcPlayer;
import net.treset.minecraft_server_discord_bot.rpc.schemas.RpcResponse;
import net.treset.minecraft_server_discord_bot.tools.FormatTools;

import java.io.IOException;
import java.util.List;

public class OnlineCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;

        List<String> players = getPlayers();
        if(players == null) {
            output = "Failed to get players!";
            event.getHook().sendMessage(output).queue();
            return;
        }

        output = String.format("There %s online%s", (players.size() == 1) ? "is **1** player" : "are **" + players.size() + "** players",  (players.isEmpty()) ? "." : ":**\n" + FormatTools.formatList(players, "\n") + "**");

        event.getHook().sendMessage(output).queue();

        MessageManager.log("Handled.", LogLevel.INFO);
    }

    private static List<String> getPlayers() {
        try {
            RpcResponse res = MessageHandler.sendBlocking("minecraft:players");
            try {
                return RpcPlayer.extractPlayersFromResponse(res);
            } catch (IOException e) {
                MessageManager.log("Failed to extract players for players command", LogLevel.WARN, e);
                return null;
            }
        } catch (IOException e) {
            MessageManager.log("Failed to update players for players command.", LogLevel.WARN, e);
            return null;
        }
    }
}
