package net.treset.minecraft_server_discord_bot;

import net.treset.minecraft_server_discord_bot.tools.*;

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

    public static void permanentLoop() {
        isLoggingEnabled = ConfigTools.PERMA_CONFIG.isLoggingEnabled;
        isStartLoggingEnabled = ConfigTools.PERMA_CONFIG.isStartLoggingEnabled;
        isJoinLoggingEnabled = ConfigTools.PERMA_CONFIG.isJoinLoggingEnabled;
        isLoggingFull = ConfigTools.PERMA_CONFIG.isLoggingFull;
        isBackupEnabled = ConfigTools.PERMA_CONFIG.isBackupEnabled;
        backupHour = ConfigTools.PERMA_CONFIG.backupHour;
        isCrashCheckEnabled = ConfigTools.PERMA_CONFIG.isCrashCheckEnabled;
        isLogNoBackup = ConfigTools.PERMA_CONFIG.isLogNoBackup;
        inactivityReminder = ConfigTools.PERMA_CONFIG.inactivityReminder;

        prevData = FileTools.readFile(ConfigTools.CONFIG.LOG_PATH);

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
            String data = FileTools.readFile(ConfigTools.CONFIG.LOG_PATH);
            String formattedData = data.replace(prevData, "");

            if (!formattedData.isEmpty()) {
                if (isLoggingFull) logFullConsole(formattedData);
                else {
                    String[] lines = formattedData.split("\\r?\\n");

                    for (String line : lines) {
                        if(isStartLoggingEnabled) logStarted(line);
                        if(isJoinLoggingEnabled) {
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
        DiscordBot.BOT_CHANNEL.sendMessage(input).queue();
        DiscordBot.LOGGER.info("PermanentLogger logged: full console");
        hasSomethingHappened = true;
    }

    private static void logStarted(String input) {
        if (input.contains("Done (")) {
            DiscordBot.BOT_CHANNEL.sendMessage("Server started.").queue();
            DiscordBot.LOGGER.info("PermanentLogger logged: server started");
            hasSomethingHappened = true;
        }
    }

    private static void logPlayerJoin(String input) {
        String playerName = FormatTools.findStringBetween(input, "INFO]: ", " joined the game");
        if(!playerName.equals("")) {
            DiscordBot.BOT_CHANNEL.sendMessage(String.format("%s joined the game.", playerName)).queue();
            ConfigTools.addPlayer(playerName);
            DiscordBot.LOGGER.info("PermanentLogger logged: " + playerName + " joined the game");
            hasSomethingHappened = true;
        }
    }

    private static void logPlayerLeave(String input) {
        String playerName = FormatTools.findStringBetween(input, "INFO]: ", " left the game");
        if(!playerName.equals("")) {
            DiscordBot.BOT_CHANNEL.sendMessage(String.format("%s left the game.", playerName)).queue();
            ConfigTools.removePlayer(playerName);
            DiscordBot.LOGGER.info("PermanentLogger logged: " + playerName + " left the game");
            hasSomethingHappened = true;
        }
    }

    private static void autoBackup() {
        //auto-backup
        DateTimeFormatter dtfH = DateTimeFormatter.ofPattern("HH");

        LocalDateTime now = LocalDateTime.now();
        boolean isCorrectHour = dtfH.format(now).equals(backupHour);

        if(!wasBackedUpToday && isCorrectHour) {
            if(hasSomethingHappened) {
                daysSinceActivity = 0;
                if(isBackupEnabled) createAutoBackup();
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
        String output = "";
        if (ServerTools.isServerRunning()) ServerTools.prepareServerForBackup();

        output = "Creating auto-backup.";
        DiscordBot.BOT_CHANNEL.sendMessage(output).queue();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);
        if (FileTools.zipFile(ConfigTools.CONFIG.WORLD_PATH, ConfigTools.CONFIG.BACKUPS_PATH + date + "-auto.zip")) {
            if(DriveTools.uploadFile(ConfigTools.CONFIG.BACKUPS_PATH + date + "-auto.zip", date + "-auto.zip", "application/x-zip-compressed", ConfigTools.CONFIG.DRIVE_FOLDER_ID) != null) {
                output = "Created auto-backup successfully.";
                DiscordBot.LOGGER.info("CreateBackup Command handled: success");
            } else {
                output = "Created local auto-backup successfully.";
                DiscordBot.LOGGER.warn("CreateBackup Command handled: failed to upload file");
            }
        } else {
            output = "Failed to create auto-backup.";
            DiscordBot.LOGGER.error("AutoBackup FAILED: failed to create file");
        }
        DiscordBot.BOT_CHANNEL.sendMessage(output).queue();

        String cmd = "save-on";
        ServerTools.runServerCommand(cmd);
    }

    private static void dontCreateAutoBackup(boolean log) {
        if(log) {
            String output = "Not creating a auto-backup because nothing happened today.";
            DiscordBot.BOT_CHANNEL.sendMessage(output).queue();
        }
        DiscordBot.LOGGER.info("AutoBackup handled: not necessary");
    }

    private static void changeDay() {
        DateTimeFormatter dtfD = DateTimeFormatter.ofPattern("dd");
        wasBackedUpToday = false;
        prevDay = dtfD.format(LocalDateTime.now());
        DiscordBot.LOGGER.info("AutoBackup handling: day changed");
    }

    private static void logInactivity() {
        daysSinceActivity++;

        if(inactivityReminder > 0 && daysSinceActivity % inactivityReminder == 0) {
            DiscordBot.BOT_CHANNEL.sendMessage(String.format("Reminder: The server hasn't been used in %s days. Consider stopping it.", daysSinceActivity)).queue();
            DiscordBot.LOGGER.info(String.format("Inactivity Reminder: Reminded after %s days.", daysSinceActivity));
        }
    }

    private static void checkForCrash() {
        if(!isCrashCheckEnabled) return;

        boolean running = ServerTools.isServerRunning();
        if(!running && prevRunning) {
            prevRunning = false;
            if(isStopExpected) {
                DiscordBot.LOGGER.info("Crash Check detected: Server stopped but was expected to");
                isStopExpected = false;
            } else if(crashesInShortTime >= 5) {
                DiscordBot.BOT_CHANNEL.sendMessage("Server stopped unexpectedly, has crashed too often in a short time, not attempting to restart.").queue();
                DiscordBot.LOGGER.warn("Crash Check detected: Server stopped unexpectedly, crashed to often, not attempting restart");
            } else {
                DiscordBot.BOT_CHANNEL.sendMessage("Server stopped unexpectedly, attempting to restart...").queue();
                DiscordBot.LOGGER.warn("Crash Check detected: Server stopped unexpectedly, restarting");

                crashedRecently = 60; //600 sec * 0.1 loops per second
                crashesInShortTime++;

                ServerTools.startServer();

                float time = 0;
                while (!ServerTools.isServerRunning()) {
                    MiscTools.timeout(500);
                    time += .2f;
                    if (time >= 30) {
                        DiscordBot.BOT_CHANNEL.sendMessage("Failed to start Server, not trying again.").queue();
                        DiscordBot.LOGGER.error("Crash Check detected: Failed to initialize restart after server crash");
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
