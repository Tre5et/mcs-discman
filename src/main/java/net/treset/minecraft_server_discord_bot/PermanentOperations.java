package net.treset.minecraft_server_discord_bot;

import net.treset.minecraft_server_discord_bot.config.Config;
import net.treset.minecraft_server_discord_bot.discord.DiscordBot;
import net.treset.minecraft_server_discord_bot.logging.Logger;
import net.treset.minecraft_server_discord_bot.discord.MessageOrigin;
import net.treset.minecraft_server_discord_bot.server.ManagementClient;
import net.treset.minecraft_server_discord_bot.server.ServerActions;

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
            checkForCrash();

            try {
                Thread.sleep(Config.get().discord.updateInterval * 1000L);
            } catch (InterruptedException e) {
                Logger.error(e, "Failed to wait for permanent operations loop");
            }
        }

        terminatePermanentLoop = false;
    }

    private static void checkForCrash() {
        if(Config.get().crash == null) return;

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

                ServerActions.startServerAsync();

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
