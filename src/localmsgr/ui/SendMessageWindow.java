package localmsgr.ui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.MouseInputAdapter;

import java.awt.event.KeyAdapter;

import localmsgr.CoreBase64;
import localmsgr.DateManager;
import localmsgr.Config;
import localmsgr.SystemLogger;
import localmsgr.data.MessageData;
import localmsgr.net.SocketIO;

public class SendMessageWindow extends JFrame {

    public JLayeredPane contentPane;

    public JLabel infoLabel;

    public JTextArea messageArea;
    public JScrollPane messageScrollPane;

    public JButton closeButton;
    public JButton sendButton;

    public MouseInputAdapter closeButtonListener;
    public MouseInputAdapter sendButtonListener;

    public KeyAdapter onPressShiftEnterToSend;

    public String ip;
    public int port;

    public SendMessageWindow(String name, String ip, int port, String originalMessage) {

        this.ip = ip;
        this.port = port;

        setShiftEnderToSend();
        setCloseButtonListener();
        setSendButtonListener();

        this.setTitle("Message to " + name);
        this.setSize(Config.messageReceiveWindowSize[0], Config.messageReceiveWindowSize[1]);
        this.setResizable(false);

        contentPane = new JLayeredPane();
        contentPane.setLayout(null);
        contentPane.setBounds(0, 0, Config.messageReceiveWindowSize[0], Config.messageReceiveWindowSize[1]);
        contentPane.setVisible(true);

        infoLabel = new JLabel("Message to " + name);
        infoLabel.setBounds(10, 10, Config.messageReceiveWindowSize[0], 20);
        infoLabel.setVisible(true);
        contentPane.add(infoLabel);

        messageArea = new JTextArea();
        messageArea.setEditable(true);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBounds(10, 40, Config.messageReceiveWindowSize[0]-20, Config.messageReceiveWindowSize[1] - 100);
        messageArea.addKeyListener(onPressShiftEnterToSend);

        if (originalMessage != null) {
            messageArea.setText("*************\nReply written at" + DateManager.getTimestamp() + "\n*************\n\n");
            messageArea.append(originalMessage);
            messageArea.append("\n\n*************\n\n");
        }

        messageArea.setVisible(true);

        messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setBounds(10, 40, Config.messageReceiveWindowSize[0]-20, Config.messageReceiveWindowSize[1] - 100);
        messageScrollPane.setVisible(true);
        contentPane.add(messageScrollPane);

        closeButton = new JButton("Close");
        closeButton.setBounds(10, Config.messageReceiveWindowSize[1] - 55, 100, 30);
        closeButton.addMouseListener(closeButtonListener);
        closeButton.setVisible(true);
        contentPane.add(closeButton);

        sendButton = new JButton("Send");
        sendButton.setBounds(120, Config.messageReceiveWindowSize[1] - 55, 100, 30);
        sendButton.addMouseListener(sendButtonListener);
        sendButton.setVisible(true);
        contentPane.add(sendButton);
        
        this.setContentPane(contentPane);
        this.setVisible(true);
    }
    
    private void setShiftEnderToSend() {
        onPressShiftEnterToSend = new KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && evt.isShiftDown()) {
                    send();
                }
            }
        };
    }

    public void setCloseButtonListener() {
        closeButtonListener = new MouseInputAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
            }
        };
    }

    public void setSendButtonListener() {
        sendButtonListener = new MouseInputAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                send();
            }
        };
    }

    public void send() {
        String messageFromBox = messageArea.getText();
        if (messageFromBox.equals("")) {
            SystemLogger.warning("Message is empty.");
            return;
        }

        sendButton.setText("Sending...");
        sendButton.setEnabled(false);
        Thread t = new Thread() {
            public void run() {
                String message = CoreBase64.encode(messageFromBox);
                MessageData qd = new MessageData(Config.myIP, Config.myName, Config.recvPort, message, DateManager.getTimestamp());
                boolean sent = SocketIO.sendData(ip, port, qd.buildString());
                if (sent) {
                    SystemLogger.log("Message sent.");
                    dispose();
                    JOptionPane.showMessageDialog(null, "Message successfully sent.");
                }else{
                    SystemLogger.error("Message failed to send. Perhaps the recipient is no longer online?", true, SystemLogger.CONTINUE, null);
                    sendButton.setText("Send");
                    sendButton.setEnabled(true);
                }
            }
        };
        t.start();
    }
}
