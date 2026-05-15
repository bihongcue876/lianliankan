package com.lianliankan.back;

import java.util.*;

public class GameGraph {
    private int rows, cols;
    private GraphVertex[][] grid;
    private List<GraphVertex> allVertices;
    private Map<String, GraphVertex> posMap;

    public static GameGraph fromMap(int[][] map) {
        int rows = map.length;
        int cols = map[0].length;
        return new GameGraph(map, rows, cols);
    }

    private GameGraph(int[][] map, int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        int totalRows = rows + 2;
        int totalCols = cols + 2;
        grid = new GraphVertex[totalRows][totalCols];
        allVertices = new ArrayList<>();
        posMap = new HashMap<>();

        for (int i = -1; i <= rows; i++) {
            for (int j = -1; j <= cols; j++) {
                int color;
                if (i >= 0 && i < rows && j >= 0 && j < cols) {
                    color = map[i][j];
                } else {
                    color = -1;
                }
                int id = allVertices.size();
                GraphVertex v = new GraphVertex(id, i, j, color);
                grid[i + 1][j + 1] = v;
                allVertices.add(v);
                posMap.put(i + "," + j, v);
            }
        }

        for (int i = -1; i <= rows; i++) {
            for (int j = -1; j <= cols; j++) {
                GraphVertex v = grid[i + 1][j + 1];
                if (j + 1 <= cols) v.neighbors.add(grid[i + 1][j + 2]);
                if (i + 1 <= rows) v.neighbors.add(grid[i + 2][j + 1]);
                if (j - 1 >= -1) v.neighbors.add(grid[i + 1][j]);
                if (i - 1 >= -1) v.neighbors.add(grid[i][j + 1]);
            }
        }
    }

    public GraphVertex getVertex(int row, int col) {
        return posMap.get(row + "," + col);
    }

    public void clearVertex(GraphVertex v) {
        v.color = -1;
    }

    public boolean isAllEmpty() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (getVertex(i, j).color != -1) return false;
            }
        }
        return true;
    }

    public int[][] toMap() {
        int[][] map = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                map[i][j] = getVertex(i, j).color;
            }
        }
        return map;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public List<GraphVertex> getAllVertices() { return allVertices; }
}
