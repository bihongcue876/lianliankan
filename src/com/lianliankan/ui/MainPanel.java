package com.lianliankan.ui;

import com.lianliankan.audio.AudioManager;
import com.lianliankan.model.SettingsState;
import com.lianliankan.util.ImageUtils;
import com.lianliankan.util.ResourcePath;
import com.lianliankan.util.UIUtils;

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

        btnBasic = UIUtils.createButton("普通模式", 15, 230, 120, 45);
        btnEndless = UIUtils.createButton("休闲模式", 15, 340, 120, 45);
        btnStage = UIUtils.createButton("关卡模式", 15, 450, 120, 45);
        btnContinue = UIUtils.createButton("继续游戏", 650, 320, 100, 35);
        btnHelp = UIUtils.createButton("帮助", 650, 365, 100, 35);
        btnRank = UIUtils.createButton("排行榜", 650, 410, 100, 35);
        btnSettings = UIUtils.createButton("设置", 650, 455, 100, 35);
        btnExit = UIUtils.createButton("退出游戏", 650, 500, 100, 35);

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
        
        java.util.List<String> availableModes = new java.util.ArrayList<>();
        java.util.List<Integer> availableModeIndices = new java.util.ArrayList<>();
        String[] modeNames = {"普通模式", "休闲模式", "关卡模式"};
        
        for (int i = 0; i < 3; i++) {
            String saveFile = ResourcePath.getSaveFile(i);
            if (ResourcePath.exists(saveFile)) {
                availableModes.add(modeNames[i]);
                availableModeIndices.add(i);
            }
        }
        
        if (availableModes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有找到存档");
            return;
        }
        
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "选择存档", true);
        dlg.setSize(300, 250);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JList<String> list = new JList<>(availableModes.toArray(new String[0]));
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
                int modeIndex = availableModeIndices.get(idx);
                mainFrame.showGamePanel(modeIndex);
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
        new HelpDialog((Frame) SwingUtilities.getWindowAncestor(this)).setVisible(true);
    }

    private void showRank() {
        new HighScorePanel((Frame) SwingUtilities.getWindowAncestor(this)).setVisible(true);
    }

    private void showSettings() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "设置", true);
        dlg.setSize(380, 250);
        dlg.setLocationRelativeTo(this);
        JPanel contentPanel = new JPanel(new GridLayout(4, 1, 0, 8));
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

        JPanel bgmVolumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel bgmVolumeLabel = new JLabel("背景音乐：");
        bgmVolumeLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        bgmVolumePanel.add(bgmVolumeLabel);
        JSlider bgmVolumeSlider = new JSlider(0, 100, currentSettings.getBgmVolume());
        bgmVolumeSlider.setPreferredSize(new Dimension(150, 30));
        bgmVolumePanel.add(bgmVolumeSlider);
        JLabel bgmVolumeValueLabel = new JLabel(currentSettings.getBgmVolume() + "%");
        bgmVolumeValueLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        bgmVolumePanel.add(bgmVolumeValueLabel);
        bgmVolumeSlider.addChangeListener(e -> bgmVolumeValueLabel.setText(bgmVolumeSlider.getValue() + "%"));

        JPanel sfxVolumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel sfxVolumeLabel = new JLabel("音效：    ");
        sfxVolumeLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        sfxVolumePanel.add(sfxVolumeLabel);
        JSlider sfxVolumeSlider = new JSlider(0, 100, currentSettings.getSfxVolume());
        sfxVolumeSlider.setPreferredSize(new Dimension(150, 30));
        sfxVolumePanel.add(sfxVolumeSlider);
        JLabel sfxVolumeValueLabel = new JLabel(currentSettings.getSfxVolume() + "%");
        sfxVolumeValueLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        sfxVolumePanel.add(sfxVolumeValueLabel);
        sfxVolumeSlider.addChangeListener(e -> sfxVolumeValueLabel.setText(sfxVolumeSlider.getValue() + "%"));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveBtn = new JButton("保存");
        JButton cancelBtn = new JButton("取消");
        saveBtn.setFont(new Font("宋体", Font.PLAIN, 14));
        cancelBtn.setFont(new Font("宋体", Font.PLAIN, 14));
        saveBtn.addActionListener(e -> {
            SettingsState settings = new SettingsState(
                themeCombo.getSelectedIndex(), 
                bgmVolumeSlider.getValue(),
                sfxVolumeSlider.getValue()
            );
            saveSettings(settings);
            AudioManager.setBgmVolume(settings.getBgmVolume());
            AudioManager.setSfxVolume(settings.getSfxVolume());
            String[] themes = {"fruit", "cxk", "mh"};
            AudioManager.setTheme(themes[settings.getThemeIndex()]);
            dlg.dispose();
        });
        cancelBtn.addActionListener(e -> dlg.dispose());
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        contentPanel.add(themePanel);
        contentPanel.add(bgmVolumePanel);
        contentPanel.add(sfxVolumePanel);
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
        return new SettingsState(0, 80, 80);
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) g.drawImage(bgImage, 0, 0, 800, 600, this);
    }
}
