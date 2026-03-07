package net.treset.minecraft_server_discord_bot.config.notification;

import java.util.List;
import java.util.stream.Stream;

public class NotificationContextTemplate {
    public final List<String> formatEntries;

    public NotificationContextTemplate(List<String> formatEntries) {
        this.formatEntries = formatEntries;
    }

    public List<String> availableKeys() {
        return formatEntries;
    }
    public boolean hasKey(String key) {
        return availableKeys().contains(key);
    }

    public static class DateTime extends NotificationContextTemplate {
        public static List<String> keys = List.of("date_iso", "date_dmy", "date_mdy", "time_12", "time_24");

        public DateTime(List<String> keys) {
            super(Stream.concat(DateTime.keys.stream(), keys.stream()).toList());
        }

        public DateTime() {
            super(DateTime.keys);
        }
    }

    public static class Player extends DateTime {
        public Player() {
            super(List.of("name", "uuid"));
        }
    }

    public static class Advancement extends DateTime {
        public Advancement() {
            super(List.of("player.name", "player.uuid", "message", "identifier", "title", "description", "toast"));
        }
    }

    public static class Death extends DateTime {
        public Death() {
            super(List.of("player.name", "player.uuid", "message"));
        }
    }

    public static class UploadService extends DateTime {
        public UploadService() {
            super(List.of("service"));
        }
    }

    public static class Countdown extends DateTime {
        public Countdown() {
            super(List.of("timeRemaining"));
        }
    }
}
