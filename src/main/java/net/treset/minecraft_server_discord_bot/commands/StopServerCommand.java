package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.messaging.MessageOrigin;
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
                MessageManager.log("Handled. Already stopped.", LogLevel.INFO);
            } else {
                ServerTools.stopServer();

                output = "Stopping the server...";
                event.reply(output).queue();
                MessageManager.log("Stopping server.", LogLevel.INFO);

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
                        MessageManager.sendText(output, MessageOrigin.COMMAND);

                        MessageManager.log("Stop timed out.", LogLevel.ERROR);

                        PermanentOperations.isStopExpected = false;

                        return;
                    }
                }

                MessageManager.sendStopped(MessageOrigin.COMMAND);

                MessageManager.log("Handled. Stopped.", LogLevel.INFO);
            }
        } else {
            output = "You don't have permission to do that.";
            event.reply(output).queue();
            MessageManager.log("Handled. Permission required.", LogLevel.INFO);
        }
    }
}
