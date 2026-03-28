package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.function.FunctionConfig;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;
import java.util.function.Supplier;

public class ReloadConfigCommand extends Command<FunctionConfig.Reload> {
    public ReloadConfigCommand(Supplier<FunctionConfig.Reload> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandEvent event, FunctionConfig.Reload function) {
        try {
            Config.load();
            event.getHook().sendMessage(function.messageReloaded.get()).queue();
        } catch (ConfigException e) {
            event.getHook().sendMessage(function.messageFailed.get()).queue();
        }
    }
}
