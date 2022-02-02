package localmsgr;

import java.io.InputStream;
import java.io.BufferedInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class NotificationSound {
    public void playSound() {
        Thread t = new Thread() {
            public void run() {
                try {
                    SystemLogger.log("Loading notification sound");
                    InputStream is = getClass().getResourceAsStream("/res/sfx/notification.wav"); 
                    AudioInputStream audioInput = AudioSystem.getAudioInputStream(new BufferedInputStream(is));

                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInput);
                    clip.start();
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };

        t.start();
    }
}
