package net.treset.minecraft_server_discord_bot.networking;

import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.messaging.MessageOrigin;
import net.treset.minecraft_server_discord_bot.tools.ConfigTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class CommunicationManager {

    private static boolean closeReader = false;

    private static BufferedReader br;

    public static void updateReader() {
        br = ConnectionManager.getClientReader();
        MessageManager.log("Updated Reader.", LogLevel.INFO);
    }

    //continuous code, only run async
    public static boolean handleData() {
        updateReader();

        MessageManager.log("Opened reader.", LogLevel.INFO);

        String msg;

        while(!closeReader) {
            if(br == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }

            try {
                msg = br.readLine();
            } catch (IOException e) {
                MessageManager.log("Error reading line. -> Stacktrace.", LogLevel.ERROR);
                e.printStackTrace();
                continue;
            }

            if(msg == null) continue;

            switch(msg.substring(0, 3)) {
                case "joi" -> MessageManager.sendJoin(msg.substring(4), MessageOrigin.CLIENT);
                case "lev" -> MessageManager.sendLeave(msg.substring(4), MessageOrigin.CLIENT);
                case "dth" -> MessageManager.sendDeath(msg.substring(4), MessageOrigin.CLIENT);
                case "txt" -> MessageManager.sendText(msg.substring(4), MessageOrigin.CLIENT);
                case "cls" -> ConnectionManager.respondToClosingConnection(msg.substring(4), false);
                case "acl" -> ConnectionManager.acceptClose();
                case "tim" -> receiveTime(msg.substring(4));
                case "ply" -> receivePlayers(msg.substring(4));
                default -> System.out.println(msg);

            }
        }
        MessageManager.log("Closed reader.", LogLevel.INFO);

        closeReader = false;

        return true;
    }

    private static Integer time = -1;
    public static int getTime() {
        if(!ConnectionManager.isConnected()) {
            return -2;
        }

        time = -1;
        sendToClient("tim/ig_now");

        for(int i = 0; i < 300; i++) {
            if(time == null) {
                MessageManager.log("Error getting time. Received invalid time.", LogLevel.WARN);
                return -1;
            } else if(Objects.requireNonNull(time) != -1) {
                return time;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        MessageManager.log("Error getting time. Timed out.", LogLevel.WARN);
        return -1;
    }

    private static void receiveTime(String timeStr) {
        time = Integer.parseInt(timeStr);
    }

    public static void tryUpdatePlayers() {
        String[] players = getPlayers();

        if(Arrays.equals(players, new String[]{"-2"})) return;
        if(Arrays.equals(players, new String[]{"-1"})) {
            MessageManager.log("Error updating players. Undefined.", LogLevel.WARN);
            return;
        }

        ConfigTools.setPlayers(players);
        MessageManager.log("Updated players.", LogLevel.INFO);
    }

    private static String[] players;
    public static String[] getPlayers() {
        if(!ConnectionManager.isConnected()) return new String[]{"-2"};

        players = new String[]{"-1"};
        sendToClient("ply/curr");

        for(int i = 0; i < 300; i++) {
            if(players == null) {
                MessageManager.log("Error getting players. Received invalid list.", LogLevel.WARN);
                return new String[]{"-1"};
            } else if(!Arrays.equals(Objects.requireNonNull(players), new String[]{"-1"})) {
                return players;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        MessageManager.log("Error getting players. Timed out.", LogLevel.WARN);
        return new String[]{"-1"};
    }

    private static void receivePlayers(String newPlayers) {

        players = newPlayers.split(";");
    }

    public static boolean requestCloseReader() { closeReader = true; return true; }

    public static boolean sendToClient(String message) {
        if(ConnectionManager.getClientSender() == null) return false;

        ConnectionManager.getClientSender().println(message + "\n");
        return true;
    }
}
