package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.treset.minecraft_server_discord_bot.config.event.EventDiscordOutput;
import net.treset.minecraft_server_discord_bot.config.function.CommandConfig;
import net.treset.minecraft_server_discord_bot.server.BackupHandler;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Supplier;

public class CreateBackupCommand extends Command<CommandConfig.CreateBackup> {
    private static final DateTimeFormatter backupNameFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm");

    public CreateBackupCommand(Supplier<CommandConfig.CreateBackup> configSupplier) {
        super(configSupplier);
    }

    @Override
    protected void process(SlashCommandEvent event, CommandConfig.CreateBackup function) {

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
                new EventDiscordOutput.Reply(event.getHook()),
                skipNotify
        )).start();
    }

    @Override
    public CommandData data() {
        return new CommandData("createbackup", "Create a backup! [Moderator only]").addOptions(
                new OptionData(OptionType.STRING, "mode", "The backup mode to use.", true)
                        .addChoice("restart", "restart").addChoice("while running", "while-running"),
                new OptionData(OptionType.BOOLEAN, "skip-notify", "Don't notify the players and create the backup instantly")
        );
    }
}
