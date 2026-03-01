package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.exception.ServerOperationException;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ServerActions;

public class StopServerCommand {
    public static void handleCommand(SlashCommandEvent event) {
        if(DiscordBot.isModerator(event)) {
            if(!ServerActions.isRunning()) {
                event.getHook().sendMessage("Server is already stopped.").queue();
                Logger.info("Handled. Already stopped.");
            } else {
                event.getHook().sendMessage("Stopping the server...").queue();
                Logger.info("Stopping server.");

                try {
                    ServerActions.stopServer();
                } catch (ServerOperationException e) {
                    Logger.error(e, "Failed to stop server");
                    event.getHook().sendMessage("Failed to stop server.").queue();
                }
                event.getHook().sendMessage("Server stopped.").queue();
            }
        } else {
            event.getHook().sendMessage("You don't have permission to do that.").queue();
            Logger.info("Handled. Permission required.");
        }
    }
}
