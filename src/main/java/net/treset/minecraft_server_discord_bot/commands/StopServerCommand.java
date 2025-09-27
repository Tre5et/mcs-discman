package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.rpc.ConnectionManager;
import net.treset.minecraft_server_discord_bot.tools.DiscordTools;
import net.treset.minecraft_server_discord_bot.tools.ServerTools;

public class StopServerCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;

        if(DiscordTools.isModerator(event)) {
            if(!ConnectionManager.isRunning()) {
                output = "Server is already stopped.";
                event.getHook().sendMessage(output).queue();
                MessageManager.log("Handled. Already stopped.", LogLevel.INFO);
            } else {
                output = "Stopping the server...";
                event.getHook().sendMessage(output).queue();
                MessageManager.log("Stopping server.", LogLevel.INFO);

                if(!ServerTools.stopServer()) {
                    output = "Server stop failed.";
                    event.getHook().sendMessage(output).queue();
                }
                output = "Server stopped.";
                event.getHook().sendMessage(output).queue();
            }
        } else {
            output = "You don't have permission to do that.";
            event.getHook().sendMessage(output).queue();
            MessageManager.log("Handled. Permission required.", LogLevel.INFO);
        }
    }
}
