package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.config.message.MessageContext;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.util.function.Supplier;

public class PingCommand extends Command<CommandConfig.Ping> {
    public PingCommand(Supplier<CommandConfig.Ping> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandInteraction interaction, CommandConfig.Ping function) {
        interaction.getHook().sendMessage(function.messagePong.get(MessageContext.DISCORD)).queue();

        Logger.info("Handled.");
    }

    @Override
    public CommandData data() {
        return Commands.slash("ping", "See if the bot is online!");
    }
}
