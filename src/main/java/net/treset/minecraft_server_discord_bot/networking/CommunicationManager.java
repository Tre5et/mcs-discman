package net.treset.minecraft_server_discord_bot.networking;

import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.messaging.MessageOrigin;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

public class CommunicationManager {

    private static boolean closeReader = false;

    //continuous code, only run async
    public static boolean handleData() {
        BufferedReader br = ConnectionManager.getClientReader();
        if(br == null) {
            MessageManager.log("Not starting data handling. No client reader.", LogLevel.WARN);
            return false;
        }

        String msg;

        while(!closeReader) {
            try {
                msg = br.readLine();
            } catch (IOException e) {
                MessageManager.log("Error reading line. -> Stacktrace.", LogLevel.ERROR);
                e.printStackTrace();
                return false;
            }

            if(msg == null) continue;

            switch(msg.substring(0, 3)) {
                case "joi" -> MessageManager.sendJoin(msg.substring(4), MessageOrigin.CLIENT);
                case "lev" -> MessageManager.sendLeave(msg.substring(4), MessageOrigin.CLIENT);
                case "dth" -> MessageManager.sendDeath(msg.substring(4), MessageOrigin.CLIENT);
                case "txt" -> MessageManager.sendText(msg.substring(4), MessageOrigin.CLIENT);
                case "cls" -> ConnectionManager.respondToClosingConnection(msg.substring(4));
                case "acl" -> ConnectionManager.acceptClose();
                case "tim" -> receiveTime(msg.substring(4));
                default -> System.out.println(msg);

            }
        }
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

    public static boolean requestCloseReader() { closeReader = true; return true; }

    public static boolean sendToClient(String message) {
        if(ConnectionManager.getClientSender() == null) return false;

        ConnectionManager.getClientSender().println(message + "\n");
        return true;
    }
}
