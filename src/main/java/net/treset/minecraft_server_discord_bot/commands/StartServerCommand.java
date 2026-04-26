package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.config.message.MessageContext;
import net.treset.minecraft_server_discord_bot.exception.ServerOperationException;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ServerActions;

import java.util.function.Supplier;

public class StartServerCommand extends Command<CommandConfig.Start> {
    public StartServerCommand(Supplier<CommandConfig.Start> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandInteraction interaction, CommandConfig.Start function) {
        if(ServerActions.isRunning()) {
            interaction.getHook().sendMessage(function.messageAlreadyRunning.get(MessageContext.DISCORD)).queue();

            Logger.info("Handled. Already running.");
        } else {
            interaction.getHook().sendMessage(function.messageStarting.get(MessageContext.DISCORD)).queue();
            try {
                ServerActions.startServer();
                interaction.getHook().sendMessage(function.messageStarted.get(MessageContext.DISCORD)).queue();
            } catch (ServerOperationException e) {
                Logger.error(e, "Failed to start server");
                interaction.getHook().sendMessage(function.messageStartFailed.get(MessageContext.DISCORD)).queue();
            }
            Logger.info("Handled.");
        }
    }

    @Override
    public CommandData data() {
        return Commands.slash("startserver", "Start the server! [Moderator only]");
    }
}
