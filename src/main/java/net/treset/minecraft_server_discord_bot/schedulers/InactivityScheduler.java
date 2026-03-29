package net.treset.minecraft_server_discord_bot.schedulers;

import net.treset.minecraft_server_discord_bot.config.Config;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class InactivityScheduler {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private static ScheduledFuture<?> task = null;

    public static void scheduleNext(LocalDateTime lastActivityTime) {
        if(task != null) task.cancel(true);
        task = null;

        if(Config.get().inactivity == null) return;

        LocalDateTime nextTimeEventTimestamp = Config.get().inactivity.getNextEventTime(lastActivityTime);
        long until = Duration.between(LocalDateTime.now(), nextTimeEventTimestamp).getSeconds();

        task = scheduler.schedule(() -> {
            Config.get().events.inactive.send(Duration.between(lastActivityTime, LocalDateTime.now()));
            scheduleNext(lastActivityTime);
        }, until, TimeUnit.SECONDS);
    }

    static {
        EventScheduler.onEvent(InactivityScheduler::scheduleNext);
    }
}
