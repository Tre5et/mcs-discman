package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
import net.treset.minecraft_server_discord_bot.server.ServerActions;

public class RestartServerCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;

        if(DiscordBot.isModerator(event)) {
            if(ServerActions.isServerRunning()) {
                output = "Stopping the server for a restart...";
                event.getHook().sendMessage(output).queue();
                Logger.info("Stopping server.");

                if(!ServerActions.stopServer()) {
                    output = "Server stop failed.";
                    DiscordBot.sendText(output, MessageOrigin.COMMAND);
                    return;
                }

                output = "Server stopped, restarting... (this may take a few minutes)";
                DiscordBot.sendText(output, MessageOrigin.COMMAND);

                Logger.info("Stopped server.");
                try {
                    Thread.sleep(Config.server.restart_delay * 1000L);
                } catch (InterruptedException e) {
                    Logger.error(e, "Failed to wait for restart delay");
                }

            } else {
                output = "Restarting... (this may take a few minutes)";
                event.getHook().sendMessage(output).queue();
            }
            ServerActions.startServer();

            Logger.info("Handled. Restarting.");
        } else {
            output = "You don't have permission to do that.";
            event.getHook().sendMessage(output).queue();

            Logger.info("Handled. Permission required.");
        }
    }
}
