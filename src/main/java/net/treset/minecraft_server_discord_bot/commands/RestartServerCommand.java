package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.exception.ServerOperationException;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ServerActions;

import java.util.function.Supplier;

public class RestartServerCommand extends Command<CommandConfig.Restart> {
    public RestartServerCommand(Supplier<CommandConfig.Restart> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandEvent event, CommandConfig.Restart function) {
        if(ServerActions.isRunning()) {
            event.getHook().sendMessage(function.messageStopping.get()).queue();
            Logger.info("Stopping server.");

            try {
                ServerActions.stopServer();
            } catch (ServerOperationException e) {
                Logger.error(e, "Failed to stop server for restart");
                event.getHook().sendMessage(function.messageStopFailed.get()).queue();
                return;
            }

            event.getHook().sendMessage(function.messageStopped.get()).queue();

            Logger.info("Stopped server.");
            try {
                Thread.sleep(Config.get().server.restartDelay * 1000L);
            } catch (InterruptedException e) {
                Logger.error(e, "Failed to wait for restart delay");
            }

        } else {
            event.getHook().sendMessage(function.messageRestarting.get()).queue();
        }
        try {
            ServerActions.startServer();
            event.getHook().sendMessage(function.messageRestarted.get()).queue();
        } catch (ServerOperationException e) {
            event.getHook().sendMessage(function.messageRestartFailed.get()).queue();
            Logger.error(e, "Failed to restart server");
        }

        Logger.info("Handled. Restarted.");
    }
}
