package com.lianliankan.ui;

import com.lianliankan.audio.AudioManager;
import com.lianliankan.back.GameControl;
import com.lianliankan.back.Vertex;
import com.lianliankan.model.GameState;
import com.lianliankan.model.ScoreRecord;
import com.lianliankan.util.ImageUtils;
import com.lianliankan.util.ResourcePath;
import com.lianliankan.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GamePanel extends JPanel {
    private MainFrame mainFrame;
    private GameControl gameControl = new GameControl();

    private BufferedImage memDCBg;
    private BufferedImage[] elementImages;

    private Point m_ptGameTop = new Point(30, 50);
    private int m_elemW = 35;
    private int m_elemH = 35;

    private Vertex m_svSelFst;
    private Vertex m_svSelSec;
    private boolean m_bFirstPoint = true;
    private volatile boolean m_bPlaying = false;
    private volatile boolean m_bPaused = false;

    private List<Vertex> linkPath = new ArrayList<>();
    private Vertex[] hintPath = null;

    private int m_nGameMode = 0;
    private int m_nLevel = 1;
    private int m_nStage = 1;
    private int m_nDifficulty = 0;
    private int m_nEndlessBoardSize = 0;
    private int m_nEndlessTheme = 0;
    private int m_nScore = 0;
    private BigInteger m_bigScore = BigInteger.ZERO;
    private int m_nCombo = 0;
    private int m_nMaxCombo = 0;
    private long m_nLastClearTime = 0;
    private long m_nRemainTime = 0;
    private int m_nClearCount = 0;
    private int m_nCurrentRows = 10;
    private int m_nCurrentCols = 16;
    private int m_nCurrentPicNum = 16;
    private String m_strCurrentTheme = "fruit";
    private int m_nThemeType = 0;
    private Color m_themeLabelColor = new Color(0, 0, 139);
    private int m_themeLabelFontSize = 14;

    private JButton btnStart;
    private JButton btnPause;
    private JButton btnPrompt;
    private JButton btnReset;
    private JButton btnHelp;
    private JButton btnBack;
    private JButton btnRestart;
    private JButton btnGiveUp;
    private JButton btnSettings;

    private JLabel lblScore;
    private JLabel lblCombo;
    private JLabel lblTime;
    private JLabel lblLevel;
    private JLabel lblTitle;
    private JLabel lblComboDisplay;

    private Timer countdownTimer;
    private Timer hintBlinkTimer;
    private Timer comboDisplayTimer;
    private Vertex[] hintVertices;
    private int hintBlinkCount = 0;
    private int comboDisplayAlpha = 0;

    private static final int MODE_BASIC = 0;
    private static final int MODE_ENDLESS = 1;
    private static final int MODE_STAGE = 2;

    private static final int DIFFICULTY_NORMAL = 0;

    private static final int THEME_FRUIT = 0;
    private static final int THEME_CXK = 1;
    private static final int THEME_MH = 2;

    private static final int[][] STAGE_PARAMS = {
        {8, 10, 8},
        {8, 10, 10},
        {10, 12, 12},
        {10, 14, 14},
        {12, 16, 16}
    };

    public GamePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);

        loadTheme("fruit");

        btnStart = UIUtils.createButton("开始游戏", 650, 50, 100, 35);
        btnPause = UIUtils.createButton("暂停", 650, 95, 100, 35);
        btnPrompt = UIUtils.createButton("提示", 650, 140, 100, 35);
        btnReset = UIUtils.createButton("重排", 650, 185, 100, 35);
        btnRestart = UIUtils.createButton("重新开始", 650, 230, 100, 35);
        btnHelp = UIUtils.createButton("帮助", 650, 275, 100, 35);
        btnGiveUp = UIUtils.createButton("放弃游戏", 650, 320, 100, 35);
        btnSettings = UIUtils.createButton("设置", 650, 365, 100, 35);
        btnBack = UIUtils.createButton("返回", 650, 520, 100, 35);

        lblTitle = new JLabel("欢乐连连看");
        lblTitle.setBounds(200, 10, 200, 30);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("隶书", Font.BOLD, 20));

        lblScore = new JLabel("分数: 0");
        lblScore.setBounds(650, 410, 100, 25);
        lblScore.setHorizontalAlignment(SwingConstants.CENTER);
        lblScore.setFont(new Font("黑体", Font.BOLD, 14));

        lblCombo = new JLabel("连击: 0");
        lblCombo.setBounds(650, 440, 100, 25);
        lblCombo.setHorizontalAlignment(SwingConstants.CENTER);
        lblCombo.setFont(new Font("黑体", Font.BOLD, 14));

        lblTime = new JLabel("时间: --");
        lblTime.setBounds(650, 470, 100, 25);
        lblTime.setHorizontalAlignment(SwingConstants.CENTER);
        lblTime.setFont(new Font("黑体", Font.BOLD, 14));

        lblLevel = new JLabel("关卡: 1");
        lblLevel.setBounds(650, 500, 100, 25);
        lblLevel.setHorizontalAlignment(SwingConstants.CENTER);
        lblLevel.setFont(new Font("黑体", Font.BOLD, 14));

        lblComboDisplay = new JLabel("", SwingConstants.CENTER);
        lblComboDisplay.setBounds(30, 520, 600, 40);
        lblComboDisplay.setFont(new Font("黑体", Font.BOLD, 22));
        lblComboDisplay.setForeground(new Color(255, 100, 0));

        btnStart.addActionListener(e -> startGame());
        btnPause.addActionListener(e -> togglePause());
        btnPrompt.addActionListener(e -> doHint());
        btnReset.addActionListener(e -> doReset());
        btnRestart.addActionListener(e -> restartGame());
        btnHelp.addActionListener(e -> showHelp());
        btnGiveUp.addActionListener(e -> giveUpGame());
        btnSettings.addActionListener(e -> showGameSettings());
        btnBack.addActionListener(e -> backToMain());

        btnPause.setEnabled(false);
        btnPrompt.setEnabled(false);
        btnReset.setEnabled(false);
        btnRestart.setEnabled(false);
        btnGiveUp.setEnabled(false);
        btnSettings.setEnabled(false);

        add(btnStart);
        add(btnPause);
        add(btnPrompt);
        add(btnReset);
        add(btnRestart);
        add(btnHelp);
        add(btnGiveUp);
        add(btnSettings);
        add(btnBack);
        add(lblTitle);
        add(lblScore);
        add(lblCombo);
        add(lblTime);
        add(lblLevel);
        add(lblComboDisplay);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                handleClick(e.getPoint());
            }
        });
    }

    private void loadTheme(String theme) {
        memDCBg = ImageUtils.loadImage(ResourcePath.getThemeBg(theme));
        BufferedImage elemBmp = ImageUtils.loadImage(ResourcePath.getThemeElement(theme));
        BufferedImage maskBmp = ImageUtils.loadImage(ResourcePath.getThemeMask(theme));
        if (elemBmp != null && maskBmp != null) {
            int numElements = maskBmp.getHeight() / 40;
            elementImages = ImageUtils.buildElementImages(elemBmp, maskBmp, numElements);
        }
        if ("fruit".equals(theme)) {
            m_themeLabelColor = new Color(0, 0, 139);
            m_themeLabelFontSize = 14;
        } else if ("mh".equals(theme)) {
            m_themeLabelColor = Color.WHITE;
            m_themeLabelFontSize = 18;
        } else {
            m_themeLabelColor = Color.BLACK;
            m_themeLabelFontSize = 14;
        }
    }

    public void setGameMode(int mode) {
        m_nGameMode = mode;
    }

    public void setLevel(int level) {
        m_nLevel = level;
    }

    public boolean isPlaying() {
        return m_bPlaying;
    }

    public boolean startNewGame(int mode) {
        m_nGameMode = mode;
        if (mode == MODE_BASIC) {
            if (!showDifficultyDialog()) {
                return false;
            }
            setupBasicMode(1);
        } else if (mode == MODE_ENDLESS) {
            if (!showEndlessSettingsDialog()) {
                return false;
            }
            setupEndlessMode();
        } else {
            setupStageMode(m_nLevel);
        }
        enterGame();
        return true;
    }

    private boolean showDifficultyDialog() {
        String[] options = {"Normal", "Hard"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "请选择难度",
            "普通模式",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        if (choice == JOptionPane.CLOSED_OPTION) {
            return false;
        }
        m_nDifficulty = choice;
        return true;
    }

    private boolean showEndlessSettingsDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "休闲模式设置", true);
        dlg.setSize(350, 250);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] themes = {"水果 (基础)", "CXK (额外主题 2.5倍)", "MH (额外主题 2.5倍)"};
        String[] boardSizes = {"12×8", "12×10", "14×16"};
        String[] picNums = {"8种棋子", "10种棋子", "14种棋子", "16种棋子", "20种棋子"};
        
        JComboBox<String> themeCombo = new JComboBox<>(themes);
        JComboBox<String> boardCombo = new JComboBox<>(boardSizes);
        JComboBox<String> picCombo = new JComboBox<>(picNums);
        JLabel picLabel = new JLabel("棋子数量:");
        JLabel infoLabel = new JLabel("<html>额外主题只有8种元素<br>棋盘越大加成越高</html>");
        
        panel.add(new JLabel("主题:"));
        panel.add(themeCombo);
        panel.add(new JLabel("棋盘规格:"));
        panel.add(boardCombo);
        panel.add(picLabel);
        panel.add(picCombo);
        panel.add(new JLabel(""));
        panel.add(infoLabel);
        
        themeCombo.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                boolean isFruit = themeCombo.getSelectedIndex() == 0;
                picLabel.setEnabled(isFruit);
                picCombo.setEnabled(isFruit);
                if (!isFruit) {
                    picCombo.setSelectedIndex(0);
                }
            }
        });
        
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton okBtn = new JButton("确定");
        JButton cancelBtn = new JButton("取消");
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        
        final boolean[] confirmed = {false};
        okBtn.addActionListener(e -> {
            confirmed[0] = true;
            dlg.dispose();
        });
        cancelBtn.addActionListener(e -> dlg.dispose());
        
        dlg.add(panel, BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
        
        if (!confirmed[0]) {
            return false;
        }
        
        m_nEndlessTheme = themeCombo.getSelectedIndex();
        m_nEndlessBoardSize = boardCombo.getSelectedIndex();
        if (m_nEndlessTheme == 0) {
            m_nCurrentPicNum = new int[]{8, 10, 14, 16, 20}[picCombo.getSelectedIndex()];
        } else {
            m_nCurrentPicNum = 8;
        }
        return true;
    }

    public void loadAndResume(String saveFile) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
            GameState state = (GameState) ois.readObject();
            m_nGameMode = state.getMode();
            m_nLevel = state.getLevel();
            m_nStage = state.getStage();
            m_nScore = state.getScore();
            m_bigScore = state.getBigScore();
            m_nCombo = state.getCombo();
            m_nMaxCombo = state.getMaxCombo();
            m_nLastClearTime = state.getLastClearTime();
            m_nRemainTime = state.getRemainTime();
            m_nCurrentRows = state.getRows();
            m_nCurrentCols = state.getCols();
            m_nCurrentPicNum = state.getPicNum();
            gameControl.setGameMap(state.getMap());
            enterGame();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            startNewGame(m_nGameMode);
        }
    }

    private void setupBasicMode(int stage) {
        m_nLevel = stage;
        int[] params = STAGE_PARAMS[stage - 1];
        m_nCurrentRows = params[0];
        m_nCurrentCols = params[1];
        m_nCurrentPicNum = params[2];
        int totalCells = m_nCurrentRows * m_nCurrentCols;
        if (m_nDifficulty == DIFFICULTY_NORMAL) {
            m_nRemainTime = 180 + (totalCells - 80) * 3 / 2;
        } else {
            m_nRemainTime = 120 + (totalCells - 80) * 5 / 4;
        }
        gameControl.startGame(m_nCurrentRows, m_nCurrentCols, m_nCurrentPicNum);
        m_nCombo = 0;
        m_nMaxCombo = 0;
        m_nClearCount = 0;
        lblTitle.setText("阶段 " + stage + " - 普通模式");
        updateLabels();
    }

    private void setupEndlessMode() {
        int[][] boardParams = {{12, 8}, {12, 10}, {14, 16}};
        
        m_nCurrentRows = boardParams[m_nEndlessBoardSize][0];
        m_nCurrentCols = boardParams[m_nEndlessBoardSize][1];
        
        m_nThemeType = m_nEndlessTheme;
        if (m_nEndlessTheme == THEME_FRUIT) {
            m_strCurrentTheme = "fruit";
        } else if (m_nEndlessTheme == THEME_CXK) {
            m_strCurrentTheme = "cxk";
        } else {
            m_strCurrentTheme = "mh";
        }
        loadTheme(m_strCurrentTheme);
        
        gameControl.startGame(m_nCurrentRows, m_nCurrentCols, m_nCurrentPicNum);
        m_bigScore = BigInteger.ZERO;
        m_nCombo = 0;
        m_nMaxCombo = 0;
        m_nLastClearTime = 0;
        m_nClearCount = 0;
        m_nLevel = 1;
        
        String themeName = m_nEndlessTheme == THEME_FRUIT ? "水果" : (m_nEndlessTheme == THEME_CXK ? "CXK" : "MH");
        lblTitle.setText("休闲模式 " + themeName + " Lv.1");
        lblTime.setText("时间: --");
        updateLabels();
    }

    private void setupStageMode(int level) {
        m_nLevel = level;
        m_nStage = 1;
        setupStage();
    }

    private void setupStage() {
        int[] params = STAGE_PARAMS[m_nStage - 1];
        m_nCurrentRows = params[0];
        m_nCurrentCols = params[1];
        m_nCurrentPicNum = 8;
        
        int totalCells = m_nCurrentRows * m_nCurrentCols;
        m_nRemainTime = 150 + (totalCells - 80) * 11 / 8;
        
        if (m_nLevel == 1) {
            m_strCurrentTheme = "cxk";
        } else if (m_nLevel == 2) {
            m_strCurrentTheme = "mh";
        } else {
            m_strCurrentTheme = "fruit";
        }
        loadTheme(m_strCurrentTheme);
        
        gameControl.startGame(m_nCurrentRows, m_nCurrentCols, m_nCurrentPicNum);
        m_nCombo = 0;
        m_nMaxCombo = 0;
        m_nClearCount = 0;
        lblTitle.setText("关卡" + m_nLevel + " - 阶段" + m_nStage);
        updateLabels();
    }

    private void enterGame() {
        m_bPlaying = true;
        m_bPaused = false;
        m_bFirstPoint = true;
        m_svSelFst = null;
        m_svSelSec = null;
        hintVertices = null;

        centerBoard();
        
        AudioManager.setTheme(m_strCurrentTheme);
        AudioManager.init();
        if (!AudioManager.isPlaying()) {
            AudioManager.playBgm();
        }

        btnStart.setEnabled(false);
        btnPause.setEnabled(true);
        btnPrompt.setEnabled(true);
        btnReset.setEnabled(true);
        btnRestart.setEnabled(true);
        btnGiveUp.setEnabled(true);
        btnSettings.setEnabled(true);

        lblScore.setForeground(m_themeLabelColor);
        lblCombo.setForeground(m_themeLabelColor);
        lblLevel.setForeground(m_themeLabelColor);
        lblTitle.setForeground(m_themeLabelColor);

        lblScore.setFont(new Font("黑体", Font.BOLD, m_themeLabelFontSize));
        lblCombo.setFont(new Font("黑体", Font.BOLD, m_themeLabelFontSize));
        lblTime.setFont(new Font("黑体", Font.BOLD, m_themeLabelFontSize));
        lblLevel.setFont(new Font("黑体", Font.BOLD, m_themeLabelFontSize));

        updateLabels();
        repaint();

        if (m_nGameMode == MODE_STAGE || m_nGameMode == MODE_BASIC) {
            startCountdown();
        }
    }

    private void centerBoard() {
        int gameAreaWidth = 620;
        int gameAreaHeight = 510;
        int boardWidth = m_nCurrentCols * m_elemW;
        int boardHeight = m_nCurrentRows * m_elemH;
        m_ptGameTop.x = 30 + (gameAreaWidth - boardWidth) / 2;
        m_ptGameTop.y = 50 + (gameAreaHeight - boardHeight) / 2;
    }

    private void startGame() {
        startNewGame(m_nGameMode);
    }

    private void restartGame() {
        if (m_nGameMode == MODE_STAGE) {
            setupStage();
        } else if (m_nGameMode == MODE_ENDLESS) {
            setupEndlessMode();
        } else {
            setupBasicMode(m_nLevel);
        }
        enterGame();
    }

    private void togglePause() {
        if (!m_bPlaying) {
            return;
        }
        m_bPaused = !m_bPaused;
        if (m_bPaused) {
            btnPause.setText("继续");
            if (countdownTimer != null) {
                countdownTimer.stop();
            }
            JDialog pauseDlg = createPauseDialog();
            pauseDlg.setVisible(true);
        } else {
            btnPause.setText("暂停");
            if ((m_nGameMode == MODE_STAGE || m_nGameMode == MODE_BASIC) && m_nRemainTime > 0) {
                startCountdown();
            }
        }
    }

    private JDialog createPauseDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "暂停", true);
        dlg.setSize(250, 180);
        dlg.setLocationRelativeTo(this);
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lbl = new JLabel("游戏已暂停", SwingConstants.CENTER);
        lbl.setFont(new Font("宋体", Font.BOLD, 18));
        contentPanel.add(lbl);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton resume = new JButton("继续");
        JButton saveExit = new JButton("保存退出");
        resume.setFont(new Font("宋体", Font.PLAIN, 14));
        saveExit.setFont(new Font("宋体", Font.PLAIN, 14));
        resume.addActionListener(e -> {
            m_bPaused = false;
            btnPause.setText("暂停");
            dlg.dispose();
            if ((m_nGameMode == MODE_STAGE || m_nGameMode == MODE_BASIC) && m_nRemainTime > 0) {
                startCountdown();
            }
        });
        saveExit.addActionListener(e -> {
            saveGame();
            m_bPlaying = false;
            stopCountdown();
            AudioManager.stopBgm();
            dlg.dispose();
            mainFrame.showMainPanel();
        });
        btnPanel.add(resume);
        btnPanel.add(saveExit);
        contentPanel.add(btnPanel);
        dlg.add(contentPanel);
        return dlg;
    }

    private void doHint() {
        if (!m_bPlaying || m_bPaused) {
            return;
        }
        List<Vertex> path = gameControl.findMatchPath();
        if (path == null || path.size() < 2) {
            JOptionPane.showMessageDialog(this, "没有可消除的对");
            return;
        }
        hintVertices = new Vertex[]{path.get(0), path.get(path.size() - 1)};
        hintPath = path.toArray(new Vertex[0]);
        hintBlinkCount = 0;
        if (hintBlinkTimer != null) {
            hintBlinkTimer.stop();
        }
        hintBlinkTimer = new Timer(300, e -> {
            hintBlinkCount++;
            repaint();
            if (hintBlinkCount >= 8) {
                hintBlinkTimer.stop();
                hintVertices = null;
                hintPath = null;
                repaint();
            }
        });
        hintBlinkTimer.start();

        if (m_nGameMode == MODE_STAGE || m_nGameMode == MODE_BASIC) {
            m_nRemainTime = Math.max(0, m_nRemainTime - 10);
            updateTimeLabel();
        } else if (m_nGameMode == MODE_ENDLESS) {
            m_nCombo = 0;
            updateLabels();
        }
    }

    private void doReset() {
        if (!m_bPlaying || m_bPaused) {
            return;
        }
        executeReset();
    }

    private void executeReset() {
        gameControl.resetMap(m_nCurrentPicNum);
        applyResetPenalty();
        m_bFirstPoint = true;
        m_svSelFst = null;
        m_svSelSec = null;
        hintVertices = null;
        repaint();
    }

    private void applyResetPenalty() {
        if (m_nGameMode == MODE_ENDLESS) {
            m_bigScore = m_bigScore.max(BigInteger.ZERO).subtract(BigInteger.valueOf(5));
            m_nCombo = 0;
        } else if (m_nGameMode == MODE_STAGE || m_nGameMode == MODE_BASIC) {
            m_nRemainTime = Math.max(0, m_nRemainTime - 15);
            updateTimeLabel();
        }
        updateLabels();
    }

    private void handleClick(Point clickPoint) {
        if (!m_bPlaying || m_bPaused) {
            return;
        }
        if (clickPoint.x >= m_ptGameTop.x + m_nCurrentCols * m_elemW) {
            return;
        }

        int col = (clickPoint.x - m_ptGameTop.x) / m_elemW;
        int row = (clickPoint.y - m_ptGameTop.y) / m_elemH;
        int[][] map = gameControl.getGameMap();

        if (clickPoint.x < m_ptGameTop.x || clickPoint.y < m_ptGameTop.y) {
            if (!m_bFirstPoint) {
                m_bFirstPoint = true;
                m_svSelFst = null;
                linkPath.clear();
                repaint();
            }
            return;
        }
        if (clickPoint.x >= m_ptGameTop.x + m_nCurrentCols * m_elemW ||
            clickPoint.y >= m_ptGameTop.y + m_nCurrentRows * m_elemH) {
            if (!m_bFirstPoint) {
                m_bFirstPoint = true;
                m_svSelFst = null;
                linkPath.clear();
                repaint();
            }
            return;
        }

        if (map == null || row < 0 || row >= map.length || col < 0 || col >= map[0].length) {
            return;
        }
        if (map[row][col] == -1) {
            if (!m_bFirstPoint) {
                m_bFirstPoint = true;
                m_svSelFst = null;
                linkPath.clear();
                repaint();
            }
            return;
        }

        if (!m_bFirstPoint && m_svSelFst != null
            && m_svSelFst.row == row && m_svSelFst.col == col) {
            m_bFirstPoint = true;
            m_svSelFst = null;
            linkPath.clear();
            repaint();
            return;
        }

        AudioManager.playClick();

        if (m_bFirstPoint) {
            m_svSelFst = new Vertex(row, col, map[row][col]);
            m_bFirstPoint = false;
            linkPath.clear();
        } else {
            m_svSelSec = new Vertex(row, col, map[row][col]);
            List<Vertex> path = gameControl.link(m_svSelFst, m_svSelSec);
            if (path != null) {
                linkPath = path;
                m_nClearCount++;
                calculateScore();
                AudioManager.playClear();
                m_svSelFst = null;
                m_svSelSec = null;
                m_bFirstPoint = true;
                repaint();
                Timer clearLineTimer = new Timer(200, e -> {
                    linkPath.clear();
                    repaint();
                });
                clearLineTimer.setRepeats(false);
                clearLineTimer.start();
                if (gameControl.isWin()) {
                    handleWin();
                }
            } else {
                if (m_nGameMode == MODE_ENDLESS) {
                    m_bigScore = m_bigScore.max(BigInteger.ZERO).subtract(BigInteger.valueOf(5));
                } else {
                    m_nScore = Math.max(0, m_nScore - 5);
                }
                updateLabels();
                linkPath.clear();
                m_svSelFst = new Vertex(row, col, map[row][col]);
                m_svSelSec = null;
            }
        }
        repaint();
    }

    private void calculateScore() {
        long now = System.currentTimeMillis();
        if (now - m_nLastClearTime < 1500 && m_nLastClearTime > 0) {
            m_nCombo++;
        } else {
            m_nCombo = 1;
        }
        m_nMaxCombo = Math.max(m_nMaxCombo, m_nCombo);
        m_nLastClearTime = now;

        double comboMultiplier = m_nCombo > 5 ? 3.0 : 1.5;
        int baseScore = 10;
        
        if (m_nGameMode == MODE_ENDLESS) {
            double themeMultiplier = 1.0;
            if (m_nThemeType == THEME_CXK || m_nThemeType == THEME_MH) {
                themeMultiplier = 2.5;
            }
            
            double[] boardMultipliers = {1.0, 1.2, 1.4};
            double boardMultiplier = boardMultipliers[m_nEndlessBoardSize];
            
            int scoreGain = (int) (baseScore * comboMultiplier * themeMultiplier * boardMultiplier);
            m_bigScore = m_bigScore.add(BigInteger.valueOf(scoreGain));
        } else {
            m_nScore += (int) (baseScore * comboMultiplier);
        }
        
        showComboEffect();
        updateLabels();
    }

    private void showComboEffect() {
        if (m_nCombo >= 2) {
            String multiplier = m_nCombo > 5 ? "3.0x" : "1.5x";
            lblComboDisplay.setText("COMBO x" + m_nCombo + " (" + multiplier + ")");
            lblComboDisplay.setForeground(m_nCombo > 5 ? new Color(255, 50, 50) : new Color(255, 150, 0));
            
            if (comboDisplayTimer != null) {
                comboDisplayTimer.stop();
            }
            comboDisplayAlpha = 255;
            comboDisplayTimer = new Timer(50, e -> {
                comboDisplayAlpha -= 15;
                if (comboDisplayAlpha <= 0) {
                    comboDisplayAlpha = 0;
                    lblComboDisplay.setText("");
                    comboDisplayTimer.stop();
                } else {
                    int alpha = Math.max(0, Math.min(255, comboDisplayAlpha));
                    Color baseColor = m_nCombo > 5 ? new Color(255, 50, 50) : new Color(255, 150, 0);
                    lblComboDisplay.setForeground(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha));
                }
            });
            comboDisplayTimer.start();
        }
    }

    private void handleWin() {
        stopCountdown();
        if (m_nGameMode == MODE_STAGE) {
            if (m_nStage >= 5) {
                JOptionPane.showMessageDialog(this,
                    "恭喜通过关卡" + m_nLevel + "全部阶段！",
                    "通关", JOptionPane.INFORMATION_MESSAGE);
                gameCompleted();
            } else {
                m_nStage++;
                JOptionPane.showMessageDialog(this,
                    "恭喜通过阶段" + (m_nStage - 1) + "！\n进入阶段" + m_nStage,
                    "通关", JOptionPane.INFORMATION_MESSAGE);
                setupStage();
                enterGame();
                startCountdown();
            }
        } else if (m_nGameMode == MODE_BASIC) {
            if (m_nLevel >= 5) {
                gameCompleted();
            } else {
                JOptionPane.showMessageDialog(this,
                    "恭喜通过第 " + m_nLevel + " 关！\n点击进入下一关",
                    "通关", JOptionPane.INFORMATION_MESSAGE);
                setupBasicMode(m_nLevel + 1);
                enterGame();
                startCountdown();
            }
        } else if (m_nGameMode == MODE_ENDLESS) {
            m_nLevel++;
            int opt = JOptionPane.showConfirmDialog(this,
                "恭喜通过第 " + (m_nLevel - 1) + " 关！\n当前分数: " + m_bigScore + "\n是否继续下一关？",
                "通关", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                setupEndlessNextLevel();
                enterGame();
            } else {
                gameCompleted();
            }
        } else {
            gameCompleted();
        }
    }

    private void setupEndlessNextLevel() {
        int[][] boardParams = {{12, 8}, {12, 10}, {14, 16}};
        
        m_nCurrentRows = boardParams[m_nEndlessBoardSize][0];
        m_nCurrentCols = boardParams[m_nEndlessBoardSize][1];
        m_nCurrentPicNum = 8;
        
        loadTheme(m_strCurrentTheme);
        gameControl.startGame(m_nCurrentRows, m_nCurrentCols, m_nCurrentPicNum);
        m_nCombo = 0;
        m_nLastClearTime = 0;
        m_nClearCount = 0;
        
        String themeName = m_nThemeType == THEME_FRUIT ? "水果" : (m_nThemeType == THEME_CXK ? "CXK" : "MH");
        lblTitle.setText("休闲模式 " + themeName + " Lv." + m_nLevel);
        lblTime.setText("时间: --");
        updateLabels();
    }

    private void gameCompleted() {
        m_bPlaying = false;
        btnStart.setEnabled(true);
        btnPause.setEnabled(false);
        btnPrompt.setEnabled(false);
        btnReset.setEnabled(false);
        btnRestart.setEnabled(false);
        btnGiveUp.setEnabled(false);
        btnSettings.setEnabled(false);

        if (m_nGameMode == MODE_STAGE || m_nGameMode == MODE_BASIC) {
            m_nScore += (int) (m_nRemainTime * 10 + m_nClearCount * 5);
        }

        updateLabels();

        String scoreDisplay = m_nGameMode == MODE_ENDLESS ? m_bigScore.toString() : String.valueOf(m_nScore);
        if (HighScorePanel.isHighScore(m_nGameMode == MODE_ENDLESS ? m_bigScore : BigInteger.valueOf(m_nScore), m_nGameMode)) {
            String name = JOptionPane.showInputDialog(this,
                "恭喜！你的分数 " + scoreDisplay + " 进入排行榜，请输入姓名：");
            if (name != null && !name.trim().isEmpty()) {
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                ScoreRecord record = new ScoreRecord(name.trim(), 
                    m_nGameMode == MODE_ENDLESS ? m_bigScore : BigInteger.valueOf(m_nScore),
                    0,
                    m_nLevel, date, m_nGameMode);
                HighScorePanel.saveScore(record);
            }
        } else {
            JOptionPane.showMessageDialog(this, "游戏结束！得分: " + scoreDisplay);
        }

        deleteSaveFile();
    }

    private void gameFailed() {
        m_bPlaying = false;
        btnStart.setEnabled(true);
        btnPause.setEnabled(false);
        btnPrompt.setEnabled(false);
        btnReset.setEnabled(false);
        btnRestart.setEnabled(false);
        btnGiveUp.setEnabled(false);
        btnSettings.setEnabled(false);

        int opt = JOptionPane.showConfirmDialog(this,
            "时间到！是否重试当前关卡？",
            "失败", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            if (m_nGameMode == MODE_STAGE) {
                setupStageMode(m_nLevel);
            } else {
                setupBasicMode(m_nLevel);
            }
            enterGame();
            startCountdown();
        } else {
            deleteSaveFile();
            mainFrame.showMainPanel();
        }
    }

    private void startCountdown() {
        stopCountdown();
        countdownTimer = new Timer(1000, e -> {
            if (!m_bPaused) {
                m_nRemainTime--;
                updateTimeLabel();
                if (m_nRemainTime <= 0) {
                    stopCountdown();
                    SwingUtilities.invokeLater(() -> {
                        m_bPaused = true;
                        gameFailed();
                        m_bPaused = false;
                    });
                }
            }
        });
        countdownTimer.start();
    }

    private void stopCountdown() {
        if (countdownTimer != null) {
            countdownTimer.stop();
            countdownTimer = null;
        }
    }

    private void updateLabels() {
        if (m_nGameMode == MODE_ENDLESS) {
            lblScore.setText("分数: " + m_bigScore.toString());
        } else {
            lblScore.setText("分数: " + m_nScore);
        }
        String comboText = m_nCombo >= 2 ? "连击: " + m_nCombo + " (x" + (m_nCombo > 5 ? "3.0" : "1.5") + ")" : "连击: " + m_nCombo;
        lblCombo.setText(comboText);
        if (m_nGameMode == MODE_STAGE) {
            lblLevel.setText("关卡" + m_nLevel + "-" + m_nStage);
        } else if (m_nGameMode == MODE_ENDLESS) {
            lblLevel.setText("关卡: " + m_nLevel);
        } else {
            lblLevel.setText("");
        }
        updateTimeLabel();
    }

    private void updateTimeLabel() {
        if (m_nGameMode == MODE_STAGE || m_nGameMode == MODE_BASIC) {
            lblTime.setText("剩余: " + m_nRemainTime + "s");
            if (m_nRemainTime <= 15) {
                lblTime.setForeground(Color.RED);
            } else {
                lblTime.setForeground(m_themeLabelColor);
            }
        } else {
            lblTime.setText("时间: --");
            lblTime.setForeground(m_themeLabelColor);
        }
    }

    public void saveGame() {
        if (gameControl.getGameMap() == null) {
            return;
        }
        String saveFile = ResourcePath.getSaveFile(m_nGameMode);
        File f = new File(saveFile);
        f.getParentFile().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            GameState state = new GameState(
                gameControl.getGameMap(), m_nGameMode, m_nLevel, m_nStage, m_nScore, m_bigScore,
                m_nCombo, m_nMaxCombo, m_nLastClearTime, m_nRemainTime,
                m_nGameMode == MODE_ENDLESS, m_nCurrentRows, m_nCurrentCols, m_nCurrentPicNum
            );
            oos.writeObject(state);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveGameOnExit() {
        if (m_bPlaying) {
            saveGame();
        }
    }

    private void deleteSaveFile() {
        File f = new File(ResourcePath.getSaveFile(m_nGameMode));
        if (f.exists()) {
            f.delete();
        }
    }

    private void showHelp() {
        new HelpDialog((Frame) SwingUtilities.getWindowAncestor(this)).setVisible(true);
    }

    private void showGameSettings() {
        boolean wasRunning = countdownTimer != null && countdownTimer.isRunning();
        if (wasRunning) {
            countdownTimer.stop();
        }
        
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "设置", true);
        dlg.setSize(350, 200);
        dlg.setLocationRelativeTo(this);
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel bgmVolumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel bgmVolumeLabel = new JLabel("背景音乐：");
        bgmVolumeLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        bgmVolumePanel.add(bgmVolumeLabel);
        JSlider bgmVolumeSlider = new JSlider(0, 100, AudioManager.getBgmVolume());
        bgmVolumeSlider.setPreferredSize(new Dimension(150, 30));
        bgmVolumePanel.add(bgmVolumeSlider);
        JLabel bgmVolumeValueLabel = new JLabel(AudioManager.getBgmVolume() + "%");
        bgmVolumeValueLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        bgmVolumePanel.add(bgmVolumeValueLabel);
        bgmVolumeSlider.addChangeListener(e -> {
            bgmVolumeValueLabel.setText(bgmVolumeSlider.getValue() + "%");
            AudioManager.setBgmVolume(bgmVolumeSlider.getValue());
        });

        JPanel sfxVolumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel sfxVolumeLabel = new JLabel("音效：    ");
        sfxVolumeLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        sfxVolumePanel.add(sfxVolumeLabel);
        JSlider sfxVolumeSlider = new JSlider(0, 100, AudioManager.getSfxVolume());
        sfxVolumeSlider.setPreferredSize(new Dimension(150, 30));
        sfxVolumePanel.add(sfxVolumeSlider);
        JLabel sfxVolumeValueLabel = new JLabel(AudioManager.getSfxVolume() + "%");
        sfxVolumeValueLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        sfxVolumePanel.add(sfxVolumeValueLabel);
        sfxVolumeSlider.addChangeListener(e -> {
            sfxVolumeValueLabel.setText(sfxVolumeSlider.getValue() + "%");
            AudioManager.setSfxVolume(sfxVolumeSlider.getValue());
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeBtn = new JButton("关闭");
        closeBtn.setFont(new Font("宋体", Font.PLAIN, 14));
        closeBtn.addActionListener(e -> dlg.dispose());
        btnPanel.add(closeBtn);

        contentPanel.add(bgmVolumePanel);
        contentPanel.add(sfxVolumePanel);
        contentPanel.add(btnPanel);
        dlg.add(contentPanel);
        dlg.setVisible(true);
        
        if (wasRunning) {
            countdownTimer.start();
        }
    }

    private void giveUpGame() {
        if (!m_bPlaying) return;
        int opt = JOptionPane.showConfirmDialog(this,
            "确定要放弃当前游戏吗？", "放弃游戏", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            m_bPlaying = false;
            stopCountdown();
            AudioManager.stopBgm();
            
            btnStart.setEnabled(true);
            btnPause.setEnabled(false);
            btnPrompt.setEnabled(false);
            btnReset.setEnabled(false);
            btnRestart.setEnabled(false);
            btnGiveUp.setEnabled(false);
            btnSettings.setEnabled(false);

            if (m_nGameMode == MODE_STAGE || m_nGameMode == MODE_BASIC) {
                m_nScore += (int) (m_nRemainTime * 10 + m_nClearCount * 5);
            }

            updateLabels();

            String scoreDisplay = m_nGameMode == MODE_ENDLESS ? m_bigScore.toString() : String.valueOf(m_nScore);
            if (HighScorePanel.isHighScore(m_nGameMode == MODE_ENDLESS ? m_bigScore : BigInteger.valueOf(m_nScore), m_nGameMode)) {
                String name = JOptionPane.showInputDialog(this,
                    "恭喜！你的分数 " + scoreDisplay + " 进入排行榜，请输入姓名：");
                if (name != null && !name.trim().isEmpty()) {
                    String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    ScoreRecord record = new ScoreRecord(name.trim(), 
                        m_nGameMode == MODE_ENDLESS ? m_bigScore : BigInteger.valueOf(m_nScore),
                        0,
                        m_nLevel, date, m_nGameMode);
                    HighScorePanel.saveScore(record);
                }
            } else {
                JOptionPane.showMessageDialog(this, "游戏结束！得分: " + scoreDisplay);
            }

            deleteSaveFile();
            mainFrame.showMainPanel();
        }
    }

    private void backToMain() {
        if (m_bPlaying) {
            int opt = JOptionPane.showConfirmDialog(this,
                "是否保存当前进度？", "返回", JOptionPane.YES_NO_CANCEL_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                saveGame();
            } else if (opt == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        m_bPlaying = false;
        stopCountdown();
        AudioManager.stopBgm();
        mainFrame.showMainPanel();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (memDCBg != null) {
            g2d.drawImage(memDCBg, 0, 0, 800, 600, this);
        } else {
            g2d.setColor(Color.GRAY);
            g2d.fillRect(0, 0, 800, 600);
        }

        int[][] map = gameControl.getGameMap();
        if (map != null && m_bPlaying) {
            for (int i = 0; i < m_nCurrentRows; i++) {
                for (int j = 0; j < m_nCurrentCols; j++) {
                    drawElement(g2d, i, j, map[i][j]);
                }
            }
        }

        drawTipFrame(g2d, m_svSelFst, new Color(233, 43, 43));
        drawTipFrame(g2d, m_svSelSec, new Color(233, 43, 43));

        if (hintVertices != null && hintBlinkCount % 2 == 0) {
            drawTipFrame(g2d, hintVertices[0], Color.BLUE);
            drawTipFrame(g2d, hintVertices[1], Color.BLUE);
        }

        if (hintPath != null && hintPath.length >= 2) {
            drawPath(g2d, java.util.Arrays.asList(hintPath), Color.CYAN, 2);
        }

        if (linkPath.size() >= 2) {
            drawPath(g2d, linkPath, new Color(0, 200, 200), 3);
        }

        if (m_bPaused) {
            g2d.setColor(new Color(0, 0, 0, 128));
            g2d.fillRect(m_ptGameTop.x, m_ptGameTop.y,
                m_nCurrentCols * m_elemW, m_nCurrentRows * m_elemH);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("微软雅黑", Font.BOLD, 36));
            FontMetrics fm = g2d.getFontMetrics();
            String text = "暂停中";
            int tw = fm.stringWidth(text);
            g2d.drawString(text, m_ptGameTop.x + (m_nCurrentCols * m_elemW - tw) / 2,
                m_ptGameTop.y + (m_nCurrentRows * m_elemH) / 2);
        }
    }

    private void drawElement(Graphics2D g, int row, int col, int elemVal) {
        if (elemVal < 0 || elementImages == null || elemVal >= elementImages.length) {
            return;
        }
        int dstX = m_ptGameTop.x + col * m_elemW;
        int dstY = m_ptGameTop.y + row * m_elemH;
        g.drawImage(elementImages[elemVal], dstX, dstY, m_elemW, m_elemH, null);
    }

    private void drawTipFrame(Graphics2D g, Vertex v, Color color) {
        if (v == null) {
            return;
        }
        int x = m_ptGameTop.x + v.col * m_elemW;
        int y = m_ptGameTop.y + v.row * m_elemH;
        g.setColor(color);
        g.setStroke(new BasicStroke(3));
        g.drawRect(x, y, m_elemW, m_elemH);
    }

    private void drawPath(Graphics2D g, List<Vertex> path, Color color, int strokeWidth) {
        if (path == null || path.size() < 2) {
            return;
        }
        g.setColor(color);
        g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < path.size() - 1; i++) {
            Vertex p1 = path.get(i);
            Vertex p2 = path.get(i + 1);
            int x1 = m_ptGameTop.x + p1.col * m_elemW + m_elemW / 2;
            int y1 = m_ptGameTop.y + p1.row * m_elemH + m_elemH / 2;
            int x2 = m_ptGameTop.x + p2.col * m_elemW + m_elemW / 2;
            int y2 = m_ptGameTop.y + p2.row * m_elemH + m_elemH / 2;
            g.drawLine(x1, y1, x2, y2);
        }
    }
}
