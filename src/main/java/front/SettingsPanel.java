package front;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * 设置界面
 */
public class SettingsPanel extends JPanel {
    private JButton btnBack;
    private BufferedImage backgroundImage;
    private JPanel parentPanel;
    private Main mainFrame;

    public SettingsPanel(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.parentPanel = null;
        init();
    }
    
    public SettingsPanel(JPanel parentPanel) {
        this.parentPanel = parentPanel;
        this.mainFrame = null;
        init();
    }
    
    private void init() {
        setLayout(null);
        setPreferredSize(new Dimension(800, 600));
        
        loadBackground();
        
        JLabel title = new JLabel("设置");
        title.setFont(new Font("华文中宋", Font.BOLD, 36));
        title.setBounds(350, 100, 150, 50);
        add(title);
        
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
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
    
    private void loadBackground() {
        String bgPath = "source/setting.bmp";
        
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(bgPath)) {
            if (is != null) {
                backgroundImage = ImageIO.read(is);
            }
        } catch (Exception e) {
            System.err.println("设置背景加载失败: " + e.getMessage());
        }
        
        if (backgroundImage == null) {
            try {
                String resourcePath = ResourceManager.getInstance().getResourcePath();
                File file = new File(resourcePath + bgPath);
                if (file.exists()) {
                    backgroundImage = ImageIO.read(file);
                }
            } catch (Exception e) {
                System.err.println("设置背景文件加载失败: " + e.getMessage());
            }
        }
    }
}
