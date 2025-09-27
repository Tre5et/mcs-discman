package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.messaging.MessageOrigin;
import net.treset.minecraft_server_discord_bot.tools.DiscordTools;
import net.treset.minecraft_server_discord_bot.tools.ServerTools;

public class RestartServerCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;

        if(DiscordTools.isModerator(event)) {
            if(ServerTools.isServerRunning()) {
                output = "Stopping the server for a restart...";
                event.getHook().sendMessage(output).queue();
                MessageManager.log("Stopping server.", LogLevel.INFO);

                if(!ServerTools.stopServer()) {
                    output = "Server stop failed.";
                    MessageManager.sendText(output, MessageOrigin.COMMAND);
                    return;
                }

                output = "Server stopped, restarting... (this may take a few minutes)";
                MessageManager.sendText(output, MessageOrigin.COMMAND);

                MessageManager.log("Stopped server.", LogLevel.INFO);
                try {
                    Thread.sleep(Config.server.restart_delay * 1000L);
                } catch (InterruptedException e) {
                    MessageManager.log("Failed to wait for restart delay", LogLevel.ERROR, e);
                }

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
