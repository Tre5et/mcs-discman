package net.treset.minecraft_server_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.messaging.MessageOrigin;
import net.treset.minecraft_server_discord_bot.tools.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreateBackupCommand {
    public static void handleCommand(SlashCommandEvent event) {
        String output = "";
        if(DiscordTools.isModerator(event)) {
            Thread buThread = new Thread(() -> executeBackup(event));
            buThread.start();

            try {
                Thread.sleep(ConfigTools.PERMA_CONFIG.BACKUP_TIMEOUT * 1000L);
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
        String output = "";

        if (ServerTools.isServerRunning()) ServerTools.prepareServerForBackup();

        output = "Creating backup... (this may take a few minutes)";
        event.getHook().sendMessage(output).queue();
        MessageManager.log("Creating backup.", LogLevel.INFO);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);

        if (FileTools.zipFile(ConfigTools.CONFIG.WORLD_PATH, ConfigTools.CONFIG.BACKUPS_PATH + date + ".zip")) {
            if(!ConfigTools.CONFIG.DRIVE_UPLOAD) {
                output = "Created backup successfully.";
                MessageManager.sendText(output, MessageOrigin.COMMAND);
                MessageManager.log("Local backup complete.", LogLevel.INFO);
                ServerTools.undoBackupPreparation();
                return;
            }
            output = "Created backup successfully. Uploading to Google Drive... (this may take a few minutes)";
            MessageManager.sendText(output, MessageOrigin.COMMAND);
            MessageManager.log("Local backup complete. Uploading.", LogLevel.INFO);

            if(DriveTools.uploadFile(ConfigTools.CONFIG.BACKUPS_PATH + date + ".zip", date + ".zip", "application/x-zip-compressed", ConfigTools.CONFIG.DRIVE_FOLDER_ID) != null) {
                output = "Uploaded backup successfully.";
                MessageManager.log("Online backup complete.", LogLevel.INFO);
            } else {
                output = "Failed to upload backup. The backup still exists locally but isn't accessible online.";
                MessageManager.log("Online backup failed.", LogLevel.WARN);
            }
        } else {
            output = "Failed to create a backup. Try again.";
            MessageManager.log("Local backup failed. Error creating file.", LogLevel.ERROR);
        }
        MessageManager.sendText(output, MessageOrigin.COMMAND);

        ServerTools.undoBackupPreparation();
    }
}
