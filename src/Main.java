import javax.swing.JFrame;
import javax.swing.JOptionPane;

import localmsgr.Config;
import localmsgr.SystemLogger;
import localmsgr.net.LocalNetwork;
import localmsgr.net.SocketIO;
import localmsgr.ui.MainWindow;
import updateutil.UpdateUtility;
import updateutil.Versioning;


public class Main {

    public static void startupSeq() {

        // Initialize update utility
        Versioning.setBuildNum(Config.BUILD_NUM);
        Versioning.setUpdateCheckerURL(Config.updateInfURL);
        Versioning.useStableRelease();
        try {
            // Retrieve latest version info from server
            Object[] result = UpdateUtility.checkUpdate();
            
            if ((Boolean) result[0] == true) {   // Has update
                // Prompt update
                int resultInt = JOptionPane.showConfirmDialog(null, "Update available!\nCurrent build: " + Versioning.getBuildNum() + "\nLatest build: " + result[1] + "\nDownload URL: " + result[2] + "\n\nUpdate now?", "Update available", JOptionPane.YES_NO_OPTION);
                
                // Run update if yes is pressed
                if (resultInt == JOptionPane.YES_OPTION) {
                    UpdateUtility.doUpdate("./Slip.jar");
                    JOptionPane.showMessageDialog(null, "Update downloaded.");
                    System.exit(0);
                }
            }
        }catch(Exception e) {
            SystemLogger.error("Update Failed", true, SystemLogger.CONTINUE, e);
        }
        

        Config.myIP = LocalNetwork.getNetworkAddress();
        Config.myName = Config.myIP;
        SystemLogger.log("My IP: " + Config.myIP);
        Config.loadOptions();
        SocketIO.listenerStart();
        LocalNetwork.scanPortOpenDevices();
    }

    public static void main(String[] args){
        startupSeq();
        new MainWindow();
    }

    public static void dummyWindow() {
        JFrame frame = new JFrame("Dummy Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(10, 10);
        frame.setVisible(true);
    }
}