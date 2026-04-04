package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
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
    protected void process(SlashCommandInteraction interaction, CommandConfig.Stop function) {
        if(!ServerActions.isRunning()) {
            interaction.getHook().sendMessage(function.messageAlreadyStopped.get()).queue();
            Logger.info("Handled. Already stopped.");
        } else {
            interaction.getHook().sendMessage(function.messageStopping.get()).queue();
            Logger.info("Stopping server.");

            try {
                ServerActions.stopServer();
            } catch (ServerOperationException e) {
                Logger.error(e, "Failed to stop server");
                interaction.getHook().sendMessage(function.messageStopFailed.get()).queue();
            }
            interaction.getHook().sendMessage(function.messageStopped.get()).queue();
        }
    }

    @Override
    public CommandData data() {
        return Commands.slash("stopserver", "Start the server! [Moderator only]");
    }
}
