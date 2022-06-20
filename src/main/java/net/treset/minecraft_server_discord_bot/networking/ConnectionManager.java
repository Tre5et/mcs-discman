package net.treset.minecraft_server_discord_bot.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionManager {
    private static int port = 876;
    private static String sessionId;

    private static ServerSocket ss;
    private static Socket s;
    private static BufferedReader clientReader;

    public static int getPort() { return port; }
    public static boolean setPort(int newPort) { port = newPort; return true; }

    public static String getSessionId() { return sessionId; }
    public static boolean setSessionId(String id) { sessionId = id; return true; }

    public static BufferedReader getClientReader() { return clientReader; }

    public static boolean init() {
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            return false;
        }

        System.out.println("Conn init");
        return true;
    }

    //waits for client to connect, only use async
    public static boolean establishConnection() {
        try {
            s = ss.accept();
            clientReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
            return false;
        }

        String initMsg;
        try {
            initMsg = clientReader.readLine();
        } catch (IOException e) {
            return false;
        }

        if(!initMsg.startsWith("sid/")) return false;
        sessionId = initMsg.substring(4);

        System.out.println("Conn est");
        return true;
    }

    public static boolean closeConnection() {
        try {
            clientReader.close();
        } catch (IOException e) {
            return false;
        }

        clientReader = null;

        try {
            s.close();
        } catch (IOException e) {
            return false;
        }

        sessionId = null;

        System.out.println("Conn cls");
        return true;
    }

}
