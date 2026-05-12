package com.lianliankan.ui;

import com.lianliankan.model.SettingsState;
import com.lianliankan.util.ImageUtils;
import com.lianliankan.util.ResourcePath;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class MainPanel extends JPanel {
    private MainFrame mainFrame;
    private BufferedImage bgImage;
    private JButton btnBasic;
    private JButton btnEndless;
    private JButton btnStage;
    private JButton btnContinue;
    private JButton btnHelp;
    private JButton btnRank;
    private JButton btnSettings;
    private JButton btnExit;

    public MainPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);
        bgImage = ImageUtils.loadImage(ResourcePath.MAIN_BG);

        btnBasic = createButton("普通模式", 15, 230, 120, 45);
        btnEndless = createButton("休闲模式", 15, 340, 120, 45);
        btnStage = createButton("关卡模式", 15, 450, 120, 45);
        btnContinue = createButton("继续游戏", 650, 320, 100, 35);
        btnHelp = createButton("帮助", 650, 365, 100, 35);
        btnRank = createButton("排行榜", 650, 410, 100, 35);
        btnSettings = createButton("设置", 650, 455, 100, 35);
        btnExit = createButton("退出游戏", 650, 500, 100, 35);

        btnBasic.addActionListener(e -> mainFrame.showGamePanel(0));
        btnEndless.addActionListener(e -> mainFrame.showGamePanel(1));
        btnStage.addActionListener(e -> mainFrame.showLevelSelectPanel());
        btnContinue.addActionListener(e -> handleContinue());
        btnHelp.addActionListener(e -> showHelp());
        btnRank.addActionListener(e -> showRank());
        btnSettings.addActionListener(e -> showSettings());
        btnExit.addActionListener(e -> System.exit(0));

        add(btnBasic);
        add(btnEndless);
        add(btnStage);
        add(btnContinue);
        add(btnHelp);
        add(btnRank);
        add(btnSettings);
        add(btnExit);

        refreshContinueButton();
    }

    private void handleContinue() {
        if (!ResourcePath.hasAnySaveFile()) {
            JOptionPane.showMessageDialog(this, "没有找到存档");
            return;
        }
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "选择存档", true);
        dlg.setSize(300, 250);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        String[] modes = {"基本模式", "休闲模式", "关卡模式"};
        JList<String> list = new JList<>(modes);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(new Font("宋体", Font.PLAIN, 16));
        list.setFixedCellHeight(40);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("宋体", Font.PLAIN, 16));
                return label;
            }
        });
        dlg.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton ok = new JButton("确定");
        JButton cancel = new JButton("取消");
        ok.setFont(new Font("宋体", Font.PLAIN, 14));
        cancel.setFont(new Font("宋体", Font.PLAIN, 14));
        ok.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx >= 0) {
                String saveFile = ResourcePath.getSaveFile(idx);
                if (ResourcePath.exists(saveFile)) {
                    mainFrame.showGamePanel(idx);
                } else {
                    JOptionPane.showMessageDialog(dlg, "该模式没有存档");
                }
            }
            dlg.dispose();
        });
        cancel.addActionListener(e -> dlg.dispose());
        btnPanel.add(ok);
        btnPanel.add(cancel);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void showHelp() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "游戏规则", true);
        dlg.setSize(500, 400);
        dlg.setLocationRelativeTo(this);
        JTextArea text = new JTextArea(
            "欢乐连连看游戏规则\n\n" +
            "1. 点击两张相同的图片，如果它们可以通过不超过2个拐角的路径连接，则消除。\n" +
            "2. 基本模式：不限时，消除一对+10分，无连击。\n" +
            "3. 休闲模式（无尽）：不限时，消除一对+10分，2秒内连续消除触发连击（分数翻倍）。\n" +
            "4. 关卡模式：限时，共20关，难度递增，通关得分=剩余秒数×10+消除对数×5。\n" +
            "5. 提示：找到一对可消除的图片，普通模式扣10秒，无尽模式中断连击。\n" +
            "6. 重排：重新排列剩余图片，普通模式扣15秒，无尽模式扣5分。\n" +
            "7. 暂停：暂停游戏，普通模式暂停时倒计时停止。"
        );
        text.setEditable(false);
        text.setFont(new Font("宋体", Font.PLAIN, 16));
        text.setMargin(new Insets(10, 10, 10, 10));
        dlg.add(new JScrollPane(text), BorderLayout.CENTER);
        JButton close = new JButton("关闭");
        close.setFont(new Font("宋体", Font.PLAIN, 14));
        close.addActionListener(e -> dlg.dispose());
        JPanel bp = new JPanel();
        bp.add(close);
        dlg.add(bp, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void showRank() {
        new HighScorePanel((Frame) SwingUtilities.getWindowAncestor(this)).setVisible(true);
    }

    private void showSettings() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "设置", true);
        dlg.setSize(350, 200);
        dlg.setLocationRelativeTo(this);
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        SettingsState currentSettings = loadSettings();

        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel themeLabel = new JLabel("主题：");
        themeLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        themePanel.add(themeLabel);
        JComboBox<String> themeCombo = new JComboBox<>(new String[]{"水果", "CXK", "怪物猎人"});
        themeCombo.setFont(new Font("宋体", Font.PLAIN, 14));
        themeCombo.setSelectedIndex(currentSettings.getThemeIndex());
        themePanel.add(themeCombo);

        JPanel volumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel volumeLabel = new JLabel("音量：");
        volumeLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        volumePanel.add(volumeLabel);
        JSlider volumeSlider = new JSlider(0, 100, currentSettings.getVolume());
        volumeSlider.setPreferredSize(new Dimension(150, 30));
        volumePanel.add(volumeSlider);
        JLabel volumeValueLabel = new JLabel(currentSettings.getVolume() + "%");
        volumeValueLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        volumePanel.add(volumeValueLabel);
        volumeSlider.addChangeListener(e -> volumeValueLabel.setText(volumeSlider.getValue() + "%"));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveBtn = new JButton("保存");
        JButton cancelBtn = new JButton("取消");
        saveBtn.setFont(new Font("宋体", Font.PLAIN, 14));
        cancelBtn.setFont(new Font("宋体", Font.PLAIN, 14));
        saveBtn.addActionListener(e -> {
            SettingsState settings = new SettingsState(themeCombo.getSelectedIndex(), volumeSlider.getValue());
            saveSettings(settings);
            dlg.dispose();
        });
        cancelBtn.addActionListener(e -> dlg.dispose());
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        contentPanel.add(themePanel);
        contentPanel.add(volumePanel);
        contentPanel.add(btnPanel);
        dlg.add(contentPanel);
        dlg.setVisible(true);
    }

    private SettingsState loadSettings() {
        String settingsFile = ResourcePath.getSettingsFile();
        File file = new File(settingsFile);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(settingsFile))) {
                return (SettingsState) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new SettingsState(0, 80);
    }

    private void saveSettings(SettingsState settings) {
        String settingsFile = ResourcePath.getSettingsFile();
        File dir = new File(ResourcePath.SAVE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(settingsFile))) {
            oos.writeObject(settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshContinueButton() {
        btnContinue.setEnabled(ResourcePath.hasAnySaveFile());
    }

    private JButton createButton(String text, int x, int y, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) g.drawImage(bgImage, 0, 0, 800, 600, this);
    }
}
