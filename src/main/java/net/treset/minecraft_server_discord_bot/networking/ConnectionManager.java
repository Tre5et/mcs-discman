package net.treset.minecraft_server_discord_bot.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionManager {
    private static int port = 876;
    private static String sessionId;

    private static ServerSocket ss;
    private static Socket s;
    private static BufferedReader clientReader;
    private static PrintStream clientSender;

    public static int getPort() { return port; }
    public static boolean setPort(int newPort) { port = newPort; return true; }

    public static String getSessionId() { return sessionId; }
    public static boolean setSessionId(String id) { sessionId = id; return true; }

    public static BufferedReader getClientReader() { return clientReader; }
    public static PrintStream getClientSender() { return clientSender; }

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
            clientSender = new PrintStream(s.getOutputStream());
        } catch (IOException e) {
            return false;
        }

        String initMsg;
        try {
            initMsg = clientReader.readLine();
        } catch (IOException e) {
            return false;
        }

        if (!initMsg.startsWith("sid/")) return false;
        sessionId = initMsg.substring(4);

        CommunicationManager.sendToClient("sid/" + sessionId);

        System.out.println("Conn est");
        return true;
    }

    private static boolean closeAccepted = false;
    public static void acceptClose() { closeAccepted = true; }

    public static boolean closeConnection(boolean force) {
        if(sessionId == null) return true;

        boolean success = true;

        if(CommunicationManager.sendToClient("cls/" + sessionId)) {
            try {
                Thread.sleep(10);
            } catch(InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(!closeAccepted && !force) return false;
            closeAccepted = false;

            try {
                CommunicationManager.requestCloseReader();
                clientSender.close();
                clientSender.close();
                s.close();
            } catch (IOException e) {
                return false;
            }

            clientSender = null;
            clientReader = null;
            sessionId = null;
        }
        else success = false;

        System.out.println(success ? "Connection close" : "Connection close unsuccessfull");
        return success;
    }

    public static boolean respondToClosingConnection(String sid) {
        if(!sid.equals(sessionId)) {
            CommunicationManager.sendToClient("dcl/" + sessionId);
            return false;
        }

        boolean success = true;
        if(CommunicationManager.sendToClient("acl/" + sessionId)) {
            System.out.println("Connection close accepted");
            try {
                clientReader.close();
                clientSender.close();
            } catch (IOException e) {
                success = false;
            }

            clientReader = null;
            clientSender = null;

            try {
                s.close();
            } catch (IOException e) {
                success = false;
            }

            sessionId = null;
        } else success = false;

        System.out.println(success ? "Connection closed" : "Connection closed unsuccessfully");

        return success;
    }

}
