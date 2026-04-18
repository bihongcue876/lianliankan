package front;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 关卡模式 - 基本选关界面
 */
public class LevelGamePanel extends JPanel {
    private JButton btnBack;
    private BufferedImage backgroundImage;
    private GameApp mainFrame;
    private SettingsPanel settingsPanel;
    private HelpPanel helpPanel;
    private CardLayout cardLayout;

    public LevelGamePanel(GameApp mainFrame) {
        this.mainFrame = mainFrame;
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        setPreferredSize(new Dimension(800, 600));
        
        loadBackground();
        
        // 主面板
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), LevelGamePanel.this);
                }
            }
        };
        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(new Dimension(800, 600));
        
        JLabel title = new JLabel("关卡模式");
        title.setFont(new Font("华文中宋", Font.BOLD, 36));
        title.setBounds(300, 100, 200, 50);
        mainPanel.add(title);
        
        btnBack = new JButton("返回");
        btnBack.setBounds(10, 10, 60, 25);
        btnBack.addActionListener(e -> mainFrame.showMainPanel());
        mainPanel.add(btnBack);
        
        add(mainPanel, "main");
        
        // 设置页面
        settingsPanel = new SettingsPanel(mainFrame);
        add(settingsPanel, "settings");
        
        // 帮助页面
        helpPanel = new HelpPanel(mainFrame);
        add(helpPanel, "help");
    }
    
    private void loadBackground() {
        String basePath = System.getProperty("user.dir");
        String[] possiblePaths = {
            basePath + "/src/main/resources/source/level.bmp",
            basePath + "/res/source/level.bmp",
            "src/main/resources/source/level.bmp",
            "source/level.bmp"
        };
        
        for (String path : possiblePaths) {
            try {
                File file = new File(path);
                if (file.exists()) {
                    backgroundImage = ImageIO.read(file);
                    System.out.println("关卡背景加载成功: " + path);
                    break;
                }
            } catch (Exception e) {
                System.err.println("尝试加载失败 " + path + ": " + e.getMessage());
            }
        }
        
        if (backgroundImage == null) {
            System.err.println("警告: 无法加载关卡背景图片");
        }
    }
}
