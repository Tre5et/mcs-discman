package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.RpcMessager;
import net.treset.minecraft_server_discord_bot.server.data.RpcPlayer;
import net.treset.minecraft_server_discord_bot.server.schemas.RpcResponse;
import net.treset.minecraft_server_discord_bot.system.Formatter;

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

        output = String.format("There %s online%s", (players.size() == 1) ? "is **1** player" : "are **" + players.size() + "** players",  (players.isEmpty()) ? "." : ":**\n" + Formatter.formatList(players, "\n") + "**");

        event.getHook().sendMessage(output).queue();

        Logger.info("Handled.");
    }

    private static List<String> getPlayers() {
        try {
            RpcResponse res = RpcMessager.request("minecraft:players");
            try {
                return RpcPlayer.fromList(res.result()).stream().map(RpcPlayer::name).toList();
            } catch (IOException e) {
                Logger.warn(e, "Failed to extract players for players command");
                return null;
            }
        } catch (IOException e) {
            Logger.warn(e,"Failed to update players for players command.");
            return null;
        }
    }
}
