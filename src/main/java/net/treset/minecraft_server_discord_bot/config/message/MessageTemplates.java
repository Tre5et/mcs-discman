package net.treset.minecraft_server_discord_bot.config.message;

import dev.treset.mcdl.servermanagement.vanilla.types.RpcPlayer;
import net.treset.minecraft_server_discord_bot.server.data.RpcAdvancement;
import net.treset.minecraft_server_discord_bot.server.data.RpcDeath;
import net.treset.minecraft_server_discord_bot.upload.UploadService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Function;

public class MessageTemplates {
    public static final class DateTimeTemplate extends MessageTemplate<Object> {
        public DateTimeTemplate(Map<String, java.util.function.Function<Object, String>> extractors) {
            super(extractors);
        }

        public String value(String key) {
            return value(null, key);
        }

        public <T> MessageTemplate<T> extend(Map<String, Function<T, String>> own) {
            return extend(own, ignored -> null);
        }
    }

    public static final DateTimeTemplate DATE_TIME = new DateTimeTemplate(Map.of(
            "date_iso", ignored -> DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now()),
            "date_dmy", ignored -> DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDateTime.now()),
            "date_mdy", ignored -> DateTimeFormatter.ofPattern("M/d/yyyy").format(LocalDateTime.now()),
            "time_12", ignored -> DateTimeFormatter.ofPattern("hh:mm a").format(LocalDateTime.now()),
            "time_24", ignored -> DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.now())
    ));

    public static final MessageTemplate<RpcPlayer> PLAYER = DATE_TIME.extend(
            Map.of(
                    "name", RpcPlayer::name,
                    "uuid", player -> player.id().toString()
            )
    );

    public static final MessageTemplate<RpcAdvancement> ADVANCEMENT = DATE_TIME.extend(
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

    public static final MessageTemplate<RpcDeath> DEATH = DATE_TIME.extend(
            Map.of(
                    "player.name", death -> death.player().name(),
                    "player.uuid", death -> death.player().id().toString(),
                    "message", death -> death.message().literal()
            )
    );

    public static final MessageTemplate<UploadService> UPLOAD_SERVICE = DATE_TIME.extend(
            Map.of("service", UploadService::name)
    );

    public static final MessageTemplate<Duration> DURATION = DATE_TIME.extend(
            Map.of("duration", MessageTemplates::durationString)
    );

    public record DetailsContext(String version) {}
    public static final MessageTemplate<DetailsContext> DETAILS = DATE_TIME.extend(
            Map.of("version", DetailsContext::version)
    );

    public record JoinContext(String url) {}
    public static final MessageTemplate<JoinContext> JOIN = DATE_TIME.extend(
            Map.of("url", JoinContext::url)
    );

    public record BackupsContext(String location, String admin) {}
    public static final MessageTemplate<BackupsContext> BACKUPS = DATE_TIME.extend(
            Map.of(
                    "location", BackupsContext::location,
                    "admin", BackupsContext::admin
            )
    );

    public record MembersContext(String list, String admin, int count) {}
    public static final MessageTemplate<MembersContext> MEMBERS = DATE_TIME.extend(
            Map.of(
                    "list", MembersContext::list,
                    "admin", MembersContext::admin,
                    "count", context -> String.valueOf(context.count())
            )
    );

    public record OnlineContext(String list, int count) {}
    public static final MessageTemplate<OnlineContext> ONLINE = DATE_TIME.extend(
            Map.of(
                    "list", OnlineContext::list,
                    "count", context -> String.valueOf(context.count())
            )
    );

    public record RunCommandContext(String command, String response) {}
    public static final MessageTemplate<RunCommandContext> RUN_COMMAND = DATE_TIME.extend(
            Map.of(
                    "command", RunCommandContext::command,
                    "response", RunCommandContext::response
            )
    );

    private static String durationString(Duration duration) {
        int seconds = duration.toSecondsPart();
        int minutes = duration.toMinutesPart();
        int hours = duration.toHoursPart();

        if (hours == 0 && minutes == 0) {
            return String.format("%d seconds", seconds);
        }

        StringBuilder output = new StringBuilder();
        if (hours != 0) {
            output.append(String.format("%d hours", hours));
        }
        if (minutes != 0 || seconds != 0) {
            if (!output.isEmpty()) {
                output.append(" ");
            }
            output.append(String.format("%d minutes", minutes));
        }
        if (seconds != 0) {
            output.append(String.format(" %d seconds", seconds));
        }

        return output.toString().trim();
    }
}
