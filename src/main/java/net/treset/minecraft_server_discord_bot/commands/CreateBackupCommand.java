package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
import net.treset.minecraft_server_discord_bot.server.ServerActions;
import net.treset.minecraft_server_discord_bot.system.*;
import net.treset.minecraft_server_discord_bot.upload.GoogleDriveClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreateBackupCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;
        if(DiscordBot.isModerator(event)) {
            Thread buThread = new Thread(() -> executeBackup(event));
            buThread.start();

            try {
                Thread.sleep(Config.server.backup_timeout * 1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(buThread.isAlive() || buThread.isInterrupted()) {
                Logger.error("Error creating backup. Timed out.");
                DiscordBot.sendText("Failed to create backup. Try again.", MessageOrigin.SCHEDULE);

                buThread.interrupt();

                ServerActions.undoBackupPreparation();
            }

        } else {
            output = "You don't have permission to do that.";
            event.getHook().sendMessage(output).queue();
            Logger.info("Handled.");
        }
    }

    private static void executeBackup(SlashCommandEvent event) {
        String output;

        if(!ServerActions.prepareServerForBackup()) {
            event.getHook().sendMessage("Failed to prepare sever for backup, aborting!").queue();
            return;
        }

        output = "Creating backup... (this may take a few minutes)";
        event.getHook().sendMessage(output).queue();
        Logger.info("Creating backup.");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);

        try {
            FileHandler.zipFile(Config.server.world_path, Config.server.backup_path + date + ".zip");
            if(!Config.drive.enabled) {
                output = "Created backup successfully.";
                DiscordBot.sendText(output, MessageOrigin.COMMAND);
                Logger.info("Local backup complete.");
                ServerActions.undoBackupPreparation();
                return;
            }
            output = "Created backup successfully. Uploading to Google Drive... (this may take a few minutes)";
            DiscordBot.sendText(output, MessageOrigin.COMMAND);
            Logger.info("Local backup complete. Uploading.");

            if(GoogleDriveClient.uploadFile(Config.server.backup_path + date + ".zip", date + ".zip", "application/x-zip-compressed", Config.drive.drive_folder_id) != null) {
                output = "Uploaded backup successfully.";
                Logger.info("Online backup complete.");
            } else {
                output = "Failed to upload backup. The backup still exists locally but isn't accessible online.";
                Logger.info("Online backup failed.");
            }
        } catch (IOException e) {
            output = "Failed to create backup.";
            Logger.error(e, "Local backup failed.");
        }

        DiscordBot.sendText(output, MessageOrigin.COMMAND);

        if(!ServerActions.undoBackupPreparation()) {
            DiscordBot.sendText("**FAILED to enable auto save after backup!** Please resolve manually!", MessageOrigin.COMMAND);
        }
    }
}
