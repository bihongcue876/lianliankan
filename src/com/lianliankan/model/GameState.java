package com.lianliankan.model;

import java.io.Serializable;
import java.math.BigInteger;

public class GameState implements Serializable {
    private static final long serialVersionUID = 3L;

    private int[][] map;
    private int mode;
    private int level;
    private int stage;
    private int score;
    private String bigScore;
    private int combo;
    private int maxCombo;
    private long lastClearTime;
    private long remainTime;
    private boolean isEndlessMode;
    private int rows;
    private int cols;
    private int picNum;

    public GameState(int[][] map, int mode, int level, int stage, int score, BigInteger bigScore, int combo, int maxCombo,
                     long lastClearTime, long remainTime, boolean isEndlessMode,
                     int rows, int cols, int picNum) {
        this.map = map;
        this.mode = mode;
        this.level = level;
        this.stage = stage;
        this.score = score;
        this.bigScore = bigScore != null ? bigScore.toString() : "0";
        this.combo = combo;
        this.maxCombo = maxCombo;
        this.lastClearTime = lastClearTime;
        this.remainTime = remainTime;
        this.isEndlessMode = isEndlessMode;
        this.rows = rows;
        this.cols = cols;
        this.picNum = picNum;
    }

    public int[][] getMap() { return map; }
    public int getMode() { return mode; }
    public int getLevel() { return level; }
    public int getStage() { return stage; }
    public int getScore() { return score; }
    public BigInteger getBigScore() { return new BigInteger(bigScore); }
    public int getCombo() { return combo; }
    public int getMaxCombo() { return maxCombo; }
    public long getLastClearTime() { return lastClearTime; }
    public long getRemainTime() { return remainTime; }
    public boolean isEndlessMode() { return isEndlessMode; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getPicNum() { return picNum; }
}
