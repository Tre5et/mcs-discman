package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.messaging.MessageOrigin;
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
                event.getHook().sendMessage(output).queue();
                MessageManager.log("Stopping server.", LogLevel.INFO);

                int timeSinceStop = 0;
                while(ServerTools.isServerRunning()) {

                    MiscTools.timeout(2000);

                    timeSinceStop += 2;

                    if(timeSinceStop > 90) {
                        output = "Server stop failed. Try again.";
                        MessageManager.sendText(output, MessageOrigin.COMMAND);

                        MessageManager.log("Stop timed out.", LogLevel.ERROR);

                        PermanentOperations.isStopExpected = false;
                        return;
                    }
                }

                output = "Server stopped, restarting... (this may take a few minutes)";
                MessageManager.sendText(output, MessageOrigin.COMMAND);

                MessageManager.log("Stopped server.", LogLevel.INFO);

            } else {
                output = "Restarting... (this may take a few minutes)";
                event.getHook().sendMessage(output).queue();
            }
            ServerTools.startServer();

            MessageManager.log("Handled. Restarting.", LogLevel.INFO);
        } else {
            output = "You don't have permission to do that.";
            event.getHook().sendMessage(output).queue();

            MessageManager.log("Handled. Permission required.", LogLevel.INFO);
        }
    }
}
