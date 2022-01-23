import javax.swing.JFrame;

import localmsgr.Config;
import localmsgr.SystemLogger;
import localmsgr.net.LocalNetwork;
import localmsgr.net.SocketIO;
import localmsgr.ui.MainWindow;


public class Main {

    public static void startupSeq() {
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