package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.config.message.MessageContext;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.schedulers.ReminderScheduler;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class ReminderCommand extends Command<CommandConfig.Reminder> {
    protected ReminderCommand(Supplier<CommandConfig.Reminder> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandInteraction interaction, CommandConfig.Reminder config) {
        String message = Objects.requireNonNull(interaction.getOption("message")).getAsString();
        Long time = Objects.requireNonNull(interaction.getOption("time")).getAsLong();
        OptionMapping timeUnitOption = interaction.getOption("unit");
        String timeUnit = timeUnitOption != null ? timeUnitOption.getAsString() : "hours";
        List<IMentionable> mentions = new ArrayList<>();
        for(int i = 1; i < 10; i++) {
            OptionMapping mentionOption = interaction.getOption("mention"+i);
            if(mentionOption != null) mentions.add(mentionOption.getAsMentionable());
        }

        Instant instant = constructTime(time, timeUnit);

        CommandConfig.Reminder.MentionableString strings = config.getMentionStrings(mentions, interaction.getUser());
        ReminderScheduler.Reminder reminder = new ReminderScheduler.Reminder(
                UUID.randomUUID(),
                instant,
                message,
                strings.discord(),
                strings.inGame(),
                interaction.getChannel().getId(),
                true,
                true
        );
        try {
            ReminderScheduler.schedule(reminder, config);
        } catch (IOException e) {
            Logger.error(e, "Failed to schedule reminder");
            interaction.getHook().sendMessage(config.messageFailed.get(reminder, MessageContext.DISCORD)).queue();
            return;
        }
        interaction.getHook().sendMessage(config.message.get(reminder, MessageContext.DISCORD)).queue();
    }

    @Override
    public CommandData data() {
        return Commands.slash("reminder", "Set a reminder!").addOptions(
                new OptionData(OptionType.STRING, "message", "The reminder message to send.", true),
                new OptionData(OptionType.INTEGER, "time", "The time in which to remind. (Default unit: hours)", true),
                new OptionData(OptionType.STRING, "unit", "The time unit of the time to remind. (Default: hours)")
                        .addChoice("seconds", "seconds")
                        .addChoice("minutes", "minutes")
                        .addChoice("hours", "hours")
                        .addChoice("days", "days")
                        .addChoice("weeks", "weeks")
                        .addChoice("months", "months")
                        .addChoice("years", "years"),
                new OptionData(OptionType.MENTIONABLE, "mention1", "The users to mention with the reminder."),
                new OptionData(OptionType.MENTIONABLE, "mention2", "The users to mention with the reminder."),
                new OptionData(OptionType.MENTIONABLE, "mention3", "The users to mention with the reminder."),
                new OptionData(OptionType.MENTIONABLE, "mention4", "The users to mention with the reminder."),
                new OptionData(OptionType.MENTIONABLE, "mention5", "The users to mention with the reminder."),
                new OptionData(OptionType.MENTIONABLE, "mention6", "The users to mention with the reminder."),
                new OptionData(OptionType.MENTIONABLE, "mention7", "The users to mention with the reminder."),
                new OptionData(OptionType.MENTIONABLE, "mention8", "The users to mention with the reminder."),
                new OptionData(OptionType.MENTIONABLE, "mention9", "The users to mention with the reminder.")
        );
    }

    private Instant constructTime(Long time, String unit) {
        TemporalUnit temporalUnit = switch (unit) {
            case "seconds" -> ChronoUnit.SECONDS;
            case "minutes" -> ChronoUnit.MINUTES;
            case "hours" -> ChronoUnit.HOURS;
            case "days" -> ChronoUnit.DAYS;
            case "months" -> ChronoUnit.MONTHS;
            case "years" -> ChronoUnit.YEARS;
            default -> ChronoUnit.HOURS;
        };
        return Instant.now().plus(time, temporalUnit);
    }
}
