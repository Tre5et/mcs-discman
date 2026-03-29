package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ServerActions;

import java.util.function.Supplier;

public class ActiveCommand extends Command<CommandConfig.Active> {
    public ActiveCommand(Supplier<CommandConfig.Active> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandEvent event, CommandConfig.Active function) {
        String output;

        boolean running = ServerActions.isRunning();
        if(running) output = function.messageActive.get();
        else output = function.messageInactive.get();

        event.getHook().sendMessage(output).queue();

        Logger.info("Handled: %s.", running ? "running": "not running");
    }
}
