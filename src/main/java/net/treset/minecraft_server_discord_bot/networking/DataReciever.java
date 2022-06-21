package net.treset.minecraft_server_discord_bot.networking;

import java.io.BufferedReader;
import java.io.IOException;

public class DataReciever {

    //continuous code, only run async
    public static boolean handleData() {
        BufferedReader br = ConnectionManager.getClientReader();
        if(br == null) return false;

        String msg;

        boolean cancel = false;
        while(!cancel) {
            try {
                msg = br.readLine();
            } catch (IOException e) {
                return false;
            }

            if(msg == null) continue;

            switch(msg.substring(0, 3)) {
                case "dcn" -> {
                    if(msg.substring(4).equals(ConnectionManager.getSessionId())) {
                        cancel = true;
                    }
                }
                case "txt" -> printText(msg.substring(4));
                default -> System.out.println(msg);

            }


        }

        return ConnectionManager.closeConnection();
    }

    private static void printText(String text) {
        System.out.println(text);
    }
}
