package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ReminderCommand extends Command<CommandConfig.Reminder> {
    protected ReminderCommand(Supplier<CommandConfig.Reminder> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandInteraction interaction, CommandConfig.Reminder config) {
        IMentionable mentionable = Objects.requireNonNull(interaction.getOption("mention")).getAsMentionable();
        interaction.getHook().sendMessage(config.getMentionStrings(List.of(mentionable), interaction.getUser()).discord() + "AAAA").queue();
    }

    @Override
    public CommandData data() {
        return Commands.slash("reminder", "test")
                .addOption(OptionType.MENTIONABLE, "mention", "test", true);
    }
}
