import javax.swing.*;

public class Main extends JFrame {
    public Main() {
        initUI();
    }

    private void initUI() {
        setTitle("连连看");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main window = new Main();
            window.setVisible(true);
        });
    }
}
