package com.lianliankan.ui;

import com.lianliankan.util.ImageUtils;
import com.lianliankan.util.ResourcePath;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class LevelSelectPanel extends JPanel {
    private MainFrame mainFrame;
    private BufferedImage bgImage;
    private JButton btnLevel1;
    private JButton btnLevel2;
    private JButton btnLevel3;
    private JButton btnBack;

    public LevelSelectPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);
        bgImage = ImageUtils.loadImage(ResourcePath.LEVEL_BG);

        int btnW = 120;
        int btnH = 50;
        int gapX = 200;
        int centerX = 400 - btnW / 2;
        int btnY = 320;
        
        btnLevel1 = createLevelButton("关卡 1", centerX - gapX, btnY, btnW, btnH);
        btnLevel2 = createLevelButton("关卡 2", centerX, btnY, btnW, btnH);
        btnLevel3 = createLevelButton("关卡 3", centerX + gapX, btnY, btnW, btnH);
        btnBack = createButton("返回", 400 - btnW / 2, 500, btnW, btnH);

        btnLevel1.addActionListener(e -> startLevel(1));
        btnLevel2.addActionListener(e -> startLevel(2));
        btnLevel3.addActionListener(e -> JOptionPane.showMessageDialog(this, "请等待VIP1解锁", "提示", JOptionPane.INFORMATION_MESSAGE));
        btnBack.addActionListener(e -> mainFrame.showMainPanel());

        add(btnLevel1);
        add(btnLevel2);
        add(btnLevel3);
        add(btnBack);
    }

    private void startLevel(int level) {
        mainFrame.showGamePanel(2, level);
    }

    private JButton createLevelButton(String text, int x, int y, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setFont(new Font("宋体", Font.BOLD, 16));
        return btn;
    }

    private JButton createButton(String text, int x, int y, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setFont(new Font("宋体", Font.PLAIN, 14));
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, 800, 600, this);
        } else {
            g.setColor(new Color(70, 130, 180));
            g.fillRect(0, 0, 800, 600);
        }
    }
}
