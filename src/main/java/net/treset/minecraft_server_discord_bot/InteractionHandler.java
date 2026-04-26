package net.treset.minecraft_server_discord_bot;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.treset.minecraft_server_discord_bot.config.Config;
import org.jetbrains.annotations.NotNull;

public class InteractionHandler extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(Config.get() == null || Config.get().commands == null) return;
        Config.get().commands.handleCommand(event);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(Config.get() == null || Config.get().commands == null) return;
        Config.get().commands.handleButtonInteraction(event);
    }
}