package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.config.message.MessageContext;
import net.treset.minecraft_server_discord_bot.exception.ServerOperationException;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ServerActions;

import java.util.function.Supplier;

public class RestartServerCommand extends Command<CommandConfig.Restart> {
    public RestartServerCommand(Supplier<CommandConfig.Restart> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandInteraction interaction, CommandConfig.Restart function) {
        if(ServerActions.isRunning()) {
            interaction.getHook().sendMessage(function.messageStopping.get(MessageContext.DISCORD)).queue();
            Logger.info("Stopping server.");

            try {
                ServerActions.stopServer();
            } catch (ServerOperationException e) {
                Logger.error(e, "Failed to stop server for restart");
                interaction.getHook().sendMessage(function.messageStopFailed.get(MessageContext.DISCORD)).queue();
                return;
            }

            interaction.getHook().sendMessage(function.messageStopped.get(MessageContext.DISCORD)).queue();

            Logger.info("Stopped server.");
            try {
                Thread.sleep(Config.get().server.restartDelay * 1000L);
            } catch (InterruptedException e) {
                Logger.error(e, "Failed to wait for restart delay");
            }

        } else {
            interaction.getHook().sendMessage(function.messageRestarting.get(MessageContext.DISCORD)).queue();
        }
        try {
            ServerActions.startServer();
            interaction.getHook().sendMessage(function.messageRestarted.get(MessageContext.DISCORD)).queue();
        } catch (ServerOperationException e) {
            interaction.getHook().sendMessage(function.messageRestartFailed.get(MessageContext.DISCORD)).queue();
            Logger.error(e, "Failed to restart server");
        }

        Logger.info("Handled. Restarted.");
    }

    @Override
    public CommandData data() {
        return Commands.slash("restartserver", "Restart the server! [Moderator only]");
    }
}
