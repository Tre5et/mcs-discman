package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.tools.DiscordTools;

import java.util.Objects;

public class AutoBackupCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";
        if(DiscordTools.isModerator(event)) {
            if(Objects.requireNonNull(event.getOption("state")).getAsBoolean()) {
                if(PermanentOperations.isBackupEnabled) {
                    output = "Auto-Backup is already enabled.";
                    event.reply(output).queue();
                    DiscordBot.LOGGER.info("AutoBackup Command handled: true: already active");
                } else {
                    PermanentOperations.isBackupEnabled = true;
                    output = "Enabled Auto-Backup";
                    event.reply(output).queue();
                    DiscordBot.LOGGER.info("AutoBackup Command handled: true: success");
                }
            } else {
                if(PermanentOperations.isBackupEnabled) {
                    PermanentOperations.isBackupEnabled = false;
                    output = "Disabled Auto-Backup";
                    event.reply(output).queue();
                    DiscordBot.LOGGER.info("AutoBackup Command handled: false: success");
                } else {
                    output = "Auto-Backup is already disabled.";
                    event.reply(output).queue();
                    DiscordBot.LOGGER.info("AutoBackup Command handled: false: already inactive");
                }
            }
        } else {
            output = "You don't have permission to do that.";
            event.reply(output).queue();
            DiscordBot.LOGGER.info("AutoBackup Command handled: permission required");
        }
    }
}
