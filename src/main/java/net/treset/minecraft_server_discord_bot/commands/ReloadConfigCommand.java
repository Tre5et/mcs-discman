package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;
import java.util.function.Supplier;

public class ReloadConfigCommand extends Command<CommandConfig.Reload> {
    public ReloadConfigCommand(Supplier<CommandConfig.Reload> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandEvent event, CommandConfig.Reload function) {
        try {
            Config.load();
            event.getHook().sendMessage(function.messageReloaded.get()).queue();
        } catch (ConfigException e) {
            event.getHook().sendMessage(function.messageFailed.get()).queue();
        }
    }

    @Override
    public CommandData data() {
        return new CommandData("reloadconfig", "Reloads the configuration from a file! [Moderator only]");
    }
}
