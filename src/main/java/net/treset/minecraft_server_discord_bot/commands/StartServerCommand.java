package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.exception.ServerOperationException;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ServerActions;

public class StartServerCommand {
    public static void handleCommand(SlashCommandEvent event) {
        if(DiscordBot.isModerator(event)) {
            if(ServerActions.isRunning()) {
                event.getHook().sendMessage("Server is already running.").queue();

                Logger.info("Handled. Already running.");
            } else {
                event.getHook().sendMessage("Starting the server... (this may take a few minutes)").queue();
                try {
                    ServerActions.startServer();
                    event.getHook().sendMessage("Started!").queue();
                } catch (ServerOperationException e) {
                    Logger.error(e, "Failed to start server");
                    event.getHook().sendMessage("Failed to start server").queue();
                }
                Logger.info("Handled.");
            }
        } else {
            event.getHook().sendMessage("You don't have permission to do that.").queue();
            Logger.info("Handled. Permission required.");
        }
    }
}
