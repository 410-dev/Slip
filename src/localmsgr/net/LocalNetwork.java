package localmsgr.net;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import localmsgr.Config;
import localmsgr.StaticAgent;
import localmsgr.SystemLogger;
import localmsgr.data.MessageData;

public class LocalNetwork {

    public static String networkPrefix = "";

    public static String getNetworkAddress() {
        try {

            // Get the local host IP address for the right device
            String ip = "";
            try(final DatagramSocket socket = new DatagramSocket()){
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                ip = socket.getLocalAddress().getHostAddress();
            }

            return ip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Scan the port with the specific port open.
    // After scan, send the device information request.
    // If the device information request is received, add to talkable list.
    public static void scanPortOpenDevices() {
        String[] ipComponents = Config.myIP.split("\\.");
        String localNetworkIP = ipComponents[0] + "." + ipComponents[1] + "." + ipComponents[2] + ".";
        networkPrefix = localNetworkIP;
    
        // Scan the local network in multithread (Faster)
        Thread[] threads = new Thread[255];
        ArrayList<MessageData> talkables = new ArrayList<>();

        // Scan the ips: 192.168.x.[1-255]
        for(int i = 1; i < 256; i++) {
            String ip = localNetworkIP + i;
            if (ip.equals(Config.myIP)) {
                SystemLogger.warning("Skipped scanning " + ip + " because it is my own IP.");
                continue;
            }

            // Create a thread for ping, scan, and information retrieve
            threads[i-1] = new Thread() {
                public void run() {
                    try {

                        // Inner thread
                        Thread t = new Thread() {
                            public void run() {

                                // Ping
                                if(SocketIO.pingPort(ip, Config.recvPort)) {

                                    // Scan for the device information
                                    SocketIO.requestDeviceInformation(ip);

                                    // Wait until the query is received
                                    while (StaticAgent.returnedQuery.equals("")) {
                                        try { Thread.sleep(100); } catch (Exception e) { e.printStackTrace(); }
                                    }

                                    // Add to talkable list
                                    talkables.add(MessageData.parseRespond(StaticAgent.returnedQuery));
                                    SystemLogger.log("Added talkable: " + talkables.get(talkables.size()-1).ip);
                                }
                            }
                        };
                        t.start();
                    }catch(Exception e) {}
                }
            };

            threads[i-1].start();
        }

        // Wait for all threads to finish
        for(int i = 0; i < 255; i++) {
            try {
                threads[i].join();
            }catch(Exception ignored) {}
        }

        // Set global talkable list
        Config.talkables = talkables;

        SystemLogger.log("Finished updating talkables.");
    }
}
