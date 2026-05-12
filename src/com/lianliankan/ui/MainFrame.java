package com.lianliankan.ui;

import com.lianliankan.util.ResourcePath;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);
    private MainPanel mainPanel;
    private GamePanel gamePanel;
    private LevelSelectPanel levelSelectPanel;

    public MainFrame() {
        setTitle("欢乐连连看");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        checkResources();

        loadIcon();

        mainPanel = new MainPanel(this);
        gamePanel = new GamePanel(this);
        levelSelectPanel = new LevelSelectPanel(this);
        mainContainer.add(mainPanel, "Main");
        mainContainer.add(gamePanel, "Game");
        mainContainer.add(levelSelectPanel, "LevelSelect");
        add(mainContainer);

        cardLayout.show(mainContainer, "Main");
        pack();
        Insets insets = getInsets();
        setSize(800 + insets.left + insets.right, 600 + insets.top + insets.bottom);
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (gamePanel != null && gamePanel.isPlaying()) {
                    int opt = JOptionPane.showConfirmDialog(MainFrame.this, "是否保存游戏进度？", "退出", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (opt == JOptionPane.YES_OPTION) {
                        gamePanel.saveGameOnExit();
                    } else if (opt == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                }
                System.exit(0);
            }
        });
    }

    private void checkResources() {
        String[] required = {
            ResourcePath.getThemeBg("fruit"),
            ResourcePath.getThemeElement("fruit"),
            ResourcePath.getThemeMask("fruit"),
            ResourcePath.MAIN_BG
        };
        for (String path : required) {
            File f = new File(path);
            System.out.println("检查资源: " + path + " -> 绝对路径: " + f.getAbsolutePath() + " -> 存在: " + f.exists());
            if (!f.exists()) {
                JOptionPane.showMessageDialog(this,
                    "关键资源文件缺失: " + path + "\n绝对路径: " + f.getAbsolutePath(),
                    "错误", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
        System.out.println("所有资源检查通过");
    }

    private void loadIcon() {
        String iconPath = ResourcePath.ICON;
        File iconFile = new File(iconPath);
        
        if (iconFile.exists()) {
            try {
                Image img = javax.imageio.ImageIO.read(iconFile);
                if (img != null) {
                    setIconImage(img);
                    System.out.println("成功加载图标: " + iconFile.getAbsolutePath());
                    return;
                }
            } catch (Exception e) {
                System.out.println("加载图标失败: " + e.getMessage());
            }
        }
        System.out.println("未能加载图标文件: " + iconPath);
    }

    public void showMainPanel() {
        cardLayout.show(mainContainer, "Main");
        mainPanel.refreshContinueButton();
    }

    public void showLevelSelectPanel() {
        cardLayout.show(mainContainer, "LevelSelect");
    }

    public void showGamePanel() {
        cardLayout.show(mainContainer, "Game");
    }

    public void showGamePanel(int mode) {
        gamePanel.setGameMode(mode);
        String saveFile = ResourcePath.getSaveFile(mode);
        if (ResourcePath.exists(saveFile)) {
            int opt = JOptionPane.showConfirmDialog(this,
                "检测到上次存档，是否继续？",
                "继续游戏", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                gamePanel.loadAndResume(saveFile);
                cardLayout.show(mainContainer, "Game");
                return;
            }
        }
        if (gamePanel.startNewGame(mode)) {
            cardLayout.show(mainContainer, "Game");
        }
    }

    public void showGamePanel(int mode, int level) {
        gamePanel.setGameMode(mode);
        gamePanel.setLevel(level);
        if (gamePanel.startNewGame(mode)) {
            cardLayout.show(mainContainer, "Game");
        }
    }
}
