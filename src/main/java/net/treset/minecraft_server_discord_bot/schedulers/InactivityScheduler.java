package net.treset.minecraft_server_discord_bot.schedulers;

import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;

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
            DiscordBot.sendText("The server has been inactive for " + Duration.between(lastActivityTime, LocalDateTime.now()).toDays() + " day. Consider stopping it.", MessageOrigin.SCHEDULE);
            scheduleNext(lastActivityTime);
        }, until, TimeUnit.SECONDS);
    }

    static {
        EventScheduler.onEvent(InactivityScheduler::scheduleNext);
    }
}
