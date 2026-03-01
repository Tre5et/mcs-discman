package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.logging.OutputConsumer;
import net.treset.minecraft_server_discord_bot.server.BackupHandler;
import net.treset.minecraft_server_discord_bot.system.*;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class CreateBackupCommand {
    private static final DateTimeFormatter backupNameFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm");

    public static void handleCommand(SlashCommandEvent event) {
        if(DiscordBot.isModerator(event)) {
            String mode = Objects.requireNonNull(event.getOption("mode")).getAsString();
            BackupHandler.Mode backupMode = switch (mode) {
                case "restart" -> BackupHandler.Mode.RESTART;
                case "while-running" -> BackupHandler.Mode.WHILE_RUNNING;
                default -> {
                    event.getHook().sendMessage("Invalid mode").queue();
                    yield null;
                }
            };
            if(backupMode == null) {
                return;
            }
            boolean skipNotify;
            if(event.getOption("skip-notify") != null) {
                skipNotify = Objects.requireNonNull(event.getOption("skip-notify")).getAsBoolean();
            } else {
                skipNotify = false;
            }
            new Thread(() -> BackupHandler.execute(
                    backupMode,
                    backupNameFormatter::format,
                    OutputConsumer.allResponse(MessageOrigin.COMMAND, event.getHook()),
                    skipNotify
            )).start();
        } else {
            event.getHook().sendMessage("You don't have permission to do that.").queue();
            Logger.info("Handled.");
        }
    }
}
