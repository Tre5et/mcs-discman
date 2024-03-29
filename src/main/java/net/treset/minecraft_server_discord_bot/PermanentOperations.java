package net.treset.minecraft_server_discord_bot;

import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.messaging.MessageOrigin;
import net.treset.minecraft_server_discord_bot.networking.ConnectionManager;
import net.treset.minecraft_server_discord_bot.tools.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PermanentOperations {
    private static String prevData = "";
    private static boolean isLoggingEnabled = false;
    private static boolean isStartLoggingEnabled = false;
    private static boolean isJoinLoggingEnabled = false;
    public static boolean isLoggingFull = false;
    private static boolean terminatePermanentLoop = false;

    public static boolean isBackupEnabled = true;
    private static String backupHour = "01";
    private static int backupTimeout = 1200;
    private static boolean isLogNoBackup = true;

    private static boolean hasSomethingHappened = true;
    private static boolean wasBackedUpToday = false;
    private static String prevDay = "";

    public static boolean isCrashCheckEnabled = false;
    public static boolean isStopExpected = false;
    private static boolean prevRunning = false;
    private static int crashedRecently = 0;
    private static int crashesInShortTime = 0;

    private static int inactivityReminder = 0;
    private static int daysSinceActivity = 0;

    public static void setSomethingHappened() { hasSomethingHappened = true; }

    public static void permanentLoop() {
        isLoggingEnabled = ConfigTools.PERMA_CONFIG.LOGGING_ENABLED;
        isStartLoggingEnabled = ConfigTools.PERMA_CONFIG.START_LOGGING;
        isJoinLoggingEnabled = ConfigTools.PERMA_CONFIG.JOIN_LOGGING;
        isLoggingFull = ConfigTools.PERMA_CONFIG.FULL_LOGGING;
        isBackupEnabled = ConfigTools.PERMA_CONFIG.BACKUP_ENABLED;
        backupHour = ConfigTools.PERMA_CONFIG.BACKUP_HOUR;
        backupTimeout = ConfigTools.PERMA_CONFIG.BACKUP_TIMEOUT;
        isCrashCheckEnabled = ConfigTools.PERMA_CONFIG.CRASH_CHECK_ENABLED;
        isLogNoBackup = ConfigTools.PERMA_CONFIG.LOG_NO_BACKUP;
        inactivityReminder = ConfigTools.PERMA_CONFIG.INACTIVITY_REMINDER_ENABLED;

        try {
            prevData = FileTools.readFile(ConfigTools.CONFIG.LOG_PATH);
        } catch (IOException e) {
            MessageManager.log("Error reading log file.", LogLevel.WARN, e);
            prevData = "";
        }

        while(!terminatePermanentLoop) {

            logFromFile();

            autoBackup();

            checkForCrash();

            MiscTools.timeout(10000);
        }

        terminatePermanentLoop = false;
    }

    public static void logFromFile() {
        if (isLoggingEnabled) {
            String data;
            try {
                data = FileTools.readFile(ConfigTools.CONFIG.LOG_PATH);
            } catch (IOException e) {
                MessageManager.log("Error reading log file.", LogLevel.WARN, e);
                return;
            }
            String formattedData = data.replace(prevData, "");

            if (!formattedData.isEmpty()) {
                if (isLoggingFull) logFullConsole(formattedData);
                else {
                    String[] lines = formattedData.split("\\r?\\n");

                    for (String line : lines) {
                        if (isStartLoggingEnabled) logStarted(line);
                        if (isJoinLoggingEnabled && (!ConnectionManager.isConnected() || !ConfigTools.CLIENT_CONFIG.OVERRIDE_CONSOLE_READER)) {
                            logPlayerJoin(line);
                            logPlayerLeave(line);
                        }
                    }
                }
                prevData = data;
            }
        }
    }

    private static void logFullConsole(String input) {
        MessageManager.sendText(input, MessageOrigin.LOG_FILE);
        MessageManager.log("Full console logged.", LogLevel.INFO);
        hasSomethingHappened = true;
    }

    private static void logStarted(String input) {
        if (input.contains("Done (")) {
            MessageManager.sendStarted(MessageOrigin.LOG_FILE);
            ConfigTools.setPlayers(new String[]{});
            hasSomethingHappened = true;
        }
    }

    private static void logPlayerJoin(String input) {
        String playerName = FormatTools.findStringBetween(input, "INFO]: ", " joined the game");
        if(!playerName.isEmpty())
            MessageManager.sendJoin(playerName, MessageOrigin.LOG_FILE);
    }

    private static void logPlayerLeave(String input) {
        String playerName = FormatTools.findStringBetween(input, "INFO]: ", " left the game");
        if(!playerName.isEmpty())
            MessageManager.sendLeave(playerName, MessageOrigin.LOG_FILE);
    }

    private static void autoBackup() {
        //auto-backup
        DateTimeFormatter dtfH = DateTimeFormatter.ofPattern("HH");

        LocalDateTime now = LocalDateTime.now();
        boolean isCorrectHour = dtfH.format(now).equals(backupHour);

        if(!wasBackedUpToday && isCorrectHour) {
            if(hasSomethingHappened) {
                daysSinceActivity = 0;
                if(isBackupEnabled) new Thread(PermanentOperations::createAutoBackup).start();
            } else {
                logInactivity();
                if(isBackupEnabled) {
                    dontCreateAutoBackup(ServerTools.isServerRunning() && isLogNoBackup);
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
            Thread.sleep(backupTimeout * 1000L);
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
        if (ServerTools.isServerRunning()) ServerTools.prepareServerForBackup();

        output = "Creating auto-backup.";
        MessageManager.sendText(output, MessageOrigin.SCHEDULE);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);
        try {
            FileTools.zipFile(ConfigTools.CONFIG.WORLD_PATH, ConfigTools.CONFIG.BACKUPS_PATH + date + "-auto.zip");
            MessageManager.log("Local backup complete.", LogLevel.INFO);
            if(DriveTools.uploadFile(ConfigTools.CONFIG.BACKUPS_PATH + date + "-auto.zip", date + "-auto.zip", "application/x-zip-compressed", ConfigTools.CONFIG.DRIVE_FOLDER_ID) != null) {
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

        ServerTools.undoBackupPreparation();
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

        if(inactivityReminder > 0 && daysSinceActivity % inactivityReminder == 0) {
            MessageManager.sendText(String.format("Reminder: The server hasn't been used in %s days. Consider stopping it.", daysSinceActivity), MessageOrigin.SCHEDULE);
            MessageManager.log(String.format("Inactivity reminder sent after %s days.", daysSinceActivity), LogLevel.INFO);
        }
    }

    private static void checkForCrash() {
        if(!isCrashCheckEnabled) return;

        new Thread(PermanentOperations::executeCrashHandler).start();
    }

    private static void executeCrashHandler() {
        boolean running = ServerTools.isServerRunning();
        if(!running && prevRunning) {
            ConnectionManager.closeConnection(true, true);
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
                    MiscTools.timeout(500);
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
