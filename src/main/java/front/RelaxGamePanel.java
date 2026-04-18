package front;

import javax.swing.*;
import java.awt.*;

/**
 * 休闲模式游戏界面
 */
public class RelaxGamePanel extends JPanel {
    private JButton btnBack;
    private JButton btnStart;
    private JButton btnPause;
    private JButton btnHint;
    private JButton btnShuffle;
    private JButton btnSettings;
    private JButton btnHelp;
    private GamePanel gamePanel;
    private Main mainFrame;
    private SettingsPanel settingsPanel;
    private HelpPanel helpPanel;
    private CardLayout cardLayout;

    public RelaxGamePanel(Main mainFrame) {
        this.mainFrame = mainFrame;
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        setPreferredSize(new Dimension(800, 600));
        
        // 游戏面板
        JPanel gamePanelContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 右侧蒙板区域（避开底部按钮）
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.fillRect(650, 0, 150, 530);
            }
        };
        gamePanelContainer.setLayout(null);
        gamePanelContainer.setPreferredSize(new Dimension(800, 600));
        
        gamePanel = new GamePanel();
        gamePanel.setBounds(0, 0, 650, 600);
        gamePanelContainer.add(gamePanel);
        
        initButtons(gamePanelContainer);
        
        add(gamePanelContainer, "game");
        
        // 设置页面
        settingsPanel = new SettingsPanel(this);
        add(settingsPanel, "settings");
        
        // 帮助页面
        helpPanel = new HelpPanel(this);
        add(helpPanel, "help");
    }
    
    private void initButtons(JPanel gamePanelContainer) {
        // 返回按钮 - 左上角
        btnBack = new JButton("返回");
        btnBack.setFont(new Font("华文中宋", Font.PLAIN, 12));
        btnBack.setBounds(10, 10, 60, 25);
        btnBack.addActionListener(e -> mainFrame.showMainPanel());
        gamePanelContainer.add(btnBack);
        
        // 右上角按钮列
        int btnWidth = 100;
        int btnHeight = 35;
        int btnX = 680;
        
        btnStart = new JButton("开始游戏");
        btnStart.setFont(new Font("华文中宋", Font.PLAIN, 14));
        btnStart.setBounds(btnX, 20, btnWidth, btnHeight);
        btnStart.addActionListener(e -> gamePanel.startGame());
        gamePanelContainer.add(btnStart);
        
        btnPause = new JButton("暂停");
        btnPause.setFont(new Font("华文中宋", Font.PLAIN, 14));
        btnPause.setBounds(btnX, 65, btnWidth, btnHeight);
        btnPause.addActionListener(e -> gamePanel.pauseGame());
        gamePanelContainer.add(btnPause);
        
        btnHint = new JButton("提示");
        btnHint.setFont(new Font("华文中宋", Font.PLAIN, 14));
        btnHint.setBounds(btnX, 110, btnWidth, btnHeight);
        btnHint.addActionListener(e -> gamePanel.showHint());
        gamePanelContainer.add(btnHint);
        
        btnShuffle = new JButton("重排");
        btnShuffle.setFont(new Font("华文中宋", Font.PLAIN, 14));
        btnShuffle.setBounds(btnX, 155, btnWidth, btnHeight);
        btnShuffle.addActionListener(e -> gamePanel.shuffleMap());
        gamePanelContainer.add(btnShuffle);
        
        // 右下角小按钮 - 放在蒙板左侧
        int smallBtnWidth = 70;
        int smallBtnHeight = 28;
        
        btnSettings = new JButton("设置");
        btnSettings.setFont(new Font("华文中宋", Font.PLAIN, 12));
        btnSettings.setBounds(570, 560, smallBtnWidth, smallBtnHeight);
        btnSettings.addActionListener(e -> cardLayout.show(this, "settings"));
        gamePanelContainer.add(btnSettings);
        
        btnHelp = new JButton("帮助");
        btnHelp.setFont(new Font("华文中宋", Font.PLAIN, 12));
        btnHelp.setBounds(490, 560, smallBtnWidth, smallBtnHeight);
        btnHelp.addActionListener(e -> cardLayout.show(this, "help"));
        gamePanelContainer.add(btnHelp);
    }
    
    public void showGamePanel() {
        cardLayout.show(this, "game");
    }
}
