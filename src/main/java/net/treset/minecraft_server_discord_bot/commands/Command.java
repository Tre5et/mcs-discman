package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.config.message.MessageContext;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class Command<C extends CommandConfig> {
    private final Supplier<C> configSupplier;

    protected Command(Supplier<C> configSupplier) {
        this.configSupplier = configSupplier;
    }

    public final void handle(SlashCommandInteraction interaction) {
        C config = configSupplier.get();
        if (!config.isCorrectChannel(interaction.getChannel())) {
            interaction.getHook().deleteOriginal().queue();
            Logger.info("Handled. Invalid Channel.");
            return;
        }
        if (!config.enabled) {
            interaction.getHook().sendMessage(config.messageDisabled.get(MessageContext.DISCORD)).queue();
            Logger.info("Handled. Disabled.");
            return;
        }
        if (!config.isAllowed(Objects.requireNonNull(interaction.getMember()))) {
            interaction.getHook().sendMessage(config.messageDenied.get(MessageContext.DISCORD)).queue();
            Logger.info("Handled. Permission required.");
            return;
        }

        process(interaction, config);
    }

    public boolean handleButtonInteraction(ButtonInteractionEvent event) {
        C config = configSupplier.get();
        return processButtonInteraction(event, config);
    }

    protected boolean processButtonInteraction(ButtonInteractionEvent event, C config) {
        return false;
    }

    protected abstract void process(SlashCommandInteraction interaction, C config);

    public abstract CommandData data();
}


