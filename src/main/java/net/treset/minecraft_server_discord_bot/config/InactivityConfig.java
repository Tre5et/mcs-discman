package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class InactivityConfig extends Validatable {
    public int interval;
    public List<Integer> schedule = List.of();

    public LocalDateTime getNextEventTime(LocalDateTime from) {
        if(interval == 0 && schedule.isEmpty()) return null;
        List<Duration> durations = schedule.stream().map(t -> Duration.of(t, ChronoUnit.DAYS)).sorted().toList();
        LocalDateTime now = LocalDateTime.now().plusMinutes(1);
        for(Duration d : durations) {
            if(from.plus(d).isAfter(now)) {
                return from.plus(d);
            }
        }
        if(interval <= 0) return null;
        Duration intervalDuration = Duration.of(interval, ChronoUnit.DAYS);
        LocalDateTime out = from.plus(intervalDuration);
        while(out.isBefore(now)) {
            out = out.plus(intervalDuration);
        }
        return out;
    }

    @Override
    public List<String> prefix() {
        return List.of("inactivity");
    }

    @Override
    public void validate() throws ConfigException {
        requireExactly(1, p(interval, "interval"), p(schedule, "schedule"));
    }
}
