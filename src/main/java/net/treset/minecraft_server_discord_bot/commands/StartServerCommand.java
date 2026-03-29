package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.exception.ServerOperationException;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ServerActions;

import java.util.function.Supplier;

public class StartServerCommand extends Command<CommandConfig.Start> {
    public StartServerCommand(Supplier<CommandConfig.Start> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandEvent event, CommandConfig.Start function) {
        if(ServerActions.isRunning()) {
            event.getHook().sendMessage(function.messageAlreadyRunning.get()).queue();

            Logger.info("Handled. Already running.");
        } else {
            event.getHook().sendMessage(function.messageStarting.get()).queue();
            try {
                ServerActions.startServer();
                event.getHook().sendMessage(function.messageStarted.get()).queue();
            } catch (ServerOperationException e) {
                Logger.error(e, "Failed to start server");
                event.getHook().sendMessage(function.messageStartFailed.get()).queue();
            }
            Logger.info("Handled.");
        }
    }
}
