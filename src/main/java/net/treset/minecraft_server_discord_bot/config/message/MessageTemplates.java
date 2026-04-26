package net.treset.minecraft_server_discord_bot.config.message;

import dev.treset.mcdl.servermanagement.vanilla.types.RpcPlayer;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.schedulers.ReminderScheduler;
import net.treset.minecraft_server_discord_bot.server.data.RpcAdvancement;
import net.treset.minecraft_server_discord_bot.server.data.RpcDeath;
import net.treset.minecraft_server_discord_bot.upload.UploadService;

import java.time.*;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MessageTemplates {
    public static final class DateTimeTemplate extends MessageTemplate<Object> {
        public DateTimeTemplate(Map<String, Function<MessageContext, String>> extractors) {
            super(extractors.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> (t, c) -> e.getValue().apply((c))))
            );
        }

        public String value(String key, MessageContext context) {
            return value(null, context, key);
        }

        public <T> MessageTemplate<T> extend(Map<String, BiFunction<T, MessageContext, String>> own) {
            return extend(own, ignored -> null);
        }

        public <T> MessageTemplate<T> extendSimple(Map<String, Function<T, String>> own) {
            return extendSimple(own, ignored -> null);
        }
    }

    public static final DateTimeTemplate DATE_TIME = new DateTimeTemplate(Map.ofEntries(
            Map.entry("timestamp_full_weekday", context -> switch (context) {
                case DISCORD -> discordTimeStamp("F");
                case IN_GAME -> Config.get().strings.timestampFullWeekday.get(LocalDateTime.now());
            }),
            Map.entry("timestamp_full", context -> switch (context) {
                case DISCORD -> discordTimeStamp("f");
                case IN_GAME -> Config.get().strings.timestampFull.get(LocalDateTime.now());
            }),
            Map.entry("timestamp_date", context -> switch (context) {
                case DISCORD -> discordTimeStamp("D");
                case IN_GAME -> Config.get().strings.timestampDate.get(LocalDateTime.now());
            }),
            Map.entry("timestamp_date_short", context -> switch (context) {
                case DISCORD -> discordTimeStamp("d");
                case IN_GAME -> Config.get().strings.timestampDateShort.get(LocalDateTime.now());
            }),
            Map.entry("timestamp_time_seconds", context -> switch (context) {
                case DISCORD -> discordTimeStamp("T");
                case IN_GAME -> Config.get().strings.timestampTimeSeconds.get(LocalDateTime.now());
            }),
            Map.entry("timestamp_time", context -> switch (context) {
                case DISCORD -> discordTimeStamp("t");
                case IN_GAME -> Config.get().strings.timestampTime.get(LocalDateTime.now());
            }),
            Map.entry("timestamp_short_seconds", context -> switch (context) {
                case DISCORD -> discordTimeStamp("S");
                case IN_GAME -> Config.get().strings.timestampShortSeconds.get(LocalDateTime.now());
            }),
            Map.entry("timestamp_short", context -> switch (context) {
                case DISCORD -> discordTimeStamp("s");
                case IN_GAME -> Config.get().strings.timestampShort.get(LocalDateTime.now());
            }),
            Map.entry("timestring_full_weekday", context -> Config.get().strings.timestampFullWeekday.get(LocalDateTime.now())),
            Map.entry("timestring_full", context -> Config.get().strings.timestampFull.get(LocalDateTime.now())),
            Map.entry("timestring_date", context -> Config.get().strings.timestampDate.get(LocalDateTime.now())),
            Map.entry("timestring_date_short", context -> Config.get().strings.timestampDateShort.get(LocalDateTime.now())),
            Map.entry("timestring_time_seconds", context -> Config.get().strings.timestampTimeSeconds.get(LocalDateTime.now())),
            Map.entry("timestring_time", context -> Config.get().strings.timestampTime.get(LocalDateTime.now())),
            Map.entry("timestring_short_seconds", context -> Config.get().strings.timestampShortSeconds.get(LocalDateTime.now())),
            Map.entry("timestring_short", context -> Config.get().strings.timestampShort.get(LocalDateTime.now()))
    ));

    public static final MessageTemplate<Number> NUMBER = new MessageTemplate<>(Map.of(
            "number", (number, context) -> number.toString()
    ));

    public static final MessageTemplate<String> STRING = new MessageTemplate<>(Map.of(
            "string", (string, context) -> string
    ));

    public static final MessageTemplate<LocalDateTime> INSTANT = new MessageTemplate<>(Map.ofEntries(
            Map.entry("day", (dateTime, context) -> String.valueOf(dateTime.getDayOfMonth())),
            Map.entry("month", (dateTime, context) -> String.valueOf(dateTime.getMonthValue())),
            Map.entry("month_name", (dateTime, context) -> month(dateTime)),
            Map.entry("year", (dateTime, context) -> String.valueOf(dateTime.getYear())),
            Map.entry("weekday", (dateTime, context) -> String.valueOf(dateTime.getDayOfWeek())),
            Map.entry("weekday_name", (dateTime, context) -> weekday(dateTime)),
            Map.entry("hour_24", (dateTime, context) -> String.valueOf(dateTime.getHour())),
            Map.entry("hour_12", (dateTime, context) -> String.valueOf(((dateTime.getHour() + 11) % 12) + 1)),
            Map.entry("am_pm", (dateTime, context) -> (dateTime.getHour() + 11) / 12 < 2 ? Config.get().strings.am : Config.get().strings.pm),
            Map.entry("minute", (dateTime, context) -> String.valueOf(dateTime.getMinute())),
            Map.entry("second", (dateTime, context) -> String.valueOf(dateTime.getSecond()))
    ));

    public static final MessageTemplate<RpcPlayer> PLAYER = DATE_TIME.extendSimple(
            Map.of(
                    "name", RpcPlayer::name,
                    "uuid", player -> player.id().toString()
            )
    );

    public static final MessageTemplate<RpcAdvancement> ADVANCEMENT = DATE_TIME.extendSimple(
            Map.of(
                    "player.name", advancement -> advancement.player().name(),
                    "player.uuid", advancement -> advancement.player().id().toString(),
                    "message", advancement -> advancement.message().literal(),
                    "identifier", RpcAdvancement::identifier,
                    "title", advancement -> advancement.title().literal(),
                    "description", advancement -> advancement.description().literal(),
                    "toast", advancement -> advancement.toast().literal()
            )
    );

    public static final MessageTemplate<RpcDeath> DEATH = DATE_TIME.extendSimple(
            Map.of(
                    "player.name", death -> death.player().name(),
                    "player.uuid", death -> death.player().id().toString(),
                    "message", death -> death.message().literal()
            )
    );

    public static final MessageTemplate<UploadService> UPLOAD_SERVICE = DATE_TIME.extendSimple(
            Map.of("service", UploadService::name)
    );

    public static final MessageTemplate<Duration> DURATION = DATE_TIME.extendSimple(
            Map.of("duration", MessageTemplates::durationString)
    );

    public record DetailsContext(String version) {}
    public static final MessageTemplate<DetailsContext> DETAILS = DATE_TIME.extendSimple(
            Map.of("version", DetailsContext::version)
    );

    public record JoinContext(String url) {}
    public static final MessageTemplate<JoinContext> JOIN = DATE_TIME.extendSimple(
            Map.of("url", JoinContext::url)
    );

    public record BackupsContext(String location, String admin) {}
    public static final MessageTemplate<BackupsContext> BACKUPS = DATE_TIME.extendSimple(
            Map.of(
                    "location", BackupsContext::location,
                    "admin", BackupsContext::admin
            )
    );

    public record MembersContext(String list, String admin, int count) {}
    public static final MessageTemplate<MembersContext> MEMBERS = DATE_TIME.extendSimple(
            Map.of(
                    "list", MembersContext::list,
                    "admin", MembersContext::admin,
                    "count", context -> String.valueOf(context.count())
            )
    );

    public record OnlineContext(String list, int count) {}
    public static final MessageTemplate<OnlineContext> ONLINE = DATE_TIME.extendSimple(
            Map.of(
                    "list", OnlineContext::list,
                    "count", context -> String.valueOf(context.count())
            )
    );

    public record RunCommandContext(String command, String response) {}
    public static final MessageTemplate<RunCommandContext> RUN_COMMAND = DATE_TIME.extendSimple(
            Map.of(
                    "command", RunCommandContext::command,
                    "response", RunCommandContext::response
            )
    );

    public static final MessageTemplate<ReminderScheduler.Reminder> REMINDER = DATE_TIME.extend(Map.ofEntries(
            Map.entry("message", (reminder, context) -> reminder.message()),
            Map.entry("reminder_timestamp_full_weekday", (reminder, context) -> switch (context) {
                case DISCORD -> discordTimeStamp(reminder.time(), "F");
                case IN_GAME -> Config.get().strings.timestampFullWeekday.get(LocalDateTime.ofInstant(reminder.time(), ZoneId.systemDefault()));
            }),
            Map.entry("reminder_timestamp_full", (reminder, context) -> switch (context) {
                case DISCORD -> discordTimeStamp(reminder.time(), "f");
                case IN_GAME -> Config.get().strings.timestampFull.get(LocalDateTime.ofInstant(reminder.time(), ZoneId.systemDefault()));
            }),
            Map.entry("reminder_timestamp_date", (reminder, context) -> switch (context) {
                case DISCORD -> discordTimeStamp(reminder.time(), "D");
                case IN_GAME -> Config.get().strings.timestampDate.get(LocalDateTime.ofInstant(reminder.time(), ZoneId.systemDefault()));
            }),
            Map.entry("reminder_timestamp_date_short", (reminder, context) -> switch (context) {
                case DISCORD -> discordTimeStamp(reminder.time(), "d");
                case IN_GAME -> Config.get().strings.timestampDateShort.get(LocalDateTime.ofInstant(reminder.time(), ZoneId.systemDefault()));
            }),
            Map.entry("reminder_timestamp_time_seconds", (reminder, context) -> switch (context) {
                case DISCORD -> discordTimeStamp(reminder.time(), "T");
                case IN_GAME -> Config.get().strings.timestampTimeSeconds.get(LocalDateTime.ofInstant(reminder.time(), ZoneId.systemDefault()));
            }),
            Map.entry("reminder_timestamp_time", (reminder, context) -> switch (context) {
                case DISCORD -> discordTimeStamp(reminder.time(), "t");
                case IN_GAME -> Config.get().strings.timestampTime.get(LocalDateTime.ofInstant(reminder.time(), ZoneId.systemDefault()));
            }),
            Map.entry("reminder_timestamp_short_seconds", (reminder, context) -> switch (context) {
                case DISCORD -> discordTimeStamp(reminder.time(), "S");
                case IN_GAME -> Config.get().strings.timestampShortSeconds.get(LocalDateTime.ofInstant(reminder.time(), ZoneId.systemDefault()));
            }),
            Map.entry("reminder_timestamp_short", (reminder, context) -> switch (context) {
                case DISCORD -> discordTimeStamp(reminder.time(), "s");
                case IN_GAME -> Config.get().strings.timestampShort.get(LocalDateTime.ofInstant(reminder.time(), ZoneId.systemDefault()));
            }),
            Map.entry("reminder_timestamp_remaining", (reminder, context) -> switch (context) {
                case DISCORD -> discordTimeStamp(reminder.time(), "R");
                case IN_GAME -> Config.get().strings.timeIn.get(durationString(Duration.between(Instant.now(), reminder.time())));
            }),
            Map.entry("reminder_timestring_full_weekday", (reminder, context) -> Config.get().strings.timestampFullWeekday.get(LocalDateTime.ofInstant(reminder.time(), ZoneId.systemDefault()))),
            Map.entry("reminder_timestring_full", (reminder, context) -> Config.get().strings.timestampFull.get(LocalDateTime.ofInstant(reminder.time(), ZoneId.systemDefault()))),
            Map.entry("reminder_timestring_date", (reminder, context) -> Config.get().strings.timestampDate.get(LocalDateTime.ofInstant(reminder.time(), ZoneId.systemDefault()))),
            Map.entry("reminder_timestring_date_short", (reminder, context) -> Config.get().strings.timestampDateShort.get(LocalDateTime.ofInstant(reminder.time(), ZoneId.systemDefault()))),
            Map.entry("reminder_timestring_time_seconds", (reminder, context) -> Config.get().strings.timestampTimeSeconds.get(LocalDateTime.ofInstant(reminder.time(), ZoneId.systemDefault()))),
            Map.entry("reminder_timestring_time", (reminder, context) -> Config.get().strings.timestampTime.get(LocalDateTime.ofInstant(reminder.time(), ZoneId.systemDefault()))),
            Map.entry("reminder_timestring_short_seconds", (reminder, context) -> Config.get().strings.timestampShortSeconds.get(LocalDateTime.ofInstant(reminder.time(), ZoneId.systemDefault()))),
            Map.entry("reminder_timestring_short", (reminder, context) -> Config.get().strings.timestampShort.get(LocalDateTime.ofInstant(reminder.time(), ZoneId.systemDefault()))),
            Map.entry("reminder_timestring_remaining", (reminder, context) -> Config.get().strings.timeIn.get(durationString(Duration.between(Instant.now(), reminder.time()))))
    ));

    private static String durationString(Duration duration) {
        final long secondsPerMinute = 60;
        final long secondsPerHour = 60 * secondsPerMinute;
        final long secondsPerDay = 24 * secondsPerHour;
        final long secondsPerMonth = 30 * secondsPerDay;
        final long secondsPerYear = 365 * secondsPerDay;

        long remainingSeconds = Math.max(0L, duration.getSeconds());

        long years = remainingSeconds / secondsPerYear;
        remainingSeconds %= secondsPerYear;

        long months = remainingSeconds / secondsPerMonth;
        remainingSeconds %= secondsPerMonth;

        long days = remainingSeconds / secondsPerDay;
        remainingSeconds %= secondsPerDay;

        long hours = remainingSeconds / secondsPerHour;
        remainingSeconds %= secondsPerHour;

        long minutes = remainingSeconds / secondsPerMinute;
        long seconds = remainingSeconds % secondsPerMinute;

        long[] values = new long[]{years, months, days, hours, minutes, seconds};
        int highestIndex = -1;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > 0) {
                highestIndex = i;
                break;
            }
        }

        if (highestIndex == -1) {
            return Config.get().strings.seconds.get(0);
        }

        String first = switch (highestIndex) {
            case 0 -> values[0] == 1 ? Config.get().strings.oneYear : Config.get().strings.years.get(values[0]);
            case 1 -> values[1] == 1 ? Config.get().strings.oneMonth : Config.get().strings.months.get(values[1]);
            case 2 -> values[2] == 1 ? Config.get().strings.oneDay : Config.get().strings.days.get(values[2]);
            case 3 -> values[3] == 1 ? Config.get().strings.oneHour : Config.get().strings.hours.get(values[3]);
            case 4 -> values[4] == 1 ? Config.get().strings.oneMinute : Config.get().strings.minutes.get(values[4]);
            default -> values[5] == 1 ? Config.get().strings.oneSecond : Config.get().strings.seconds.get(values[5]);
        };

        int secondIndex = highestIndex + 1;
        if (secondIndex >= values.length || values[secondIndex] == 0) {
            return first;
        }

        String second = switch (secondIndex) {
            case 1 -> values[1] == 1 ? Config.get().strings.oneMonth : Config.get().strings.months.get(values[1]);
            case 2 -> values[2] == 1 ? Config.get().strings.oneDay : Config.get().strings.days.get(values[2]);
            case 3 -> values[3] == 1 ? Config.get().strings.oneHour : Config.get().strings.hours.get(values[3]);
            case 4 -> values[4] == 1 ? Config.get().strings.oneMinute : Config.get().strings.minutes.get(values[4]);
            default -> values[5] == 1 ? Config.get().strings.oneSecond : Config.get().strings.seconds.get(values[5]);
        };

        return first + " " + second;
    }

    private static String weekday(LocalDateTime dateTime) {
        return switch (dateTime.getDayOfWeek()) {
            case MONDAY -> Config.get().strings.monday;
            case TUESDAY -> Config.get().strings.tuesday;
            case WEDNESDAY -> Config.get().strings.wednesday;
            case THURSDAY -> Config.get().strings.thursday;
            case FRIDAY -> Config.get().strings.friday;
            case SATURDAY -> Config.get().strings.saturday;
            case SUNDAY -> Config.get().strings.sunday;
        };
    }

    private static String month(LocalDateTime dateTime) {
        return switch (dateTime.getMonth()) {
            case JANUARY -> Config.get().strings.january;
            case FEBRUARY -> Config.get().strings.february;
            case MARCH -> Config.get().strings.march;
            case APRIL -> Config.get().strings.april;
            case MAY -> Config.get().strings.may;
            case JUNE -> Config.get().strings.june;
            case JULY -> Config.get().strings.july;
            case AUGUST -> Config.get().strings.august;
            case SEPTEMBER -> Config.get().strings.september;
            case OCTOBER -> Config.get().strings.october;
            case NOVEMBER -> Config.get().strings.november;
            case DECEMBER -> Config.get().strings.december;
        };
    }
    
    private static String discordTimeStamp(Instant time, String format) {
        return "<t:" + time.getEpochSecond() + ":" + format + ">";
    }

    private static String discordTimeStamp(String format) {
        return discordTimeStamp(Instant.now(), format);
    }
}
