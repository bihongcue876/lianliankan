package com.lianliankan.back;

import java.util.List;

public class GameControl {
    public static final int ROWS = 10;
    public static final int COLS = 16;
    public static final int PIC_NUM = 16;

    private int[][] m_pGameMap;
    private GameGraph m_GameGraph;
    private GameLogic m_GameLogic = new GameLogic();

    public int[][] getGameMap() {
        return m_pGameMap;
    }

    public void startGame() {
        m_pGameMap = m_GameLogic.initMap(ROWS, COLS, PIC_NUM);
        m_GameGraph = GameGraph.fromMap(m_pGameMap);
        if (!m_GameLogic.hasValidMove(m_pGameMap)) {
            m_GameLogic.resetMap(m_pGameMap, PIC_NUM);
            m_GameGraph = GameGraph.fromMap(m_pGameMap);
        }
    }

    public void startGame(int rows, int cols, int picNum) {
        m_pGameMap = m_GameLogic.initMap(rows, cols, picNum);
        m_GameGraph = GameGraph.fromMap(m_pGameMap);
        if (!m_GameLogic.hasValidMove(m_pGameMap)) {
            m_GameLogic.resetMap(m_pGameMap, picNum);
            m_GameGraph = GameGraph.fromMap(m_pGameMap);
        }
    }

    public void setGameMap(int[][] map) {
        m_pGameMap = map;
        m_GameGraph = GameGraph.fromMap(map);
    }

    public List<Vertex> link(Vertex v1, Vertex v2) {
        if (v1 == null || v2 == null) return null;
        if (m_pGameMap == null) return null;
        if (v1.row == v2.row && v1.col == v2.col) return null;
        if (m_pGameMap[v1.row][v1.col] != m_pGameMap[v2.row][v2.col]) return null;
        List<Vertex> path = m_GameLogic.isLink(m_GameGraph, v1, v2);
        if (path != null) {
            m_GameLogic.clear(m_pGameMap, v1, v2);
            m_GameGraph.clearVertex(m_GameGraph.getVertex(v1.row, v1.col));
            m_GameGraph.clearVertex(m_GameGraph.getVertex(v2.row, v2.col));
        }
        return path;
    }

    public boolean isWin() {
        return m_GameLogic.isBlank(m_pGameMap);
    }

    public Vertex[] findMatch() {
        return m_GameLogic.findMatch(m_pGameMap);
    }

    public List<Vertex> findMatchPath() {
        return m_GameLogic.findMatchPath(m_pGameMap);
    }

    public boolean hasValidMove() {
        return m_GameLogic.hasValidMove(m_pGameMap);
    }

    public void resetMap() {
        m_GameLogic.resetMap(m_pGameMap, PIC_NUM);
        m_GameGraph = GameGraph.fromMap(m_pGameMap);
    }

    public void resetMap(int picNum) {
        m_GameLogic.resetMap(m_pGameMap, picNum);
        m_GameGraph = GameGraph.fromMap(m_pGameMap);
    }
}
