package localmsgr.ui;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.event.MouseInputAdapter;

public class NotificationFrame extends JFrame {

    public JLayeredPane contentPane;
    public JLabel infoLabel;
    public JButton closeButton;

    public NotificationFrame(String name) {
        this.setTitle("Message from " + name);
        this.setSize(200, 100);
        this.setMinimumSize(new Dimension(200, 100));
        this.setResizable(false);
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.setLocation(0, 0);
        this.pack();
        

        contentPane = new JLayeredPane();
        contentPane.setLayout(null);
        
        

        // Set infoLabel to the center of contentPane
        infoLabel = new JLabel("Message from " + name);
        infoLabel.setSize(10 * infoLabel.getText().length(), 100);
        infoLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        infoLabel.setAlignmentY(JLabel.CENTER_ALIGNMENT);
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        infoLabel.setVisible(true);
        this.setSize(infoLabel.getWidth() + 30, this.getHeight());
        contentPane.setBounds(0, 0, this.getWidth(), this.getHeight());
        contentPane.add(infoLabel);

        // Set closeButton to the top right corner of contentPane
        closeButton = new JButton("X");
        closeButton.setSize(20, 20);
        closeButton.setLocation(this.getWidth() - closeButton.getWidth(), 0);
        closeButton.setVisible(true);
        closeButton.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
            }
        });
        contentPane.add(closeButton);

        contentPane.setVisible(true);
        this.setContentPane(contentPane);
        this.setVisible(true);
    }
}
