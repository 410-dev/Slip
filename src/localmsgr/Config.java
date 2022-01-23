package localmsgr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.ArrayList;

import localmsgr.data.MessageData;

public class Config {

    public static final int configVersion = 1;
    public static final int communicationProtocolVersion = 1;
    public static final String programVersion = "1.0";

    public static String myIP = "";
    public static String myName = "";
    public static int recvPort = 30551;

    public static int[] messageReceiveWindowSize = {500, 400};
    public static int[] mainWindowSize = {400, 600};

    public static int[] minimumDefaultMessageReceiveWindowSize = {500, 400};
    public static int[] minimumDefaultMainWindowSize = {400, 600};

    public static int updateInterval = 30; // seconds

    public static ArrayList<MessageData> talkables = new ArrayList<MessageData>();
    public static String deviceInfoRequestMessage = "DEVICEINFO_REQUEST_MESSAGE";
    public static String deviceInfoReplyMessage = "DEVICEINFO_REPLY_MESSAGE";

    public static void parseOptions(String options) {
        String[] optionsSplit = options.split("\n");
        for (String option : optionsSplit) {
            String[] optionSplit = option.split("=");
            if (optionSplit.length == 2) {
                if (optionSplit[0].equals("name")) {
                    myName = optionSplit[1];
                } else if (optionSplit[0].equals("recvPort")) {
                    recvPort = Integer.parseInt(optionSplit[1]);
                } else if (optionSplit[0].equals("messageReceiveWindowSize")) {
                    String[] sizeSplit = optionSplit[1].split(",");

                    if (sizeSplit.length == 2) {
                        messageReceiveWindowSize[0] = Integer.parseInt(sizeSplit[0]);
                        messageReceiveWindowSize[1] = Integer.parseInt(sizeSplit[1]);

                        if (messageReceiveWindowSize[0] < minimumDefaultMessageReceiveWindowSize[0]) {
                            messageReceiveWindowSize[0] = minimumDefaultMessageReceiveWindowSize[0];
                        }
                        if (messageReceiveWindowSize[1] < minimumDefaultMessageReceiveWindowSize[1]) {
                            messageReceiveWindowSize[1] = minimumDefaultMessageReceiveWindowSize[1];
                        }
                    }
                } else if (optionSplit[0].equals("mainWindowSize")) {
                    String[] sizeSplit = optionSplit[1].split(",");
                    if (sizeSplit.length == 2) {
                        mainWindowSize[0] = Integer.parseInt(sizeSplit[0]);
                        mainWindowSize[1] = Integer.parseInt(sizeSplit[1]);

                        if (mainWindowSize[0] < minimumDefaultMainWindowSize[0]) {
                            mainWindowSize[0] = minimumDefaultMainWindowSize[0];
                        }
                        if (mainWindowSize[1] < minimumDefaultMainWindowSize[1]) {
                            mainWindowSize[1] = minimumDefaultMainWindowSize[1];
                        }
                    }
                } else if (optionSplit[0].equals("updateInterval")) {
                    updateInterval = Integer.parseInt(optionSplit[1]);
                }
            }
        }
    }

    public static String saveFormat() {
        String toReturn = "";
        toReturn += "name=" + myName + "\n";
        toReturn += "recvPort=" + recvPort + "\n";
        toReturn += "messageReceiveWindowSize=" + messageReceiveWindowSize[0] + "," + messageReceiveWindowSize[1] + "\n";
        toReturn += "mainWindowSize=" + mainWindowSize[0] + "," + mainWindowSize[1] + "\n";
        toReturn += "updateInterval=" + updateInterval + "\n";
        return toReturn;
    }

    public static File optionFile = new File(System.getProperty("user.home") + File.separator + "options_localslip.txt");
    public static void save() {
        try {
            BufferedWriter w = new BufferedWriter(new FileWriter(optionFile));
            w.write(Config.saveFormat());
            w.close();
        }catch(Exception e) {
            SystemLogger.error("Error while saving configurations: " + e.getMessage(), true, SystemLogger.CONTINUE, e);
        }
    }

    public static void loadOptions() {
        if (optionFile.isFile()) {
            // Load from it
            SystemLogger.log("Loading options from " + optionFile.getAbsolutePath());

            String options = "";
            try {
                BufferedReader r = new BufferedReader(new FileReader(optionFile));
                for(String line = r.readLine(); line != null; line = r.readLine()) {
                    options += line + "\n";
                }
                r.close();
                Config.parseOptions(options);
            }catch(Exception e) {
                SystemLogger.error("Error loading options from " + optionFile.getAbsolutePath(), true, SystemLogger.CONTINUE, e);
            }
        }else{
            // Create it
            SystemLogger.log("Creating options file at " + optionFile.getAbsolutePath());
            Config.save();
        }
    }
}
