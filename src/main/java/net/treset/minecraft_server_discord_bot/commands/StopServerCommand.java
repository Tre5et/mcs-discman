package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
import net.treset.minecraft_server_discord_bot.server.ConnectionManager;
import net.treset.minecraft_server_discord_bot.server.ServerActions;

public class StopServerCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;

        if(DiscordBot.isModerator(event)) {
            if(!ConnectionManager.isRunning()) {
                output = "Server is already stopped.";
                event.getHook().sendMessage(output).queue();
                Logger.info("Handled. Already stopped.");
            } else {
                output = "Stopping the server...";
                event.getHook().sendMessage(output).queue();
                Logger.info("Stopping server.");

                if(!ServerActions.stopServer()) {
                    output = "Server stop failed.";
                    DiscordBot.sendText(output, MessageOrigin.COMMAND);
                }
                output = "Server stopped.";
                DiscordBot.sendText(output, MessageOrigin.COMMAND);
            }
        } else {
            output = "You don't have permission to do that.";
            event.getHook().sendMessage(output).queue();
            Logger.info("Handled. Permission required.");
        }
    }
}
