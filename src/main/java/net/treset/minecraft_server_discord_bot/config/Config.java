package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.config.backup.BackupConfig;
import net.treset.minecraft_server_discord_bot.config.function.CommandsConfig;
import net.treset.minecraft_server_discord_bot.config.event.EventsConfig;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;
import net.treset.minecraft_server_discord_bot.notifications.NotificationHandlers;
import net.treset.minecraft_server_discord_bot.schedulers.AutoBackupScheduler;
import net.treset.minecraft_server_discord_bot.schedulers.EventScheduler;
import net.treset.minecraft_server_discord_bot.schedulers.InactivityScheduler;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.io.File;

public class Config {
    public DiscordConfig discord = new DiscordConfig();
    public CommandsConfig commands = new CommandsConfig();
    public EventsConfig events = new EventsConfig();
    public ServerConfig server = new ServerConfig();
    public BackupConfig backup;
    public InactivityConfig inactivity;
    public CrashConfig crash;

    private static final File CONFIG_FILE = new File("discman.yaml");
    private static final File DEBUG_CONFIG = new File("debug/discman.yaml");

    private static final ObjectMapper MAPPER = YAMLMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false)
            .build();

    private static Config config;

    public static Config get() {
        return config;
    }

    public static void load() throws ConfigException {
        File file = CONFIG_FILE;
        if(DEBUG_CONFIG.isFile()) {
            file = DEBUG_CONFIG;
        }

        Config newConfig;
        try {
            newConfig = MAPPER.readValue(file, Config.class);
        } catch (JacksonException e) {
            throw new ConfigException("Failed to deserialize config", e);
        }

        newConfig.discord.validate(newConfig);
        newConfig.commands.validate(newConfig);
        newConfig.events.validate(newConfig);
        newConfig.server.validate(newConfig);

        if(newConfig.backup != null) {
            newConfig.backup.validate(newConfig);
        }
        if(newConfig.inactivity != null) {
            newConfig.inactivity.validate(newConfig);
        }
        if(newConfig.crash != null) {
            newConfig.crash.validate(newConfig);
        }

        config = newConfig;

        ManagementClient.init();
        NotificationHandlers.register();
        AutoBackupScheduler.scheduleNext();
        InactivityScheduler.scheduleNext(EventScheduler.getLastEventTimestamp());
    }
}
