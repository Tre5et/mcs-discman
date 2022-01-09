package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.tools.DiscordTools;
import net.treset.minecraft_server_discord_bot.tools.ServerTools;

public class StartServerCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";

        if(DiscordTools.isModerator(event)) {
            if(ServerTools.isServerRunning()) {
                output = "Server is already running.";
                DiscordBot.LOGGER.info("StartServer Command handled: already running");
            } else {
                ServerTools.startServer();

                output = "Starting the server... (this may take a few minutes)";
                DiscordBot.LOGGER.info("StartServer Command handled: success");
            }
        } else {
            output = "You don't have permission to do that.";
            DiscordBot.LOGGER.info("StartServer Command handled: permission required");
        }

        event.reply(output).queue();
    }
}
