package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.tools.ServerTools;

public class ActiveCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";

        if(ServerTools.isServerRunning()) output = "The server is **running**.";
        else output = "The server is **not** running.";

        event.reply(output).queue();

        MessageManager.log(String.format("Handled: %s.", (ServerTools.isServerRunning())? "running": "not running"), LogLevel.INFO);
    }
}
