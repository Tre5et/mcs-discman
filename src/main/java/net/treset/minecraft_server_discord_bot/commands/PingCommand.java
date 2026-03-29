package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.util.function.Supplier;

public class PingCommand extends Command<CommandConfig.Ping> {
    public PingCommand(Supplier<CommandConfig.Ping> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandEvent event, CommandConfig.Ping function) {
        event.getHook().sendMessage(function.messagePong.get()).queue();

        Logger.info("Handled.");
    }
}
