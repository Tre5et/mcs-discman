package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.util.Objects;
import java.util.function.Supplier;

public class SayCommand extends Command<CommandConfig.Say> {
    public SayCommand(Supplier<CommandConfig.Say> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandInteraction interaction, CommandConfig.Say function) {
        String message = Objects.requireNonNull(interaction.getOption("message")).getAsString();
        interaction.getHook().sendMessage(message).queue();

        Logger.info("Handled. Said \"%s\".", message);
    }

    @Override
    public CommandData data() {
        return Commands.slash("say", "Make the bot say something! [Moderator only]")
                .addOption(OptionType.STRING, "message", "The message the bot will say.", true);
    }
}
