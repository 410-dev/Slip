package localmsgr;

import localmsgr.data.QueryData;
import localmsgr.net.SocketIO;
import localmsgr.ui.ReceivedMessageWindow;

public class StaticAgent {

    // public static ArrayList<QueryData> eventedData = new ArrayList<>();
    public static String returnedQuery = "";

    public static void onMessageRecv(String messageWithMetaData) {

        if (messageWithMetaData == null) {
            SystemLogger.log("There was a ping.");
            return;
        }

        QueryData qd = QueryData.parseRespond(messageWithMetaData);
        if (qd.message.equals(Config.deviceInfoRequestMessage)) {
            SystemLogger.log("Device info request received. Sending device info.");
            QueryData thisDeviceData = new QueryData(Config.myIP, Config.myName, Config.recvPort, Config.deviceInfoReplyMessage, DateManager.getTimestamp());
            thisDeviceData.progVersion = Config.programVersion;
            thisDeviceData.version = Config.communicationProtocolVersion;
            SocketIO.sendData(qd.ip, qd.recvPort, thisDeviceData.buildString());
            SystemLogger.log("Device info sent.");
        } else if (qd.message.equals(Config.deviceInfoReplyMessage)) {
            SystemLogger.log("Device info received.");
            returnedQuery = messageWithMetaData;
        }else{
            // eventedData.add(qd);
            SystemLogger.log("Message received: " + messageWithMetaData);
            new ReceivedMessageWindow(qd);
        }
    }
}
