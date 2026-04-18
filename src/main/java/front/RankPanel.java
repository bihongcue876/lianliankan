package front;

import javax.swing.*;
import java.awt.*;

/**
 * 排行榜界面
 */
public class RankPanel extends JPanel {
    private JButton btnBack;
    private Main mainFrame;

    public RankPanel(Main mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(null);
        setPreferredSize(new Dimension(800, 600));
        
        JLabel title = new JLabel("排行榜");
        title.setFont(new Font("华文中宋", Font.BOLD, 36));
        title.setBounds(300, 100, 200, 50);
        add(title);
        
        btnBack = new JButton("返回");
        btnBack.setBounds(350, 400, 100, 40);
        btnBack.addActionListener(e -> mainFrame.showMainPanel());
        add(btnBack);
    }
}
