package front;

import back.ResourceManager;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

public class GameApp extends JFrame {
    private BufferedImage backgroundImage;
    
    private JPanel mainPanel;
    private BasicGamePanel basicGamePanel;
    private RelaxGamePanel relaxGamePanel;
    private LevelGamePanel levelGamePanel;
    private RankPanel rankPanel;
    private SettingsPanel settingsPanel;
    private HelpPanel helpPanel;
    
    private JButton btnBasic;
    private JButton btnRelax;
    private JButton btnLevel;
    private JButton btnRank;
    private JButton btnSettings;
    private JButton btnHelp;

    public GameApp() {
        initUI();
    }

    private void initUI() {
        setTitle("欢乐连连看");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        loadBackground();
        
        setSize(800, 600);
        
        setLocationRelativeTo(null);
        
        setIconImage(loadIcon());
        
        initPanels();
        
        setContentPane(mainPanel);
    }

    private void initPanels() {
        mainPanel = createMainPanel();
        
        basicGamePanel = new BasicGamePanel(this);
        relaxGamePanel = new RelaxGamePanel(this);
        levelGamePanel = new LevelGamePanel(this);
        
        rankPanel = new RankPanel(this);
        settingsPanel = new SettingsPanel(this);
        helpPanel = new HelpPanel(this);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setLayout(null);
        
        btnBasic = createButton("基本模式", 15, 210, 115, 60);
        btnBasic.addActionListener(e -> showPanel(basicGamePanel));
        panel.add(btnBasic);
        
        btnRelax = createButton("休闲模式", 15, 310, 115, 60);
        btnRelax.addActionListener(e -> showPanel(relaxGamePanel));
        panel.add(btnRelax);
        
        btnLevel = createButton("关卡模式", 15, 410, 115, 60);
        btnLevel.addActionListener(e -> showPanel(levelGamePanel));
        panel.add(btnLevel);
        
        btnRank = createSmallButton("排行榜", 490, 515, 85, 40);
        btnRank.addActionListener(e -> showPanel(rankPanel));
        panel.add(btnRank);
        
        btnSettings = createSmallButton("设置", 585, 515, 85, 40);
        btnSettings.addActionListener(e -> showPanel(settingsPanel));
        panel.add(btnSettings);
        
        btnHelp = createSmallButton("帮助", 680, 515, 85, 40);
        btnHelp.addActionListener(e -> showPanel(helpPanel));
        panel.add(btnHelp);
        
        return panel;
    }
    
    private void showPanel(JPanel panel) {
        setContentPane(panel);
        revalidate();
        repaint();
    }
    
    public void showMainPanel() {
        setContentPane(mainPanel);
        revalidate();
        repaint();
    }

    private JButton createButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setFont(new Font("华文中宋", Font.BOLD, 20));
        button.setForeground(new Color(139, 69, 19));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setMargin(new Insets(0, 0, 0, 0));
        return button;
    }

    private JButton createSmallButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(210, 180, 140, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setBounds(x, y, width, height);
        button.setFont(new Font("华文中宋", Font.BOLD, 14));
        button.setForeground(new Color(80, 40, 20));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        
        return button;
    }

    private void loadBackground() {
        String bgPath = "source/llk_main.bmp";
        
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(bgPath)) {
            if (is != null) {
                backgroundImage = ImageIO.read(is);
            }
        } catch (Exception e) {
            System.err.println("背景加载失败: " + e.getMessage());
        }
        
        if (backgroundImage == null) {
            try {
                String resourcePath = ResourceManager.getInstance().getResourcePath();
                File file = new File(resourcePath + bgPath);
                if (file.exists()) {
                    backgroundImage = ImageIO.read(file);
                }
            } catch (Exception e) {
                System.err.println("背景文件加载失败: " + e.getMessage());
            }
        }
    }

    private Image loadIcon() {
        String iconPath = "source/LLKico.bmp";
        
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(iconPath)) {
            if (is != null) {
                return ImageIO.read(is);
            }
        } catch (Exception e) {
            System.err.println("类加载器加载失败: " + e.getMessage());
        }
        
        try {
            String resourcePath = ResourceManager.getInstance().getResourcePath();
            File file = new File(resourcePath + iconPath);
            if (file.exists()) {
                return ImageIO.read(file);
            }
        } catch (Exception e) {
            System.err.println("文件加载失败: " + e.getMessage());
        }
        
        return null;
    }
}
