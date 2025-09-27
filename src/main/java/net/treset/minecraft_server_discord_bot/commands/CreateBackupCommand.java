package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.messaging.MessageOrigin;
import net.treset.minecraft_server_discord_bot.tools.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreateBackupCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output;
        if(DiscordTools.isModerator(event)) {
            Thread buThread = new Thread(() -> executeBackup(event));
            buThread.start();

            try {
                Thread.sleep(Config.server.backup_timeout * 1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(buThread.isAlive() || buThread.isInterrupted()) {
                MessageManager.log("Error creating backup. Timed out.", LogLevel.ERROR);
                MessageManager.sendText("Failed to create backup. Try again.", MessageOrigin.SCHEDULE);

                buThread.interrupt();

                ServerTools.undoBackupPreparation();
            }

        } else {
            output = "You don't have permission to do that.";
            event.getHook().sendMessage(output).queue();
            MessageManager.log("Handled.", LogLevel.INFO);
        }
    }

    private static void executeBackup(SlashCommandEvent event) {
        String output;

        if(!ServerTools.prepareServerForBackup()) {
            event.getHook().sendMessage("Failed to prepare sever for backup, aborting!").queue();
            return;
        }

        output = "Creating backup... (this may take a few minutes)";
        event.getHook().sendMessage(output).queue();
        MessageManager.log("Creating backup.", LogLevel.INFO);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);

        try {
            FileTools.zipFile(Config.server.world_path, Config.server.backup_path + date + ".zip");
            if(!Config.drive.enabled) {
                output = "Created backup successfully.";
                MessageManager.sendText(output, MessageOrigin.COMMAND);
                MessageManager.log("Local backup complete.", LogLevel.INFO);
                ServerTools.undoBackupPreparation();
                return;
            }
            output = "Created backup successfully. Uploading to Google Drive... (this may take a few minutes)";
            MessageManager.sendText(output, MessageOrigin.COMMAND);
            MessageManager.log("Local backup complete. Uploading.", LogLevel.INFO);

            if(DriveTools.uploadFile(Config.server.backup_path + date + ".zip", date + ".zip", "application/x-zip-compressed", Config.drive.drive_folder_id) != null) {
                output = "Uploaded backup successfully.";
                MessageManager.log("Online backup complete.", LogLevel.INFO);
            } else {
                output = "Failed to upload backup. The backup still exists locally but isn't accessible online.";
                MessageManager.log("Online backup failed.", LogLevel.WARN);
            }
        } catch (IOException e) {
            output = "Failed to create backup.";
            MessageManager.log("Local backup failed.", LogLevel.ERROR, e);
        }

        MessageManager.sendText(output, MessageOrigin.COMMAND);

        if(!ServerTools.undoBackupPreparation()) {
            MessageManager.sendText("**FAILED to enable auto save after backup!** Please resolve manually!", MessageOrigin.COMMAND);
        }
    }
}
