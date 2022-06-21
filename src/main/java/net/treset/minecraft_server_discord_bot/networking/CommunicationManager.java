package net.treset.minecraft_server_discord_bot.networking;

import java.io.BufferedReader;
import java.io.IOException;

public class CommunicationManager {

    private static boolean closeReader = false;

    //continuous code, only run async
    public static boolean handleData() {
        BufferedReader br = ConnectionManager.getClientReader();
        if(br == null) return false;

        String msg;

        while(!closeReader) {
            try {
                msg = br.readLine();
            } catch (IOException e) {
                return false;
            }

            if(msg == null) continue;

            switch(msg.substring(0, 3)) {
                case "cls" -> ConnectionManager.respondToClosingConnection(msg.substring(4));
                case "txt" -> printText(msg.substring(4));
                case "acl" -> ConnectionManager.acceptClose();
                default -> System.out.println(msg);

            }
        }
        closeReader = false;

        return true;
    }

    public static boolean requestCloseReader() { closeReader = true; return true; }

    private static void printText(String text) {
        System.out.println(text);
    }

    public static boolean sendToClient(String message) {
        ConnectionManager.getClientSender().println(message + "\n");
        return true;
    }
}
