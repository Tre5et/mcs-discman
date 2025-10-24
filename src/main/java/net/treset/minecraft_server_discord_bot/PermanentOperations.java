package net.treset.minecraft_server_discord_bot;

import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;
import net.treset.minecraft_server_discord_bot.server.ServerActions;
import net.treset.minecraft_server_discord_bot.system.*;
import net.treset.minecraft_server_discord_bot.upload.GoogleDriveClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PermanentOperations {
    private static boolean terminatePermanentLoop = false;

    public static boolean isBackupEnabled = true;

    private static boolean hasSomethingHappened = true;
    private static boolean wasBackedUpToday = false;
    private static String prevDay = "";

    public static boolean isStopExpected = false;
    private static boolean prevRunning = false;
    private static int crashedRecently = 0;
    private static int crashesInShortTime = 0;

    private static int daysSinceActivity = 0;

    public static void setSomethingHappened() { hasSomethingHappened = true; }

    public static void permanentLoop() {
        while(!terminatePermanentLoop) {
            autoBackup();

            checkForCrash();

            try {
                Thread.sleep(Config.discord.update_interval * 1000L);
            } catch (InterruptedException e) {
                Logger.error(e, "Failed to wait for permanent operations loop");
            }
        }

        terminatePermanentLoop = false;
    }

    private static void autoBackup() {
        //auto-backup
        DateTimeFormatter dtfH = DateTimeFormatter.ofPattern("HH");

        LocalDateTime now = LocalDateTime.now();
        boolean isCorrectHour = dtfH.format(now).equals(Config.server.backup_hour_formatted);

        if(!wasBackedUpToday && isCorrectHour) {
            if(hasSomethingHappened) {
                daysSinceActivity = 0;
                if(Config.server.backup_enabled) new Thread(PermanentOperations::createAutoBackup).start();
            } else {
                logInactivity();
                if(Config.server.backup_enabled) {
                    dontCreateAutoBackup(ServerActions.isRunning() && Config.server.log_no_backup);
                }
            }

            wasBackedUpToday = true;
            hasSomethingHappened = false;
        }

        DateTimeFormatter dtfD = DateTimeFormatter.ofPattern("dd");
        if(!dtfD.format(now).equals(prevDay)) changeDay();
    }

    private static void createAutoBackup() {

        Thread buThread = new Thread(PermanentOperations::executeBackup);
        buThread.start();

        try {
            Thread.sleep(Config.server.backup_timeout * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(buThread.isAlive() || buThread.isInterrupted()) {
            Logger.error("Error creating auto backup. Timed out.");
            DiscordBot.sendText("Failed to create auto backup.", MessageOrigin.SCHEDULE);

            buThread.interrupt();

            ServerActions.undoBackupPreparation();
        }
    }

    private static void executeBackup() {
        String output;
        if(!ServerActions.prepareServerForBackup()) {
            DiscordBot.sendText("**Error preparing server for auto backup.** Aborting!", MessageOrigin.SCHEDULE);
            return;
        }

        output = "Creating auto-backup.";
        DiscordBot.sendText(output, MessageOrigin.SCHEDULE);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);
        try {
            FileHandler.zipFile(Config.server.backup_path, Config.server.backup_path + date + "-auto.zip");
            Logger.info("Local backup complete.");
            if(GoogleDriveClient.uploadFile(Config.server.backup_path + date + "-auto.zip", date + "-auto.zip", "application/x-zip-compressed", Config.drive.drive_folder_id) != null) {
                output = "Created auto-backup successfully.";
                Logger.info("Online backup complete.");
            } else {
                output = "Created local auto-backup successfully.";
                Logger.warn("Error creating online backup. Unable to upload file.");
            }
        } catch (IOException e) {
            output = "Failed to create auto-backup.";
            Logger.error(e, "Error Zipping backup file.");
        }
        DiscordBot.sendText(output, MessageOrigin.SCHEDULE);

        if(!ServerActions.undoBackupPreparation()) {
            DiscordBot.sendText("**FAILED to enable auto save after auto backup!** Please resolve manually!", MessageOrigin.SCHEDULE);
        }
    }

    private static void dontCreateAutoBackup(boolean log) {
        if(log) {
            String output = "Not creating a auto-backup because nothing happened today.";
            DiscordBot.sendText(output, MessageOrigin.SCHEDULE);
        }
        Logger.info("Not necessary to create backup.");
    }

    private static void changeDay() {
        DateTimeFormatter dtfD = DateTimeFormatter.ofPattern("dd");
        wasBackedUpToday = false;
        prevDay = dtfD.format(LocalDateTime.now());
        Logger.debug("Day changed.");
    }

    private static void logInactivity() {
        if(!ServerActions.isRunning()) return;

        daysSinceActivity++;

        if(Config.server.inactivity_reminder_enabled && daysSinceActivity % Config.server.inactivity_reminder == 0) {
            DiscordBot.sendText(String.format("Reminder: The server hasn't been used in %s days. Consider stopping it.", daysSinceActivity), MessageOrigin.SCHEDULE);
            Logger.info("Inactivity reminder sent after %s days.", daysSinceActivity);
        }
    }

    private static void checkForCrash() {
        if(!Config.server.auto_restart) return;

        new Thread(PermanentOperations::executeCrashHandler).start();
    }

    private static void executeCrashHandler() {
        boolean running = ServerActions.isRunning();
        if(!running && prevRunning) {
            ManagementClient.get().forceDisconnect();
            prevRunning = false;
            if(isStopExpected) {
                Logger.debug("Expected server stop detected.");
                isStopExpected = false;
            } else if(crashesInShortTime >= 5) {
                DiscordBot.sendText("Server stopped unexpectedly, has crashed too often in a short time, not attempting to restart.", MessageOrigin.SCHEDULE);

                Logger.warn("Server stopped unexpectedly. Crashed to often. Not attempting restart.");
            } else {
                DiscordBot.sendText("Server stopped unexpectedly, attempting to restart...", MessageOrigin.SCHEDULE);

                Logger.warn("Server stopped unexpectedly. Restarting.");

                crashedRecently = 60; //600 sec * 0.1 loops per second
                crashesInShortTime++;

                ServerActions.startServer();

                double time = 0;
                while (!ServerActions.isRunning()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Logger.error(e, "Failed to wait for server crash check");
                    }
                    time += .2d;
                    if (time >= 30) {
                        DiscordBot.sendText("Failed to start Server, not trying again.", MessageOrigin.SCHEDULE);

                        Logger.error("Failed to start server after unexpected stop.");
                        break;
                    }
                }
                prevRunning = true;
            }
        } else {
            prevRunning = running;
            if(crashedRecently > 0) crashedRecently--;
            else crashesInShortTime = 0;
        }
    }
}
