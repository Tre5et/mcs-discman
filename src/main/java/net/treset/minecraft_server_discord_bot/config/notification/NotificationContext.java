package net.treset.minecraft_server_discord_bot.config.notification;

import dev.treset.mcdl.servermanagement.vanilla.types.RpcPlayer;
import net.treset.minecraft_server_discord_bot.server.data.RpcAdvancement;
import net.treset.minecraft_server_discord_bot.server.data.RpcDeath;
import net.treset.minecraft_server_discord_bot.upload.UploadService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NotificationContext<T extends NotificationContextTemplate> {
    private final Map<String, Supplier<String>> contextEntries;

    public NotificationContext(Map<String, Supplier<String>> contextEntries) {
        this.contextEntries = contextEntries;
    }

    public String value(String key) {
        return contextEntries.get(key).get();
    }

    private static Map<String, Supplier<String>> defaultMap = Map.of(
            "date_iso", () -> DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now()),
            "date_dmy", () -> DateTimeFormatter.ofPattern("d.M.yyyy").format(LocalDateTime.now()),
            "date_mdy", () -> DateTimeFormatter.ofPattern("M/d/yyyy").format(LocalDateTime.now()),
            "time_12", () -> DateTimeFormatter.ofPattern("hh:mm a").format(LocalDateTime.now()),
            "time_24", () -> DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.now())
    );

    public static NotificationContext<NotificationContextTemplate.DateTime> of() {
        return new NotificationContext<>(defaultMap);
    }

    public static <T extends NotificationContextTemplate.DateTime> NotificationContext<T> of(Map<String, Supplier<String>> map) {
        return new NotificationContext<>(Stream.concat(defaultMap.entrySet().stream(), map.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public static NotificationContext<NotificationContextTemplate.Player> player(RpcPlayer player) {
        return of(Map.of("name", player::name, "uuid", player.id()::toString));
    }

    public static NotificationContext<NotificationContextTemplate.Advancement> advancement(RpcAdvancement advancement) {
        return of(Map.of(
                "player.name", advancement.player()::name,
                "player.uuid", advancement.player().id()::toString,
                "message", advancement.message()::literal,
                "identifier", advancement::identifier,
                "title", advancement.title()::literal,
                "description", advancement.description()::literal,
                "toast", advancement.toast()::literal
        ));
    }

    public static NotificationContext<NotificationContextTemplate.Death> death(RpcDeath death) {
        return of(Map.of(
                "player.name", death.player()::name,
                "player.uuid", death.player().id()::toString,
                "message", death.message()::literal
        ));
    }

    public static NotificationContext<NotificationContextTemplate.UploadService> uploadService(UploadService service) {
        return of(Map.of("service", service::name));
    }

    public static NotificationContext<NotificationContextTemplate.Countdown> countdown(Duration duration) {
        return of(Map.of("timeRemaining", () -> durationString(duration)));
    }

    private static String durationString(Duration duration) {
        int seconds = duration.toSecondsPart();
        int minutes = duration.toMinutesPart();
        int hours = duration.toHoursPart();

        if(hours == 0 && minutes == 0) {
            return String.format("%d seconds", seconds);
        }
        List<String> output = new ArrayList<>();

        if(hours != 0) {
            output.add(String.format("%d hours", hours));
        }
        if(minutes != 0 || seconds != 0) {
            output.add(String.format("%d minutes", minutes));
        }
        if(seconds != 0) {
            output.add(String.format("%d seconds", seconds));
        }
        return String.join(" ", output);
    }
}
