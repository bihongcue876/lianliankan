package com.lianliankan.model;

import java.io.Serializable;
import java.math.BigInteger;

public class ScoreRecord implements Serializable {
    private static final long serialVersionUID = 2L;

    private String name;
    private String score;
    private long time;
    private int stage;
    private String date;
    private int mode;

    public ScoreRecord(String name, BigInteger score, long time, int stage, String date, int mode) {
        this.name = name;
        this.score = score != null ? score.toString() : "0";
        this.time = time;
        this.stage = stage;
        this.date = date;
        this.mode = mode;
    }

    public String getName() { return name; }
    public BigInteger getScore() { return new BigInteger(score); }
    public long getTime() { return time; }
    public int getStage() { return stage; }
    public String getDate() { return date; }
    public int getMode() { return mode; }
}
