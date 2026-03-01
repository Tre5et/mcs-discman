package net.treset.minecraft_server_discord_bot.config;

import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
import net.treset.minecraft_server_discord_bot.exception.ConfigException;
import net.treset.minecraft_server_discord_bot.logging.OutputConsumer;
import net.treset.minecraft_server_discord_bot.notifications.NotificationHandlers;
import net.treset.minecraft_server_discord_bot.server.AutoBackupScheduler;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;
import net.treset.minecraft_server_discord_bot.upload.GoogleDriveClient;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.io.File;

public class Config {
    public DiscordConfig discord = new DiscordConfig();
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

        newConfig.server.validate();
        newConfig.discord.validate();

        if(newConfig.backup != null) {
            newConfig.backup.validate();
        }
        if(newConfig.inactivity != null) {
            newConfig.inactivity.validate();
        }
        if(newConfig.crash != null) {
            newConfig.crash.validate();
        }

        config = newConfig;

        if(config.backup != null && config.backup.auto != null) {
            AutoBackupScheduler.scheduleNext(OutputConsumer.all(MessageOrigin.SCHEDULE));
        }
        GoogleDriveClient.init();
        ManagementClient.init();
        NotificationHandlers.register();
    }
}
