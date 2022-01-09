package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.tools.ServerTools;

public class ActiveCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";

        if(ServerTools.isServerRunning()) output = "The server is **running**.";
        else output = "The server is **not** running.";

        event.reply(output).queue();

        DiscordBot.LOGGER.info("Active Command handled.");
    }
}
