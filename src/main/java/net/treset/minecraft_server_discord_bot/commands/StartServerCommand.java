package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.tools.DiscordTools;
import net.treset.minecraft_server_discord_bot.tools.ServerTools;

public class StartServerCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";

        if(DiscordTools.isModerator(event)) {
            if(ServerTools.isServerRunning()) {
                output = "Server is already running.";

                MessageManager.log("Handled. Already running.", LogLevel.INFO);
            } else {
                ServerTools.startServer();

                output = "Starting the server... (this may take a few minutes)";

                MessageManager.log("Handled. Starting.", LogLevel.INFO);
            }
        } else {
            output = "You don't have permission to do that.";

            MessageManager.log("Handled. Permission required.", LogLevel.INFO);
        }

        event.reply(output).queue();
    }
}
