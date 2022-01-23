package localmsgr.data;

import localmsgr.DateManager;
import localmsgr.Config;
import localmsgr.CoreBase64;
import localmsgr.SystemLogger;


// Basic structure for communication:
// This class will parse / build the message in a string format.
// The instance will contain the informations about the sender and the message.

public class MessageData {

    public int version;                 // The version of the protocol used
    public String progVersion;          // The version of the program
    public String ip;                   // The IP of the origin of message
    public String name;                 // The name of the message author
    public int recvPort;                // The port of the origin of message
    public String message;              // The message itself (Encoded with Base64)
    public String date;                 // The date of the message
    public String fileName;             // The name of the file attached to the message (Encoded with Base64)
    public String fileContent;          // The content of the file attached to the message (Encoded with Base64)
    public boolean compatible = true;   // If the instance is compatible with the current version of the program

    // Constructor
    public MessageData(String ip, String name, int recvPort, String message, String date) {
        this.ip = ip;
        this.name = name;
        this.recvPort = recvPort;
        this.message = message;
        this.date = date;
    }

    public MessageData(){}


    // Convert current instance to a string
    // This will be sent via the socket
    public String buildString() {
        String s = "";
        if (this.version == 0) this.version = Config.communicationProtocolVersion;    // If the version is not set, set it to the current version
        if (this.progVersion == null) this.progVersion = Config.programVersion;       // If the program version is not set, set it to the current version

        s += "COMMVERSION=" + this.version + ";";
        s += "PROGVERSION=" + this.progVersion + ";";
        s += "IP=" + this.ip + ";";
        s += "NAME=" + CoreBase64.encode(this.name) + ";";                            // Encode the name with Base64
        s += "RECVPORT=" + this.recvPort + ";";
        s += "MESSAGE=" + this.message + ";";
        if (this.date == null) this.date = DateManager.getTimestamp();                // If the date is not set, set it to the current date
        s += "DATE=" + this.date + ";";

        if (this.fileName != null && this.fileContent != null) {
            s += "FILENAME=" + this.fileName + ";";
            s += "FILECONTENT=" + this.fileContent + ";";
        }

        SystemLogger.debug("Built string: " + s);
        return s;
    }

    // String data which will be sent via the socket
    // This will be received by the other side
    // The receiver will reply the device information
    public static String deviceInformationRequestBuilder() {
        String s = "";
        s += "COMMVERSION=" + Config.communicationProtocolVersion + ";";
        s += "PROGVERSION=" + Config.programVersion + ";";
        s += "IP=" + Config.myIP + ";";
        s += "NAME=" + CoreBase64.encode(Config.myName) + ";";                        // Encode the name with Base64
        s += "RECVPORT=" + Config.recvPort + ";";
        s += "MESSAGE=" + Config.deviceInfoRequestMessage + ";";
        s += "DATE=" + DateManager.getTimestamp() + ";";                              // Set the date to the current date
        return s;
    }

    // Parse a string to a QueryData instance
    public static MessageData parseRespond(String respond) {
        String[] lines = respond.split(";");                          // Split the string by ;

        MessageData q = new MessageData();

        for (String line : lines) {                                   // For each line
            String[] keyValue = line.split("=");                      // Split the line by =         
            if (keyValue.length != 2) {                               // If the line is not valid
                continue;                                             // Skip this line
            }
            String key = keyValue[0];                                 // Get the key
            String value = keyValue[1];                               // Get the value
            if (key.equals("COMMVERSION")) {
                q.version = Integer.parseInt(value);                  // Check compatibility
                if (q.version != Config.communicationProtocolVersion) {
                    SystemLogger.log("Incompatible CommVersion: " + q.version);
                    q.compatible = false;
                }else{
                    q.compatible = true;
                }
            } else if (key.equals("PROGVERSION")) {                   // Program Version
                q.progVersion = value;
            } else if (key.equals("IP")) {                            // IP
                q.ip = value;
            } else if (key.equals("NAME")) {                          // Author Name
                q.name = CoreBase64.decode(value);
            } else if (key.equals("RECVPORT")) {                      // Port
                q.recvPort = Integer.parseInt(value);
            } else if (key.equals("DATE")) {                          // Date
                q.date = value;
            } else if (key.equals("MESSAGE")) {                       // Message
                q.message = value;
            } else if (key.equals("FILENAME")) {                      // File Name
                q.fileName = value;
            } else if (key.equals("FILECONTENT")) {                   // File Content
                q.fileContent = value;
            }
        }

        return q;
    }
}