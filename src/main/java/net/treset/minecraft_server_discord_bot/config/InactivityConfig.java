package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.exception.ConfigException;

import java.util.List;

public class InactivityConfig extends Validatable {
    public int interval;
    public List<Integer> schedule = List.of();


    @Override
    public List<String> prefix() {
        return List.of("inactivity");
    }

    @Override
    public void validate() throws ConfigException {
        requireExactly(1, p(interval, "interval"), p(schedule, "schedule"));
    }
}
