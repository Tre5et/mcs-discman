package net.treset.minecraft_server_discord_bot.networking;

import net.treset.minecraft_server_discord_bot.DiscordBot;

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

    private static boolean connected = false;
    public static boolean isConnected() { return connected; }

    public static boolean init() {
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            DiscordBot.LOGGER.error("ConnectionManager: Unable to create socket at port " + port + ". Stacktrace:");
            e.printStackTrace();
            return false;
        }

        DiscordBot.LOGGER.info("ConnectionManager: Connection initialized.");
        return true;
    }

    //waits for client to connect, only use async
    public static boolean openConnection() {
        if(connected && sessionId == null) {
            DiscordBot.LOGGER.warn("ConnectionManager: Not opening connection because connection to " + sessionId + " is already opened");
        }
        DiscordBot.LOGGER.info("ConnectionManager: Opening connection");
        try {
            s = ss.accept();
            clientReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            clientSender = new PrintStream(s.getOutputStream());
        } catch (IOException e) {
            DiscordBot.LOGGER.error("ConnectionManager: Unable to open socket. Stacktrace:");
            e.printStackTrace();
            return false;
        }

        String initMsg;
        try {
            initMsg = clientReader.readLine();
        } catch (IOException e) {
            DiscordBot.LOGGER.error("ConnectionManager: Unable to receive session id from client. Stacktrace:");
            e.printStackTrace();
            return false;
        }

        if (!initMsg.startsWith("sid/")) {
            DiscordBot.LOGGER.error("ConnectionManager: Unable to establish session id with client. Forcefully closing connection. Initial message wasn't a session id: \"" + initMsg + "\"");
            sessionId = "-1";
            closeConnection(true);
            return false;
        }
        sessionId = initMsg.substring(4);

        CommunicationManager.sendToClient("sid/" + sessionId);

        DiscordBot.LOGGER.info("ConnectionManager: Established connection to client " + sessionId);
        connected = true;
        return true;
    }

    private static boolean closeAccepted = false;
    public static void acceptClose() { closeAccepted = true; }

    public static boolean closeConnection(boolean force) {
        if(sessionId == null || !connected) return true;

        if(closeAccepted) {
            DiscordBot.LOGGER.warn("ConnectionManager: Found illegal close acceptance state when closing. Closing anyway.");
            closeAccepted = false;
        }

        DiscordBot.LOGGER.info("ConnectionManager: Closing connection to client " + sessionId);
        if(CommunicationManager.sendToClient("cls/" + sessionId)) {
            try {
                Thread.sleep(10);
            } catch(InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(!closeAccepted) {
                if(!force) {
                    DiscordBot.LOGGER.error("ConnectionManager: Error closing connection: Close not accepted by client " + sessionId);
                    return false;
                } else {
                    DiscordBot.LOGGER.warn("ConnectionManager: Forcing close: Close not accepted by client " + sessionId);
                }
            }
            closeAccepted = false;

            try {
                CommunicationManager.requestCloseReader();
                clientSender.close();
                clientSender.close();
                s.close();
            } catch (IOException e) {
                DiscordBot.LOGGER.error("ConnectionManager: Error closing connection: Unable to close socket. Stacktrace:");
                e.printStackTrace();
                return false;
            }

            DiscordBot.LOGGER.info("ConnectionManager: Closed connection to client " + sessionId);

            clientSender = null;
            clientReader = null;
            sessionId = null;

            connected = false;

            return true;
        }
        DiscordBot.LOGGER.error("ConnectionManager: Error closing connection: Unable to send close request to client " + sessionId);
        return false;
    }

    public static boolean respondToClosingConnection(String sid) {
        DiscordBot.LOGGER.info("ConnectionManager: Received connection close request from client " + sid);

        if(!sid.equals(sessionId)) {
            CommunicationManager.sendToClient("dcl/" + sessionId);
            DiscordBot.LOGGER.warn("ConnectionManager: Rejected close request form client " + sid + ". Client session doesn't match current connection " + sessionId);
            return false;
        }

        boolean success = true;
        if(CommunicationManager.sendToClient("acl/" + sessionId)) {
            DiscordBot.LOGGER.info("ConnectionManager: Accepted connection close from client " + sessionId);
            try {
                clientReader.close();
                clientSender.close();
            } catch (IOException e) {
                DiscordBot.LOGGER.error("ConnectionManager: Error handling connection close request from client " + sessionId + ": Unable to close io. Stacktrace:");
                e.printStackTrace();
                success = false;
            }

            clientReader = null;
            clientSender = null;

            try {
                s.close();
            } catch (IOException e) {
                DiscordBot.LOGGER.error("ConnectionManager: Error handling connection close request from client " + sessionId + ": Unable to close socket. Stacktrace:");
                e.printStackTrace();
                success = false;
            }

            if(success) {
                DiscordBot.LOGGER.info("ConnectionManager: Closed connection after close request from client " + sessionId);
            }

            sessionId = null;

            connected = true;

            return success;
        }
        DiscordBot.LOGGER.error("ConnectionManager: Error handling connection close request from client " + sessionId + ": Unable to send accept message to client.");

        return false;
    }

}
