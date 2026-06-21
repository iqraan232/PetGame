package virtualpetsimulator;

import javax.sound.sampled.*;
import java.net.URL;

public class SoundManager {

    private static Clip backgroundClip;

    public static void startBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) return; // already playing

        try {
            URL url = SoundManager.class.getResource("/virtualpetsimulator/sounds/background.wav");
            if (url == null) {
                System.out.println("Sound file not found: /virtualpetsimulator/sounds/background.wav");
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(ais);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY); // loop forever
            backgroundClip.start();

        } catch (Exception e) {
            System.out.println("Could not play background music: " + e.getMessage());
        }
    }

    public static void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
    }

    public static void setVolume(float volume) {
        // volume: 0.0f (silent) to 1.0f (full)
        if (backgroundClip == null) return;
        try {
            FloatControl fc = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = fc.getMinimum();
            float max = fc.getMaximum();
            fc.setValue(min + (max - min) * volume);
        } catch (Exception e) {
            System.out.println("Volume control not supported: " + e.getMessage());
        }
    }
}
