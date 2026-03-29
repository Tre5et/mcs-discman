package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.exception.ServerOperationException;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ServerActions;

import java.util.function.Supplier;

public class StopServerCommand extends Command<CommandConfig.Stop> {
    public StopServerCommand(Supplier<CommandConfig.Stop> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandEvent event, CommandConfig.Stop function) {
        if(!ServerActions.isRunning()) {
            event.getHook().sendMessage(function.messageAlreadyStopped.get()).queue();
            Logger.info("Handled. Already stopped.");
        } else {
            event.getHook().sendMessage(function.messageStopping.get()).queue();
            Logger.info("Stopping server.");

            try {
                ServerActions.stopServer();
            } catch (ServerOperationException e) {
                Logger.error(e, "Failed to stop server");
                event.getHook().sendMessage(function.messageStopFailed.get()).queue();
            }
            event.getHook().sendMessage(function.messageStopped.get()).queue();
        }
    }

    @Override
    public CommandData data() {
        return new CommandData("stopserver", "Start the server! [Moderator only]");
    }
}
