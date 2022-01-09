package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.tools.DiscordTools;
import net.treset.minecraft_server_discord_bot.tools.ServerTools;

import java.util.concurrent.TimeUnit;

public class StopServerCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";

        if(DiscordTools.isModerator(event)) {
            if(!ServerTools.isServerRunning()) {
                output = "Server is already stopped.";
                event.reply(output).queue();
                DiscordBot.LOGGER.info("StopServer Command handled: already stopped");
            } else {
                ServerTools.stopServer();

                output = "Stopping the server...";
                event.reply(output).queue();
                DiscordBot.LOGGER.info("StopServer Command handling started: stopping");

                int timeSinceStop = 0;
                while(ServerTools.isServerRunning()) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                    timeSinceStop += 2;

                    if(timeSinceStop > 90) {
                        output = "Server stop failed. Try again.";
                        DiscordBot.BOT_CHANNEL.sendMessage(output).queue();

                        DiscordBot.LOGGER.info("StopServer command FAILED: timeout");

                        PermanentOperations.isStopExpected = false;

                        return;
                    }
                }

                output = "Server stopped.";
                DiscordBot.BOT_CHANNEL.sendMessage(output).queue();

                DiscordBot.LOGGER.info("StopServer Command handled: success");
            }
        } else {
            output = "You don't have permission to do that.";
            event.reply(output).queue();
            DiscordBot.LOGGER.info("StopServer Command handled: permission required");
        }
    }
}
