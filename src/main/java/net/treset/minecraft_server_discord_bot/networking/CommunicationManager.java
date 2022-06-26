package net.treset.minecraft_server_discord_bot.networking;

import net.treset.minecraft_server_discord_bot.DiscordBot;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.messaging.MessageOrigin;

import java.io.BufferedReader;
import java.io.IOException;

public class CommunicationManager {

    private static boolean closeReader = false;

    //continuous code, only run async
    public static boolean handleData() {
        BufferedReader br = ConnectionManager.getClientReader();
        if(br == null) {
            DiscordBot.LOGGER.warn("CommunicationManager: Not starting data handling: No client reader active.");
            return false;
        }

        String msg;

        while(!closeReader) {
            try {
                msg = br.readLine();
            } catch (IOException e) {
                DiscordBot.LOGGER.error("CommunicationManager: Error reading line from client. Stacktrace:");
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
                default -> System.out.println(msg);

            }
        }
        closeReader = false;

        return true;
    }

    public static boolean requestCloseReader() { closeReader = true; return true; }

    public static boolean sendToClient(String message) {
        if(ConnectionManager.getClientSender() == null) return false;

        ConnectionManager.getClientSender().println(message + "\n");
        return true;
    }
}
