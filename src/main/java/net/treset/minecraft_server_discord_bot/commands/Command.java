package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class Command<C extends CommandConfig> {
    private final Supplier<C> configSupplier;

    protected Command(Supplier<C> configSupplier) {
        this.configSupplier = configSupplier;
    }

    public final void handle(SlashCommandEvent event) {
        C config = configSupplier.get();
        if (!config.isCorrectChannel(event.getChannel())) {
            event.getHook().deleteOriginal().queue();
            Logger.info("Handled. Invalid Channel.");
            return;
        }
        if (!config.enabled) {
            event.getHook().sendMessage(config.disabledMessage.get()).queue();
            Logger.info("Handled. Disabled.");
            return;
        }
        if (!config.isAllowed(Objects.requireNonNull(event.getMember()))) {
            event.getHook().sendMessage(config.deniedMessage.get()).queue();
            Logger.info("Handled. Permission required.");
            return;
        }

        process(event, config);
    }

    protected abstract void process(SlashCommandEvent event, C config);
}


