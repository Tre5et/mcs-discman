package net.treset.minecraft_server_discord_bot.schedulers;

import dev.treset.mcdl.servermanagement.vanilla.RpcMethods;
import dev.treset.mcdl.servermanagement.vanilla.types.RpcMessage;
import dev.treset.mcdl.servermanagement.vanilla.types.RpcSystemMessage;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.config.event.EventDiscordOutput;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ReminderScheduler {
    private static final ObjectMapper MAPPER = JsonMapper.builder().build();

    public static void schedule(Reminder reminder) {

    }

    public static void send(Reminder reminder, EventDiscordOutput output) {
        if(reminder.discord) output.output(reminder.discordPings + reminder.message, Set.of(reminder.channel));
        if(reminder.inGame) ManagementClient.get().send(
                RpcMethods.Server.SYSTEM_MESSAGE,
                new RpcSystemMessage(null, false, new RpcMessage(null, null, "[Discman] " + reminder.inGamePings + reminder.message)),
                r -> {},
                e -> Logger.warn(e, "Failed to send message '%s' to server", reminder.message)
        );
    }

    private static void addToStorageFile(Reminder reminder) throws IOException {
        File file = getStorageFile();
        List<Reminder> current = MAPPER.readValue(file, new TypeReference<>() {});
        List<Reminder> updated = new ArrayList<>(current);
        updated.add(reminder);
        MAPPER.writeValue(file, updated);
    }

    private static void removeFromStorageFile(Reminder reminder) throws IOException {
        File file = getStorageFile();
        List<Reminder> current = MAPPER.readValue(file, new TypeReference<>() {});
        MAPPER.writeValue(file, current.stream().filter(r -> r.id != reminder.id));
    }

    private static File getStorageFile() throws IOException {
        if(!Config.get().commands.reminder.enabled || Config.get().commands.reminder.storageFile == null || Config.get().commands.reminder.storageFile.isBlank()) {
            throw new IOException("Storage file is not configured correctly.");
        }
        File file = new File(Config.get().commands.reminder.storageFile);
        if(!file.exists()) {
            if(!file.createNewFile()) {
                throw new IOException("Failed to create storage file '" + Config.get().commands.reminder.storageFile + "'.");
            }
        }
        if(!file.isFile()) {
            throw new IOException("Storage file '" + Config.get().commands.reminder.storageFile + "' is not a file.");
        }
        return file;
    }

    public record Reminder(
            UUID id,
            Instant time,
            String message,
            String discordPings,
            String inGamePings,
            GuildMessageChannel channel,
            boolean inGame,
            boolean discord
    ) {}
}
