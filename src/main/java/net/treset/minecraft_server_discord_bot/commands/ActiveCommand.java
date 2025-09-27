package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.rpc.ConnectionManager;

public class ActiveCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;

        if(ConnectionManager.isRunning()) output = "The server is **running**.";
        else output = "The server is **not** running.";

        event.getHook().sendMessage(output).queue();

        MessageManager.log(String.format("Handled: %s.", (ConnectionManager.isRunning())? "running": "not running"), LogLevel.INFO);
    }
}
