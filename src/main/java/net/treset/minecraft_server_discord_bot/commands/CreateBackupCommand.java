package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.tools.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreateBackupCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";
        if(DiscordTools.isModerator(event)) {
            if (ServerTools.isServerRunning()) ServerTools.prepareServerForBackup();

            output = "Creating backup... (this may take a few minutes)";
            event.reply(output).queue();
            DiscordBot.LOGGER.info("CreateBackup Command handling: backing up");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm");
            LocalDateTime now = LocalDateTime.now();
            String date = dtf.format(now);

            if (FileTools.zipFile(ConfigTools.CONFIG.WORLD_PATH, ConfigTools.CONFIG.BACKUPS_PATH + date + ".zip")) {
                if(!ConfigTools.CONFIG.DRIVE_UPLOAD) {
                    output = "Created backup successfully.";
                    DiscordBot.BOT_CHANNEL.sendMessage(output).queue();
                    DiscordBot.LOGGER.info("CreateBackup Command handled: local backup complete");
                    ServerTools.undoBackupPreparation();
                    return;
                }
                output = "Created backup successfully. Uploading to Google Drive... (this may take a few minutes)";
                DiscordBot.BOT_CHANNEL.sendMessage(output).queue();
                DiscordBot.LOGGER.info("CreateBackup Command handling: local backup complete");

                if(DriveTools.uploadFile(ConfigTools.CONFIG.BACKUPS_PATH + date + ".zip", date + ".zip", "application/x-zip-compressed", ConfigTools.CONFIG.DRIVE_FOLDER_ID) != null) {
                    output = "Uploaded backup successfully.";
                    DiscordBot.LOGGER.info("CreateBackup Command handled: success");
                } else {
                    output = "Failed to upload backup. The backup still exists locally but isn't accessible online.";
                    DiscordBot.LOGGER.warn("CreateBackup Command FAILED: failed to upload file");
                }
            } else {
                output = "Failed to create a backup. Try again.";
                DiscordBot.LOGGER.error("CreateBackup Command FAILED: failed to create file");
            }
            DiscordBot.BOT_CHANNEL.sendMessage(output).queue();

            ServerTools.undoBackupPreparation();
        } else {
            output = "You don't have permission to do that.";
            event.reply(output).queue();
            DiscordBot.LOGGER.info("CreateBackup Command handled: permission required");
        }
    }
}
