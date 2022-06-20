package net.treset.minecraft_server_discord_bot.networking;

import org.checkerframework.checker.units.qual.C;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Objects;

public class DataReciever {

    //continuous code, only run async
    public static boolean printoutData() {
        BufferedReader br = ConnectionManager.getClientReader();
        if(br == null) return false;

        String msg;
        while(true) {
            try {
                msg = br.readLine();
            } catch (IOException e) {
                return false;
            }

            if(msg == null) continue;

            if(msg.equals("dc/" + ConnectionManager.getSessionId())) break;

            System.out.println(msg);
        }

        return ConnectionManager.closeConnection();
    }
}
