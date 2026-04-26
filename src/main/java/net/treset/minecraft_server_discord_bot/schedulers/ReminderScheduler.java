package net.treset.minecraft_server_discord_bot.schedulers;

import dev.treset.mcdl.servermanagement.vanilla.RpcMethods;
import dev.treset.mcdl.servermanagement.vanilla.types.RpcMessage;
import dev.treset.mcdl.servermanagement.vanilla.types.RpcSystemMessage;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.event.EventDiscordOutput;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.config.message.MessageContext;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ReminderScheduler {
    private static final ObjectMapper MAPPER = JsonMapper.builder().build();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final Map<UUID, ScheduledFuture<?>> scheduled = new HashMap<>();

    public static void load(CommandConfig.Reminder config) throws IOException {
        File file = getStorageFile(config);
        List<Reminder> reminders = MAPPER.readValue(file, new TypeReference<>() {});
        for(Reminder r : reminders) {
            schedule(r, config);
        }
    }

    public static void schedule(Reminder reminder, CommandConfig.Reminder config) throws IOException {
        if(scheduled.containsKey(reminder.id)) {
            return;
        }

        Instant now = Instant.now();
        if(!reminder.time.isAfter(now)) {
            send(reminder, new EventDiscordOutput.Message());
            return;
        }

        long until = Duration.between(now, reminder.time).getSeconds();
        ScheduledFuture<?> schedule = scheduler.schedule(() -> send(reminder, new EventDiscordOutput.Message()), until, TimeUnit.SECONDS);
        scheduled.put(reminder.id, schedule);

        addToStorageFile(reminder, config);
    }

    public static void send(Reminder reminder, EventDiscordOutput output) {
        if(reminder.discord && Config.get().commands.reminder.allowDiscord) {
            GuildMessageChannel channel = Config.get().discord.jdaGuild.getChannelById(GuildMessageChannel.class, reminder.channel);
            if(channel == null) {
                Logger.error("Channel with id %s can not be found for message with id %s.", reminder.channel, reminder.id);
                channel = Config.get().discord.jdaChannels.get("default");
                output.output(Config.get().commands.reminder.messageInvalidChannel.get(reminder, MessageContext.DISCORD), Set.of(channel));
            }
            output.output(reminder.discordPings + reminder.message, Set.of(channel));
        }
        if(reminder.inGame && Config.get().commands.reminder.allowInGame) ManagementClient.get().send(
                RpcMethods.Server.SYSTEM_MESSAGE,
                new RpcSystemMessage(null, false, new RpcMessage(null, null, "[Discman] " + reminder.inGamePings + reminder.message)),
                r -> {},
                e -> Logger.warn(e, "Failed to send message '%s' to server", reminder.message)
        );
        scheduled.remove(reminder.id);
        try {
            removeFromStorageFile(reminder, Config.get().commands.reminder);
        } catch (IOException e) {
            Logger.error(e, "Failed to remove reminder %s from storage file.", reminder.id);
            output.output(Config.get().commands.reminder.messageRemoveFailed.get(reminder, MessageContext.DISCORD), Set.of(Config.get().discord.jdaChannels.get("default")));
        }
    }

    private static void addToStorageFile(Reminder reminder, CommandConfig.Reminder config) throws IOException {
        File file = getStorageFile(config);
        List<Reminder> current = MAPPER.readValue(file, new TypeReference<>() {});
        List<Reminder> updated = new ArrayList<>(current);
        updated.add(reminder);
        MAPPER.writeValue(file, updated);
    }

    private static void removeFromStorageFile(Reminder reminder, CommandConfig.Reminder config) throws IOException {
        File file = getStorageFile(config);
        List<Reminder> current = MAPPER.readValue(file, new TypeReference<>() {});
        MAPPER.writeValue(file, current.stream().filter(r -> !r.id.equals(reminder.id)));
    }

    private static File getStorageFile(CommandConfig.Reminder config) throws IOException {
        if(!config.enabled || config.storageFile == null || config.storageFile.isBlank()) {
            throw new IOException("Storage file is not configured correctly.");
        }
        File file = new File(config.storageFile);
        if(!file.exists()) {
            if(!file.createNewFile()) {
                throw new IOException("Failed to create storage file '" + config.storageFile + "'.");
            }
            MAPPER.writeValue(file, List.of());
        }
        if(!file.isFile()) {
            throw new IOException("Storage file '" + config.storageFile + "' is not a file.");
        }
        return file;
    }

    public record Reminder(
            UUID id,
            Instant time,
            String message,
            String discordPings,
            String inGamePings,
            String channel,
            boolean inGame,
            boolean discord
    ) {}
}
