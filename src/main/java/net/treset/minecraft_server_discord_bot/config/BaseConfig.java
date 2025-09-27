package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.system.Formatter;

public abstract class BaseConfig {
    private final String config;

    public BaseConfig(String config) {
        this.config = config;
    }

    public String load(String option) {
        String start = String.format("^%s= *", option);
        String end = " *;|$";
        String output = Formatter.findStringBetween(config, start, end);
        if(output.isBlank()) return null;
        return output;
    }
}
