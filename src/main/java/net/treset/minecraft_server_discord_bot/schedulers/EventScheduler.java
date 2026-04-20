package net.treset.minecraft_server_discord_bot.schedulers;

import net.treset.minecraft_server_discord_bot.logging.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventScheduler {
    private static LocalDateTime lastEventTimestamp = LocalDateTime.now();
    private static final List<Consumer<LocalDateTime>> eventHandlers = new ArrayList<>();

    public static void eventOccurred() {
        lastEventTimestamp = LocalDateTime.now();
        for(Consumer<LocalDateTime> handler : eventHandlers) {
            try {
                handler.accept(lastEventTimestamp);
            } catch (RuntimeException e) {
                Logger.warn(e, "An EventScheduler handler failed and was ignored");
            }
        }
    }

    public static LocalDateTime getLastEventTimestamp() {
        return lastEventTimestamp;
    }

    public static void onEvent(Consumer<LocalDateTime> handler) {
        eventHandlers.add(handler);
    }
}
