package net.treset.minecraft_server_discord_bot.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Objects;

public class DataReciever {
    public static void init() throws IOException {
        ServerSocket ss = new ServerSocket(856);
        System.out.println("Created Socket");

        establishConnection(ss);
    }

    public static void establishConnection(ServerSocket ss) throws IOException {
        System.out.println("Establishing connection");
        Socket s = ss.accept();
        System.out.println("Connection established");

        BufferedReader br
                = new BufferedReader(
                new InputStreamReader(
                        s.getInputStream()));

        new Thread(() -> {
            try {
                printoutData(ss, s, br);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static void destroyConnection(Socket s) throws IOException {
        s.close();
        System.out.println("Connection destroyed");
    }

    public static void printoutData(ServerSocket ss, Socket s, BufferedReader br) throws IOException {
        // server executes continuously
        while (true) {

            String str;

            // read from client
            while (!Objects.equals(str = br.readLine(), "dc")) {
                if(str == null) continue;
                System.out.println(str);
            }

            destroyConnection(s);
            establishConnection(ss);
        }
    }
}
