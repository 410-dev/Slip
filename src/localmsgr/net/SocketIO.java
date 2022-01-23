package localmsgr.net;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;

import java.net.ServerSocket;
import java.net.Socket;

import localmsgr.Config;
import localmsgr.StaticAgent;
import localmsgr.SystemLogger;
import localmsgr.data.MessageData;

public class SocketIO {

    public static Thread messageInbound;
    public static Socket recvSocket;
    public static ServerSocket serverSocket;
    
    public static BufferedReader in;
    public static PrintWriter out;

    public static void listenerStart() {
        buildMessageInboundSocket();
        messageInbound.start();
    }

    public static void buildMessageInboundSocket() {
        messageInbound = new Thread() {
            public void run() {
                try {
                    serverSocket = new ServerSocket(Config.recvPort);
                    SystemLogger.log("Listening on port " + Config.recvPort + "...");
                    while (true) {
                        try {
                            Socket socket = serverSocket.accept();
                            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String inboundMessage = in.readLine();
                            StaticAgent.onMessageRecv(inboundMessage);
                            socket.close();
                            
                        }catch(Exception e) {
                            SystemLogger.error("Error while listening on port " + Config.recvPort + ".", false, SystemLogger.EXIT, e);
                        }
                    }
                }catch(Exception e){
                    if (e.toString().contains("Address already in use")) {
                        SystemLogger.error("Port " + Config.recvPort + " is already in use. Please close the program using such port and try again.", true, SystemLogger.EXIT, e);
                }
            }
        };
    }

    public static boolean sendData(String ip, int port, String data) {
        try {
            
            Socket socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(data);

            socket.close();

            return true;
        } catch (Exception e) {
            SystemLogger.error(e.getMessage(), false, SystemLogger.CONTINUE, e);
            return false;
        }
    }

    public static boolean requestDeviceInformation(String ip) {
        SystemLogger.log("Requesting device information for " + ip + "...");
        try {
            return sendData(ip, Config.recvPort, MessageData.deviceInformationRequestBuilder());
        }catch(Exception e) {
            if (!e.toString().contains("Connection refused")) e.printStackTrace();
            SystemLogger.log("Host " + ip + " refused to connect.");
            return false;
        }
    }

    public static boolean pingPort(String ip, int port) {
        Socket socket;
        try {
            socket = new Socket(ip, port);
            socket.close();
            SystemLogger.log("Port " + port + " for " + ip + " is open.");
            return true;
        }catch(Exception e) {
            return false;
        }
    }
}
