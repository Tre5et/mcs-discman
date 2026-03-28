package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.function.FunctionConfig;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ServerActions;

import java.util.function.Supplier;

public class ActiveCommand extends Command<FunctionConfig.Active> {
    public ActiveCommand(Supplier<FunctionConfig.Active> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandEvent event, FunctionConfig.Active function) {
        String output;

        boolean running = ServerActions.isRunning();
        if(running) output = function.messageActive.get();
        else output = function.messageInactive.get();

        event.getHook().sendMessage(output).queue();

        Logger.info("Handled: %s.", running ? "running": "not running");
    }
}
