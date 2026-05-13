package com.lianliankan.ui;

import com.lianliankan.util.ImageUtils;
import com.lianliankan.util.ResourcePath;

import javax.swing.*;
import java.awt.*;

public class HelpDialog extends JDialog {
    public HelpDialog(Frame parent) {
        super(parent, "欢乐连连看 - 游戏规则", true);
        setSize(650, 750);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        addSectionTitle(contentPanel, "基本消除规则");
        addSeparator(contentPanel);
        addParagraph(contentPanel, "点击两个相同的图样，如果它们可以无阻碍通过带不多于两个拐的连接，则可以消除。");
        
        addSubTitle(contentPanel, "连线规则图示：");
        addImageRow(contentPanel, 
            new String[]{"straight_link_1.png", "straight_link_2.png"},
            new String[]{"直线连接示例1", "直线连接示例2"},
            "直线连接：两个相同图样在同一行或同一列，中间无阻挡");
        addImageRow(contentPanel, 
            new String[]{"one_corner_link_1.png", "one_corner_link_2.png"},
            new String[]{"一个拐角连接示例1", "一个拐角连接示例2"},
            "一个拐角连接：通过一个拐角可以连通");
        addImageRow(contentPanel, 
            new String[]{"two_corner_link_1.png", "two_corner_link_2.png"},
            new String[]{"两个拐角连接示例1", "两个拐角连接示例2"},
            "两个拐角连接：通过两个拐角可以连通");

        addSectionTitle(contentPanel, "三个游戏模式");
        
        addSubTitle(contentPanel, "休闲模式");
        addParagraph(contentPanel, "不限时，消除一对+10分，无连击系统，可以选择不同的主题和复杂度，可获得不同加分。");

        addSubTitle(contentPanel, "普通模式");
        addParagraph(contentPanel, "限时，设计为20阶段，正常情况下消除一对+10分，2秒内连续消除触发连击（分数翻倍），连击倍率：5连以下=1.5倍，5连以上=3.0倍。");

        addSubTitle(contentPanel, "关卡模式");
        addParagraph(contentPanel, "有时间限制，每个关卡包含5个阶段，难度递增。关卡1表示篮球与鸡为底色，关卡2讲述怪物猎人的故事，……后续关卡正在开发中……通关得分 = 剩余秒数×10 + 消除对数×5。");
        addImageWithCaption(contentPanel, "level_select.png", "关卡选择界面");

        addSectionTitle(contentPanel, "游戏辅助功能");
        
        addSubTitle(contentPanel, "提示");
        addParagraph(contentPanel, "找到一对可消除的图片并高亮显示，普通模式：扣10秒。");

        addSubTitle(contentPanel, "重排");
        addParagraph(contentPanel, "重新排列剩余图片，普通模式：扣15秒，休闲模式：扣5分。");

        addSubTitle(contentPanel, "暂停");
        addParagraph(contentPanel, "暂停游戏，普通模式暂停时倒计时停止。");

        addSectionTitle(contentPanel, "界面说明");
        addImageWithCaption(contentPanel, "main_interface.png", "主界面");
        addImageWithCaption(contentPanel, "game_interface.png", "游戏界面");

        addSectionTitle(contentPanel, "其他帮助");
        addParagraph(contentPanel, "在设置中可以设置音量，可以续关，可以断关，有排行榜，记录历次最高得分。");

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnClose = new JButton("关闭");
        btnClose.setFont(new Font("宋体", Font.PLAIN, 14));
        btnClose.addActionListener(e -> dispose());
        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addSectionTitle(JPanel panel, String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("微软雅黑", Font.BOLD, 18));
        label.setForeground(new Color(0, 100, 180));
        label.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
    }

    private void addSubTitle(JPanel panel, String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("微软雅黑", Font.BOLD, 14));
        label.setForeground(new Color(50, 50, 50));
        label.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
    }

    private void addParagraph(JPanel panel, String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(new Font("宋体", Font.PLAIN, 13));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        textArea.setMaximumSize(new Dimension(590, Integer.MAX_VALUE));
        panel.add(textArea);
        panel.add(Box.createVerticalStrut(8));
    }

    private void addImageRow(JPanel panel, String[] imageNames, String[] captions, String description) {
        int targetHeight = 120;
        java.awt.image.BufferedImage[] images = new java.awt.image.BufferedImage[2];
        int[] widths = new int[2];
        
        for (int i = 0; i < 2; i++) {
            String imagePath = ResourcePath.getHelpImage(imageNames[i]);
            images[i] = ImageUtils.loadImage(imagePath);
            if (images[i] != null) {
                int origWidth = images[i].getWidth();
                int origHeight = images[i].getHeight();
                widths[i] = (int) ((double) targetHeight / origHeight * origWidth);
            }
        }

        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
        rowPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        rowPanel.setMaximumSize(new Dimension(590, targetHeight + 40));

        rowPanel.add(Box.createHorizontalGlue());
        
        for (int i = 0; i < 2; i++) {
            if (images[i] != null) {
                JPanel imgPanel = new JPanel();
                imgPanel.setLayout(new BoxLayout(imgPanel, BoxLayout.Y_AXIS));
                
                ImageIcon icon = new ImageIcon(images[i].getScaledInstance(widths[i], targetHeight, Image.SCALE_SMOOTH));
                JLabel imageLabel = new JLabel(icon);
                imageLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
                imgPanel.add(imageLabel);
                
                JLabel captionLabel = new JLabel(captions[i]);
                captionLabel.setFont(new Font("宋体", Font.PLAIN, 11));
                captionLabel.setForeground(Color.GRAY);
                captionLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
                imgPanel.add(captionLabel);
                
                rowPanel.add(imgPanel);
            }
            
            if (i == 0) {
                rowPanel.add(Box.createHorizontalStrut(20));
            }
        }
        
        rowPanel.add(Box.createHorizontalGlue());
        panel.add(rowPanel);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("宋体", Font.PLAIN, 12));
        descLabel.setForeground(new Color(80, 80, 80));
        descLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        panel.add(descLabel);
        panel.add(Box.createVerticalStrut(10));
    }

    private void addImageWithCaption(JPanel panel, String imageName, String caption) {
        String imagePath = ResourcePath.getHelpImage(imageName);
        java.awt.image.BufferedImage image = ImageUtils.loadImage(imagePath);
        
        if (image != null) {
            int maxWidth = 550;
            int origWidth = image.getWidth();
            int origHeight = image.getHeight();
            int displayWidth = Math.min(origWidth, maxWidth);
            int displayHeight = (int) ((double) displayWidth / origWidth * origHeight);
            
            ImageIcon icon = new ImageIcon(image.getScaledInstance(displayWidth, displayHeight, Image.SCALE_SMOOTH));
            JLabel imageLabel = new JLabel(icon);
            imageLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            panel.add(imageLabel);
        }

        JLabel captionLabel = new JLabel(caption);
        captionLabel.setFont(new Font("宋体", Font.PLAIN, 12));
        captionLabel.setForeground(Color.GRAY);
        captionLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        panel.add(captionLabel);
        panel.add(Box.createVerticalStrut(10));
    }

    private void addSeparator(JPanel panel) {
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(590, 1));
        separator.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        panel.add(Box.createVerticalStrut(5));
        panel.add(separator);
        panel.add(Box.createVerticalStrut(10));
    }
}
