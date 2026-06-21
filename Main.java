package virtualpetsimulator;
 
import javax.swing.SwingUtilities;
 
public class Main {
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StartScreen().setVisible(true));
    }
}

