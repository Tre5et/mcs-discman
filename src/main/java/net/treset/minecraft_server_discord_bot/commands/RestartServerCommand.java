package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.tools.DiscordTools;
import net.treset.minecraft_server_discord_bot.tools.MiscTools;
import net.treset.minecraft_server_discord_bot.tools.ServerTools;

public class RestartServerCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";

        if(DiscordTools.isModerator(event)) {
            if(ServerTools.isServerRunning()) {
                ServerTools.stopServer();

                output = "Stopping the server for a restart...";
                event.reply(output).queue();
                DiscordBot.LOGGER.info("RestartServer Command handling started: stopping");

                int timeSinceStop = 0;
                while(ServerTools.isServerRunning()) {

                    MiscTools.timeout(2000);

                    timeSinceStop += 2;

                    if(timeSinceStop > 90) {
                        output = "Server stop failed. Try again.";
                        DiscordBot.BOT_CHANNEL.sendMessage(output).queue();

                        DiscordBot.LOGGER.info("RestartServer command FAILED: stop timeout");

                        PermanentOperations.isStopExpected = false;
                        return;
                    }
                }

                output = "Server stopped, restarting... (this may take a few minutes)";
                DiscordBot.BOT_CHANNEL.sendMessage(output).queue();

                DiscordBot.LOGGER.info("RestartServer Command handling: stopped");

            } else {
                output = "Restarting... (this may take a few minutes)";
                event.reply(output).queue();
            }
            ServerTools.startServer();

            DiscordBot.LOGGER.info("RestartServer Command handled: success");
        } else {
            output = "You don't have permission to do that.";
            event.reply(output).queue();

            DiscordBot.LOGGER.info("RestartServer Command handled: permission required");
        }
    }

    private static void waitForServerStop() {

    }
}
