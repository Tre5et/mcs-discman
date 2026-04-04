package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.treset.minecraft_server_discord_bot.config.event.EventDiscordOutput;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
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
    protected void process(SlashCommandInteraction interaction, CommandConfig.CreateBackup function) {

        String mode = Objects.requireNonNull(interaction.getOption("mode")).getAsString();
        BackupHandler.Mode backupMode = switch (mode) {
            case "restart" -> BackupHandler.Mode.RESTART;
            case "while-running" -> BackupHandler.Mode.WHILE_RUNNING;
            default -> {
                interaction.getHook().sendMessage(function.messageInvalidMode.get()).queue();
                yield null;
            }
        };
        if(backupMode == null) {
            return;
        }
        boolean skipNotify;
        if(interaction.getOption("skip-notify") != null) {
            skipNotify = Objects.requireNonNull(interaction.getOption("skip-notify")).getAsBoolean();
        } else {
            skipNotify = false;
        }
        new Thread(() -> BackupHandler.execute(
                backupMode,
                backupNameFormatter::format,
                new EventDiscordOutput.Reply(interaction.getHook()),
                skipNotify
        )).start();
    }

    @Override
    public CommandData data() {
        return Commands.slash("createbackup", "Create a backup! [Moderator only]").addOptions(
                new OptionData(OptionType.STRING, "mode", "The backup mode to use.", true)
                        .addChoice("restart", "restart").addChoice("while running", "while-running"),
                new OptionData(OptionType.BOOLEAN, "skip-notify", "Don't notify the players and create the backup instantly")
        );
    }
}
