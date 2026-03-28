package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.function.FunctionConfig;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
import net.treset.minecraft_server_discord_bot.logging.OutputConsumer;
import net.treset.minecraft_server_discord_bot.server.BackupHandler;
import net.treset.minecraft_server_discord_bot.system.*;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Supplier;

public class CreateBackupCommand extends Command<FunctionConfig.CreateBackup> {
    private static final DateTimeFormatter backupNameFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm");

    public CreateBackupCommand(Supplier<FunctionConfig.CreateBackup> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandEvent event, FunctionConfig.CreateBackup function) {

        String mode = Objects.requireNonNull(event.getOption("mode")).getAsString();
        BackupHandler.Mode backupMode = switch (mode) {
            case "restart" -> BackupHandler.Mode.RESTART;
            case "while-running" -> BackupHandler.Mode.WHILE_RUNNING;
            default -> {
                event.getHook().sendMessage(function.messageInvalidMode.get()).queue();
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
    }
}
