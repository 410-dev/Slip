package localmsgr.ui;

import javax.swing.JButton;
import javax.swing.JFileChooser;
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
import localmsgr.FileIO;
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
    public JButton fileAttachButton;

    public MouseInputAdapter closeButtonListener;
    public MouseInputAdapter sendButtonListener;
    public MouseInputAdapter fileAttachButtonListener;

    public KeyAdapter onPressShiftEnterToSend;
    
    public String fileName;
    public String fileContentBase64Encoded;
    
    public String ip;
    public int port;

    public SendMessageWindow(String name, String ip, int port, String originalMessage) {

        this.ip = ip;
        this.port = port;

        setShiftEnderToSend();
        setCloseButtonListener();
        setSendButtonListener();
        setFileAttachButtonListener();

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

        fileAttachButton = new JButton("Attach File");
        fileAttachButton.setBounds(230, Config.messageReceiveWindowSize[1] - 55, Config.messageReceiveWindowSize[0] - 240, 30);
        fileAttachButton.addMouseListener(fileAttachButtonListener);
        fileAttachButton.setVisible(true);
        contentPane.add(fileAttachButton);

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

    private void setFileAttachButtonListener() {
        fileAttachButtonListener = new MouseInputAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                attachFile();
            }
        };
    }

    public void attachFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        int returnVal = fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileAttachButton.setText("Attaching...");
            fileAttachButton.setEnabled(false);

            sendButton.setEnabled(false);

            Thread t = new Thread() {
                public void run() {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    String convertedFileContent = FileIO.convertFileToString(filePath);
                    if (convertedFileContent == null) {
                        fileName = null;
                        fileContentBase64Encoded = null;
                        fileAttachButton.setText("Attach File");
                        fileAttachButton.setEnabled(true);
                        sendButton.setEnabled(true);
                        return;
                    }

                    fileName = fileChooser.getSelectedFile().getName();
                    fileContentBase64Encoded = CoreBase64.encode(convertedFileContent);

                    sendButton.setEnabled(true);
                    fileAttachButton.setText(fileName);
                    fileAttachButton.setEnabled(true);
                }
            };
            t.start();
        }
    }

    public void send() {
        String messageFromBox = messageArea.getText();
        if (messageFromBox.equals("")) {
            if (fileName != null && fileContentBase64Encoded != null) {
                SystemLogger.log("Empty message field, but will be ignored because file is attached.");
            }else{
                JOptionPane.showMessageDialog(null, "Message cannot be empty.");
                SystemLogger.warning("Message is empty.");
                return;
            }
        }
        
        if (fileName != null && fileContentBase64Encoded != null) {
            messageArea.append("\n\n*************\nFile Attached: " + fileName + "\n*************\n\n");
        }

        String messageFromBoxNTHREAD = messageArea.getText();

        sendButton.setText("Sending...");
        sendButton.setEnabled(false);
        Thread t = new Thread() {
            public void run() {
                String message = CoreBase64.encode(messageFromBoxNTHREAD);
                MessageData qd = new MessageData(Config.myIP, Config.myName, Config.recvPort, message, DateManager.getTimestamp());

                if (fileName != null && fileContentBase64Encoded != null) {
                    qd.fileName = CoreBase64.encode(fileName);
                    qd.fileContent = fileContentBase64Encoded;
                }

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
