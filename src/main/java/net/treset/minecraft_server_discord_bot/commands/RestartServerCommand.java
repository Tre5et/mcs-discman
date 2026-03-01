package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.exception.ServerOperationException;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ServerActions;

public class RestartServerCommand {
    public static void handleCommand(SlashCommandEvent event) {
        if(DiscordBot.isModerator(event)) {
            if(ServerActions.isRunning()) {
                event.getHook().sendMessage("Stopping the server for a restart...").queue();
                Logger.info("Stopping server.");

                try {
                    ServerActions.stopServer();
                } catch (ServerOperationException e) {
                    Logger.error(e, "Failed to stop server for restart");
                    event.getHook().sendMessage("Server stop failed.").queue();
                    return;
                }

                event.getHook().sendMessage("Server stopped, restarting... (this may take a few minutes)").queue();

                Logger.info("Stopped server.");
                try {
                    Thread.sleep(Config.get().server.restartDelay * 1000L);
                } catch (InterruptedException e) {
                    Logger.error(e, "Failed to wait for restart delay");
                }

            } else {
                event.getHook().sendMessage("Restarting... (this may take a few minutes)").queue();
            }
            try {
                ServerActions.startServer();
                event.getHook().sendMessage("Server restarted!").queue();
            } catch (ServerOperationException e) {
                event.getHook().sendMessage("Failed to restart server").queue();
                Logger.error(e, "Failed to restart server");
            }

            Logger.info("Handled. Restarted.");
        } else {
            event.getHook().sendMessage("You don't have permission to do that.").queue();
            Logger.info("Handled. Permission required.");
        }
    }
}
