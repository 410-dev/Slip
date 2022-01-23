package localmsgr.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

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
import localmsgr.Config;
import localmsgr.SystemLogger;
import localmsgr.data.QueryData;

public class ReceivedMessageWindow extends JFrame {
    public QueryData qd;

    public JLayeredPane contentPane;

    public JLabel infoLabel;

    public JTextArea messageArea;
    public JScrollPane messageScrollPane;

    public JButton closeButton;
    public JButton replyButton;
    public JButton saveButton;

    public MouseInputAdapter closeButtonListener;
    public MouseInputAdapter replyButtonListener;
    public MouseInputAdapter saveButtonListener;

    public KeyAdapter onPressShiftEnterToReply;

    public ReceivedMessageWindow(QueryData qd) {

        // // TODO: Figure out why the name and IP is swapped ONLY in this class
        // String name = qd.ip;
        // qd.ip = qd.name;
        // qd.name = name;
        // // Done swapping

        this.qd = qd;

        setCloseButtonListener();
        setReplyButtonListener();
        setSaveButtonListener();
        setShiftEnterToReply();

        this.setTitle("Message from " + qd.name);
        this.setSize(Config.messageReceiveWindowSize[0], Config.messageReceiveWindowSize[1]);
        setResizable(false);

        contentPane = new JLayeredPane();
        contentPane.setLayout(null);
        contentPane.setBounds(0, 0, Config.messageReceiveWindowSize[0], Config.messageReceiveWindowSize[1]);
        contentPane.setVisible(true);

        infoLabel = new JLabel("Message from " + qd.name + " at " + qd.date);
        infoLabel.setBounds(10, 10, Config.messageReceiveWindowSize[0], 20);
        infoLabel.setVisible(true);
        contentPane.add(infoLabel);

        String message = qd.message;
        message = CoreBase64.decode(message);
        messageArea = new JTextArea();
        messageArea.setText(message);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBounds(10, 40, Config.messageReceiveWindowSize[0]-20, Config.messageReceiveWindowSize[1] - 100);
        messageArea.grabFocus();
        messageArea.addKeyListener(onPressShiftEnterToReply);
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

        replyButton = new JButton("Reply");
        replyButton.setBounds(120, Config.messageReceiveWindowSize[1] - 55, 100, 30);
        replyButton.addMouseListener(replyButtonListener);
        replyButton.setVisible(true);
        contentPane.add(replyButton);

        saveButton = new JButton("Save");
        saveButton.setBounds(230, Config.messageReceiveWindowSize[1] - 55, 100, 30);
        saveButton.addMouseListener(saveButtonListener);
        saveButton.setVisible(true);
        contentPane.add(saveButton);
        
        this.setContentPane(contentPane);
        this.setVisible(true);
    }

    private void setShiftEnterToReply() {
        onPressShiftEnterToReply = new KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && evt.isShiftDown()) {
                    new SendMessageWindow(qd.name, qd.ip, qd.recvPort, messageArea.getText());
                }
            }
        };
    }

    public void setCloseButtonListener() {
        closeButtonListener = new MouseInputAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
            }
        };
    }

    public void setReplyButtonListener() {
        replyButtonListener = new MouseInputAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new SendMessageWindow(qd.name, qd.ip, qd.recvPort, messageArea.getText());
            }
        };
    }

    public void setSaveButtonListener() {
        saveButtonListener = new MouseInputAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {

                // JFileChooser to select the file to save
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Message");
                fileChooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(java.io.File file) {
                        return file.getName().toLowerCase().endsWith(".txt") || file.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return "Text Files (*.txt)";
                    }
                });

                int returnVal = fileChooser.showSaveDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    // Save file
                    File f = new File(fileChooser.getSelectedFile().getAbsolutePath());
                    if (!f.getName().toLowerCase().endsWith(".txt")) {
                        f = new File(fileChooser.getSelectedFile().getAbsolutePath() + ".txt");
                    }


                    String writingFormat = "FROM: <FROM>\nTO: <TO>\nDATE: <DATE>\nMESSAGE: <MESSAGE>\n\n";
                    String writing = writingFormat.replace("<FROM>", qd.name + " (" + qd.ip + ":" + qd.recvPort + ")");
                    writing = writing.replace("<TO>", "Me");
                    writing = writing.replace("<DATE>", qd.date);
                    writing = writing.replace("<MESSAGE>", messageArea.getText());

                    try {
                        BufferedWriter w = new BufferedWriter(new FileWriter(f));
                        w.write(writing);
                        w.close();
                        JOptionPane.showMessageDialog(null, "Message saved.");
                    }catch(Exception e) {
                        e.printStackTrace();
                        SystemLogger.error("Unable to save file: " + e.getMessage(), true, SystemLogger.CONTINUE, e);
                    }
                }
            }
        };
    }
}
