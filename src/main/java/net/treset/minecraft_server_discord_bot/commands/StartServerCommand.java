package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ServerActions;

public class StartServerCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;

        if(DiscordBot.isModerator(event)) {
            if(ServerActions.isRunning()) {
                output = "Server is already running.";

                Logger.info("Handled. Already running.");
            } else {
                ServerActions.startServer();

                output = "Starting the server... (this may take a few minutes)";

                Logger.info("Handled. Starting.");
            }
        } else {
            output = "You don't have permission to do that.";

            Logger.info("Handled. Permission required.");
        }

        event.getHook().sendMessage(output).queue();
    }
}
