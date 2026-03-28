package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.function.FunctionConfig;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.util.function.Supplier;

public class PingCommand extends Command<FunctionConfig.Ping> {
    public PingCommand(Supplier<FunctionConfig.Ping> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandEvent event, FunctionConfig.Ping function) {
        event.getHook().sendMessage(function.messagePong.get()).queue();

        Logger.info("Handled.");
    }
}
