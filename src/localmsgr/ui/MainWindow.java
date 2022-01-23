package localmsgr.ui;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;

import java.awt.event.KeyAdapter;

import localmsgr.Config;
import localmsgr.SystemLogger;
import localmsgr.data.MessageData;
import localmsgr.net.LocalNetwork;

public class MainWindow extends JFrame {
    
    public JLayeredPane contentPane;

    public JLabel versionLabel;
    public JButton rescanButton;

    public JLabel infoLabel;
    public JButton exitButton;

    public JLabel nameLabel;
    public JTextField nameTextField;

    public DefaultListModel<String> listModel;
    public JList<String> list;

    public MouseInputAdapter rescanButtonListener;
    public MouseInputAdapter exitButtonListener;
    public MouseInputAdapter listListener;

    public KeyAdapter onPressEnterToSaveName;

    public MainWindow() {
        setTitle("Slip");
        setSize(Config.mainWindowSize[0], Config.mainWindowSize[1]);
        setResizable(false);

        setEnterKeyBehaviour();
        setRescanButtonListener();
        setExitButtonListener();
        setListListener();

        contentPane = new JLayeredPane();
        contentPane.setLayout(null);
        contentPane.setBounds(0, 0, Config.mainWindowSize[0], Config.mainWindowSize[1]);
        contentPane.setVisible(true);

        versionLabel = new JLabel("Version: " + Config.programVersion + " (Comm: " + Config.communicationProtocolVersion + ")");
        versionLabel.setBounds(10, 10, Config.mainWindowSize[0], 20);
        versionLabel.setVisible(true);
        contentPane.add(versionLabel);

        rescanButton = new JButton("Rescan");
        rescanButton.setBounds(10, 40, Config.mainWindowSize[0] - 20, 20);
        rescanButton.addMouseListener(rescanButtonListener);
        rescanButton.setVisible(true);
        contentPane.add(rescanButton);

        infoLabel = new JLabel("My IP: " + Config.myIP + " (Port: " + Config.recvPort + ")");
        infoLabel.setBounds(10, 70, Config.mainWindowSize[0] - 20, 20);
        infoLabel.setVisible(true);
        contentPane.add(infoLabel);

        nameLabel = new JLabel("My Name: ");
        nameLabel.setBounds(10, 100, Config.mainWindowSize[0] - 20, 20);
        nameLabel.setVisible(true);
        contentPane.add(nameLabel);

        nameTextField = new JTextField();
        nameTextField.setBounds(100, 100, Config.mainWindowSize[0] - 120, 20);
        nameTextField.setText(Config.myName);
        nameTextField.addKeyListener(onPressEnterToSaveName);
        nameTextField.setVisible(true);
        contentPane.add(nameTextField);

        listModel = new DefaultListModel<String>();
        list = new JList<String>(listModel);
        list.setBounds(10, 130, Config.mainWindowSize[0] - 20, Config.mainWindowSize[1] - 190);
        list.addMouseListener(listListener);
        list.setVisible(true);
        contentPane.add(list);

        exitButton = new JButton("Quit (Offline)");
        exitButton.setBounds(10, Config.mainWindowSize[1] - 50, Config.mainWindowSize[0] - 20, 20);
        exitButton.addMouseListener(exitButtonListener);
        exitButton.setVisible(true);
        contentPane.add(exitButton);

        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        Thread asyncNetworkUpdate = new Thread() {
            public void run() {
                while (true) {
                    try {
                        updateNetwork();
                        Thread.sleep(Config.updateInterval * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread asyncListUpdate = new Thread() {
            public void run() {
                while (true) {
                    try {
                        updateList();
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        
        asyncNetworkUpdate.start();
        asyncListUpdate.start();

        setVisible(true);
    }

    public void updateNetwork() {
        rescanButton.setText("Scanning...");
        rescanButton.setEnabled(false);
        Thread t = new Thread() {
            public void run() {
                LocalNetwork.scanPortOpenDevices();
                SystemLogger.log("Scanning finished: " + Config.talkables.size() + " devices found.");
                rescanButton.setText("Rescan");
                rescanButton.setEnabled(true);
            }
        };

        t.start();
    }

    public void updateList() {
        try {
            listModel.clear();
            for (MessageData q : Config.talkables) {
                if (q.compatible) {
                    listModel.addElement(q.name + " (" + q.ip + ")");
                } else {
                    listModel.addElement(q.name + " (Incompatible)");
                }
            }
        }catch(Exception e) {
            SystemLogger.log("Error while updating list: " + e.getMessage());
        }
        // contentPane.repaint();
        // repaint();
    }

    public void setEnterKeyBehaviour() {
        onPressEnterToSaveName = new KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    Config.myName = nameTextField.getText();
                    SystemLogger.log("My name changed to: " + Config.myName);
                    JOptionPane.showMessageDialog(null, "Device name has been changed to: " + Config.myName);
                    Config.save();
                }
            }
        };
    }

    public void setExitButtonListener() {
        exitButtonListener = new MouseInputAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                System.exit(0);
            }
        };
    }

    public void setRescanButtonListener() {
        rescanButtonListener = new MouseInputAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                updateNetwork();
            }
        };
    }

    public void setListListener() {
        listListener = new MouseInputAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    if (index != -1) {
                        MessageData q = Config.talkables.get(index);
                        if (q.compatible) {
                            new SendMessageWindow(q.name, q.ip, q.recvPort, null);
                        }
                    }
                }
            }
        };
    }
}
