import front.GameApp;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameApp window = new GameApp();
            window.setVisible(true);
        });
    }
}
