package net.treset.minecraft_server_discord_bot.tools;

import net.treset.minecraft_server_discord_bot.PermanentOperations;
import net.treset.minecraft_server_discord_bot.networking.ConnectionManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerTools {
    public static boolean isServerRunning() {
        String activeStr = ExecTools.executeCommand(ConfigTools.CONFIG.ACTIVE_COMMAND);
        final Pattern pattern = Pattern.compile(ConfigTools.CONFIG.ACTIVE_KEY);
        final Matcher matcher = pattern.matcher(activeStr);
        return matcher.find();
    }

    public static String runServerCommand(String command) {
        String consoleCmd = String.format(ConfigTools.CONFIG.RUN_COMMAND, command);
        return ExecTools.executeCommand(consoleCmd);
    }

    public static void startServer() {
        ConnectionManager.closeConnection(true);
        ConnectionManager.establishConnection();
        String cmd = ConfigTools.CONFIG.START_COMMAND;
        ExecTools.executeCommand(cmd);
    }

    public static void stopServer() {
        PermanentOperations.isStopExpected = true;
        String cmd = "stop";
        ServerTools.runServerCommand(cmd);
    }

    public static void prepareServerForBackup() {
        String cmd = "save-off";
        ServerTools.runServerCommand(cmd);
        cmd = "save-all";
        ServerTools.runServerCommand(cmd);
    }

    public static void undoBackupPreparation() {
        String cmd = "save-on";
        ServerTools.runServerCommand(cmd);
    }
}
