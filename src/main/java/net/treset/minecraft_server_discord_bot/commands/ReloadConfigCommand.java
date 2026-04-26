package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.config.message.MessageContext;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;
import java.util.function.Supplier;

public class ReloadConfigCommand extends Command<CommandConfig.Reload> {
    public ReloadConfigCommand(Supplier<CommandConfig.Reload> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandInteraction interaction, CommandConfig.Reload function) {
        try {
            Config.load();
            interaction.getHook().sendMessage(function.messageReloaded.get(MessageContext.DISCORD)).queue();
        } catch (ConfigException e) {
            interaction.getHook().sendMessage(function.messageFailed.get(MessageContext.DISCORD)).queue();
        }
    }

    @Override
    public CommandData data() {
        return Commands.slash("reloadconfig", "Reloads the configuration from a file! [Moderator only]");
    }
}
