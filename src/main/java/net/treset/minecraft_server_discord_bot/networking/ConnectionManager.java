package net.treset.minecraft_server_discord_bot.networking;

import net.treset.minecraft_server_discord_bot.messaging.LogLevel;
import net.treset.minecraft_server_discord_bot.messaging.MessageManager;
import net.treset.minecraft_server_discord_bot.tools.ConfigTools;

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

    public static Thread connectionThread;

    public static int getPort() { return port; }
    public static boolean setPort(int newPort) { port = newPort; return true; }

    public static String getSessionId() { return sessionId; }
    public static boolean setSessionId(String id) { sessionId = id; return true; }

    public static BufferedReader getClientReader() { return clientReader; }
    public static PrintStream getClientSender() { return clientSender; }

    private static boolean connected = false;
    private static boolean waitingForConnection = false;
    public static boolean isConnected() { return connected; }
    public static boolean isWaitingForConnection() { return waitingForConnection; }

    public static boolean openConnection() {
        if(!ConfigTools.CLIENT_CONFIG.CLIENT_ENABLED) return true;
        if(connected || waitingForConnection || (connectionThread != null && !connectionThread.isInterrupted())) {
            MessageManager.log(String.format("Not opening connection. Connection to %s is already open.", sessionId), LogLevel.WARN);
            return false;
        }
        connectionThread = new Thread(ConnectionManager::openConnectionThread);
        connectionThread.start();
        return true;
    }

    //waits for client to connect, only use async
    private static boolean openConnectionThread() {
        MessageManager.log("Opening connection.", LogLevel.INFO);

        port = ConfigTools.CLIENT_CONFIG.PORT;

        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            MessageManager.log(String.format("Unable to create socket at port %s. -> Stacktrace.", port), LogLevel.ERROR);
            e.printStackTrace();
            return false;
        }

        try {
            waitingForConnection = true;
            s = ss.accept();
            waitingForConnection = false;
            clientReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            clientSender = new PrintStream(s.getOutputStream());
        } catch (IOException e) {
            MessageManager.log("Unable to open socket. -> Stacktrace.", LogLevel.ERROR);
            e.printStackTrace();
            waitingForConnection = false;
            return false;
        }

        String initMsg;
        try {
            initMsg = clientReader.readLine();
        } catch (IOException e) {
            MessageManager.log("Unable to receive session id from client. -> Stacktrace.", LogLevel.ERROR);
            e.printStackTrace();
            return false;
        }

        if (!initMsg.startsWith("sid/")) {
            MessageManager.log(String.format("Unable to establish session id with client. Forcefully closing connection. Initial message wasn't a session id: \"%s\". -> Stacktrace.", initMsg), LogLevel.ERROR);
            sessionId = "-1";
            closeConnection(true, false);
            return false;
        }
        sessionId = initMsg.substring(4);

        CommunicationManager.sendToClient("sid/" + sessionId);

        MessageManager.log(String.format("Opened connection to client %s.", sessionId), LogLevel.INFO);
        connected = true;
        CommunicationManager.updateReader();

        return true;
    }

    private static boolean closeAccepted = false;
    public static void acceptClose() { closeAccepted = true; }

    public static boolean closeConnection(boolean force, boolean reopen) {
        if(!waitingForConnection && !connected) return true;
        if(waitingForConnection) {
            if(reopen) return true;

            try {
                ss.close();
            } catch (IOException e) {
                MessageManager.log("Error closing server socket. -> Stacktrace", LogLevel.ERROR);
                e.printStackTrace();
                return false;
            }

            try {
                connectionThread.interrupt();
            } catch (SecurityException e) {
                MessageManager.log("Unable to terminate connection thread. -> Stacktrace", LogLevel.ERROR);
                e.printStackTrace();
            }

            waitingForConnection = false;
            return true;
        }

        if(closeAccepted) {
            MessageManager.log("Found illegal close acceptance state when closing. Closing anyway.", LogLevel.WARN);

            closeAccepted = false;
        }
        MessageManager.log(String.format("Closing connection to client %s.", sessionId), LogLevel.INFO);

        if(CommunicationManager.sendToClient("cls/" + sessionId)) {
            try {
                Thread.sleep(50);
            } catch(InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(!closeAccepted) {
                if(!force) {
                    MessageManager.log(String.format("Error closing connection. Close not accepted by client %s.", sessionId), LogLevel.ERROR);
                    return false;
                } else {
                    MessageManager.log(String.format("Forcing close. Close not accepted by client %s.", sessionId), LogLevel.WARN);
                }
            }
            closeAccepted = false;

            try {
                clientSender.close();
                clientSender.close();
                s.close();
                connectionThread.interrupt();
            } catch (IOException e) {
                MessageManager.log("Error closing connection. Unable to close socket. -> Stacktrace.", LogLevel.ERROR);
                e.printStackTrace();
                return false;
            }

            try {
                ss.close();
            } catch (IOException e) {
                MessageManager.log("Error closing server socket. -> Stacktrace", LogLevel.ERROR);
                e.printStackTrace();
                return false;
            }

            try {
                connectionThread.interrupt();
            } catch (SecurityException e) {
                MessageManager.log("Unable to terminate connection thread. -> Stacktrace", LogLevel.ERROR);
                e.printStackTrace();
            }

            MessageManager.log(String.format("Closed connection to client %s.", sessionId), LogLevel.INFO);

            clientSender = null;
            clientReader = null;
            sessionId = null;

            connected = false;

            CommunicationManager.updateReader();

            if(reopen) openConnection();

            return true;
        }
        MessageManager.log(String.format("Error closing connection. Unable to send close request to client %s.", sessionId), LogLevel.ERROR);
        return false;
    }

    public static boolean respondToClosingConnection(String sid, boolean reopen) {
        MessageManager.log(String.format("Received close request from client %s.", sid), LogLevel.INFO);

        if(!sid.equals(sessionId)) {
            CommunicationManager.sendToClient("dcl/" + sessionId);
            MessageManager.log(String.format("Rejected close request from client %s. Client session doesn't match open session %s.", sid, sessionId), LogLevel.WARN);
            return false;
        }

        boolean success = true;
        if(CommunicationManager.sendToClient("acl/" + sessionId)) {
            MessageManager.log(String.format("Accepted connection close from client %s.", sessionId), LogLevel.INFO);
            try {
                clientReader.close();
                clientSender.close();
            } catch (IOException e) {
                MessageManager.log(String.format("Error handling connection close from client %s. Unable to close io. -> Stacktrace.", sessionId), LogLevel.ERROR);
                e.printStackTrace();
                success = false;
            }

            clientReader = null;
            clientSender = null;

            try {
                s.close();
            } catch (IOException e) {
                MessageManager.log(String.format("Error handling connection close from client %s. Unable to close socket. -> Stacktrace.", sessionId), LogLevel.ERROR);
                e.printStackTrace();
                success = false;
            }

            try {
                ss.close();
            } catch (IOException e) {
                MessageManager.log("Error closing server socket. -> Stacktrace", LogLevel.ERROR);
                e.printStackTrace();
                return false;
            }

            try {
                connectionThread.interrupt();
            } catch (SecurityException e) {
                MessageManager.log("Unable to terminate connection thread. -> Stacktrace", LogLevel.ERROR);
                e.printStackTrace();
            }

            if(success) {
                MessageManager.log(String.format("Closed connection after request from client %s. ", sessionId), LogLevel.INFO);
            }

            sessionId = null;

            connected = false;

            CommunicationManager.updateReader();

            if(reopen) openConnection();

            return success;
        }
        MessageManager.log(String.format("Error handling connection close from client %s. Unable to send accept message to client.", sessionId), LogLevel.ERROR);

        return false;
    }

}
