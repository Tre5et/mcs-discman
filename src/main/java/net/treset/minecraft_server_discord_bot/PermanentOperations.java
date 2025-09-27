package net.treset.minecraft_server_discord_bot;

import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.messaging.MessageOrigin;
import net.treset.minecraft_server_discord_bot.rpc.ConnectionManager;
import net.treset.minecraft_server_discord_bot.tools.*;

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
                MessageManager.log("Failed to wait for permanent operations loop", LogLevel.ERROR, e);
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
                    dontCreateAutoBackup(ServerTools.isServerRunning() && Config.server.log_no_backup);
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
            MessageManager.log("Error creating auto backup. Timed out.", LogLevel.ERROR);
            MessageManager.sendText("Failed to create auto backup.", MessageOrigin.SCHEDULE);

            buThread.interrupt();

            ServerTools.undoBackupPreparation();
        }
    }

    private static void executeBackup() {
        String output;
        if(!ServerTools.prepareServerForBackup()) {
            MessageManager.sendText("**Error preparing server for auto backup.** Aborting!", MessageOrigin.SCHEDULE);
            return;
        }

        output = "Creating auto-backup.";
        MessageManager.sendText(output, MessageOrigin.SCHEDULE);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);
        try {
            FileTools.zipFile(Config.server.backup_path, Config.server.backup_path + date + "-auto.zip");
            MessageManager.log("Local backup complete.", LogLevel.INFO);
            if(DriveTools.uploadFile(Config.server.backup_path + date + "-auto.zip", date + "-auto.zip", "application/x-zip-compressed", Config.drive.drive_folder_id) != null) {
                output = "Created auto-backup successfully.";
                MessageManager.log("Online backup complete.", LogLevel.INFO);
            } else {
                output = "Created local auto-backup successfully.";
                MessageManager.log("Error creating online backup. Unable to upload file.", LogLevel.WARN);
            }
        } catch (IOException e) {
            output = "Failed to create auto-backup.";
            MessageManager.log("Error Zipping backup file.", LogLevel.ERROR, e);
        }
        MessageManager.sendText(output, MessageOrigin.SCHEDULE);

        if(!ServerTools.undoBackupPreparation()) {
            MessageManager.sendText("**FAILED to enable auto save after auto backup!** Please resolve manually!", MessageOrigin.SCHEDULE);
        }
    }

    private static void dontCreateAutoBackup(boolean log) {
        if(log) {
            String output = "Not creating a auto-backup because nothing happened today.";
            MessageManager.sendText(output, MessageOrigin.SCHEDULE);
        }
        MessageManager.log("Not necessary to create backup.", LogLevel.INFO);
    }

    private static void changeDay() {
        DateTimeFormatter dtfD = DateTimeFormatter.ofPattern("dd");
        wasBackedUpToday = false;
        prevDay = dtfD.format(LocalDateTime.now());
        MessageManager.log("Day changed.", LogLevel.DEBUG);
    }

    private static void logInactivity() {
        if(!ServerTools.isServerRunning()) return;

        daysSinceActivity++;

        if(Config.server.inactivity_reminder_enabled && daysSinceActivity % Config.server.inactivity_reminder == 0) {
            MessageManager.sendText(String.format("Reminder: The server hasn't been used in %s days. Consider stopping it.", daysSinceActivity), MessageOrigin.SCHEDULE);
            MessageManager.log(String.format("Inactivity reminder sent after %s days.", daysSinceActivity), LogLevel.INFO);
        }
    }

    private static void checkForCrash() {
        if(!Config.server.auto_restart) return;

        new Thread(PermanentOperations::executeCrashHandler).start();
    }

    private static void executeCrashHandler() {
        boolean running = ServerTools.isServerRunning();
        if(!running && prevRunning) {
            ConnectionManager.forceDisconnect();
            prevRunning = false;
            if(isStopExpected) {
                MessageManager.log("Expected server stop detected.", LogLevel.DEBUG);
                isStopExpected = false;
            } else if(crashesInShortTime >= 5) {
                MessageManager.sendText("Server stopped unexpectedly, has crashed too often in a short time, not attempting to restart.", MessageOrigin.SCHEDULE);

                MessageManager.log("Server stopped unexpectedly. Crashed to often. Not attempting restart.", LogLevel.WARN);
            } else {
                MessageManager.sendText("Server stopped unexpectedly, attempting to restart...", MessageOrigin.SCHEDULE);

                MessageManager.log("Server stopped unexpectedly. Restarting.", LogLevel.WARN);

                crashedRecently = 60; //600 sec * 0.1 loops per second
                crashesInShortTime++;

                ServerTools.startServer();

                double time = 0;
                while (!ServerTools.isServerRunning()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        MessageManager.log("Failed to wait for server crash check", LogLevel.ERROR, e);
                    }
                    time += .2d;
                    if (time >= 30) {
                        MessageManager.sendText("Failed to start Server, not trying again.", MessageOrigin.SCHEDULE);

                        MessageManager.log("Failed to start server after unexpected stop.", LogLevel.ERROR);
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
