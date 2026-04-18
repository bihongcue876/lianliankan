package front;

import javax.swing.*;
import java.awt.*;

/**
 * 帮助界面
 */
public class HelpPanel extends JPanel {
    private JButton btnBack;
    private JPanel parentPanel;
    private Main mainFrame;

    public HelpPanel(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.parentPanel = null;
        init();
    }
    
    public HelpPanel(JPanel parentPanel) {
        this.parentPanel = parentPanel;
        this.mainFrame = null;
        init();
    }
    
    private void init() {
        setLayout(null);
        setPreferredSize(new Dimension(800, 600));
        
        JLabel title = new JLabel("帮助");
        title.setFont(new Font("华文中宋", Font.BOLD, 36));
        title.setBounds(350, 100, 150, 50);
        add(title);
        
        JTextArea text = new JTextArea("连连看游戏规则：\n\n选择两个相同的图案进行消除。\n两个图案之间的连线不能超过3条线段。");
        text.setFont(new Font("华文中宋", Font.PLAIN, 18));
        text.setBounds(200, 180, 400, 150);
        text.setEditable(false);
        text.setOpaque(false);
        add(text);
        
        btnBack = new JButton("返回");
        btnBack.setBounds(350, 400, 100, 40);
        btnBack.addActionListener(e -> goBack());
        add(btnBack);
    }
    
    private void goBack() {
        if (mainFrame != null) {
            mainFrame.showMainPanel();
        } else if (parentPanel instanceof BasicGamePanel) {
            ((BasicGamePanel) parentPanel).showGamePanel();
        } else if (parentPanel instanceof RelaxGamePanel) {
            ((RelaxGamePanel) parentPanel).showGamePanel();
        }
    }
}
