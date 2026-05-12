package com.lianliankan.ui;

import com.lianliankan.model.ScoreRecord;
import com.lianliankan.util.ResourcePath;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class HighScorePanel extends JDialog {
    private static final String[] MODE_NAMES = {"普通模式", "休闲模式", "关卡模式"};

    public HighScorePanel(Frame parent) {
        super(parent, "排行榜", true);
        setSize(600, 450);
        setLocationRelativeTo(parent);
        setResizable(false);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("宋体", Font.BOLD, 14));
        for (int i = 0; i < 3; i++) {
            tabbedPane.addTab(MODE_NAMES[i], createScoreTable(i));
        }

        add(tabbedPane, BorderLayout.CENTER);

        JButton btnClose = new JButton("关闭");
        btnClose.setFont(new Font("宋体", Font.PLAIN, 14));
        btnClose.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JScrollPane createScoreTable(int mode) {
        String[] columns = {"排名", "姓名", "分数", "时间/阶段", "日期"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        List<ScoreRecord> records = loadScores(mode);
        for (int i = 0; i < records.size(); i++) {
            ScoreRecord r = records.get(i);
            String timeOrStage = (mode == 2) ? "Stage " + r.getStage() : formatTime(r.getTime());
            model.addRow(new Object[]{i + 1, r.getName(), r.getScore().toString(), timeOrStage, r.getDate()});
        }

        JTable table = new JTable(model);
        table.setFont(new Font("宋体", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 14));
        table.getTableHeader().setReorderingAllowed(false);
        return new JScrollPane(table);
    }

    private List<ScoreRecord> loadScores(int mode) {
        String path = ResourcePath.getHighScoreFile(mode);
        File file = new File(path);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            List<ScoreRecord> list = (List<ScoreRecord>) ois.readObject();
            list.sort((a, b) -> b.getScore().compareTo(a.getScore()));
            return list.size() > 10 ? list.subList(0, 10) : list;
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private String formatTime(long seconds) {
        long m = seconds / 60;
        long s = seconds % 60;
        return String.format("%d分%02d秒", m, s);
    }

    public static void saveScore(ScoreRecord record) {
        int mode = record.getMode();
        String path = ResourcePath.getHighScoreFile(mode);
        File file = new File(path);
        file.getParentFile().mkdirs();

        List<ScoreRecord> records = new ArrayList<>();
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                @SuppressWarnings("unchecked")
                List<ScoreRecord> list = (List<ScoreRecord>) ois.readObject();
                records.addAll(list);
            } catch (IOException | ClassNotFoundException e) {
            }
        }

        records.add(record);
        records.sort((a, b) -> b.getScore().compareTo(a.getScore()));
        if (records.size() > 10) {
            records = new ArrayList<>(records.subList(0, 10));
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(records);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isHighScore(BigInteger score, int mode) {
        String path = ResourcePath.getHighScoreFile(mode);
        File file = new File(path);
        if (!file.exists()) return true;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            List<ScoreRecord> list = (List<ScoreRecord>) ois.readObject();
            if (list.size() < 10) return true;
            list.sort((a, b) -> b.getScore().compareTo(a.getScore()));
            return score.compareTo(list.get(list.size() - 1).getScore()) > 0;
        } catch (IOException | ClassNotFoundException e) {
            return true;
        }
    }
}
