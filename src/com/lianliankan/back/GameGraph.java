package com.lianliankan.back;

import java.util.*;

public class GameGraph {
    private int rows, cols;
    
    // 顶点网格：用于通过行列坐标快速访问顶点 O(1)
    private GraphVertex[][] grid;
    
    // 所有顶点列表：用于获取顶点总数
    private List<GraphVertex> allVertices;
    
    // 位置到顶点的映射：key="row,col"，用于O(1)查找
    private Map<String, GraphVertex> posMap;
    
    public static GameGraph fromMap(int[][] map) {
        int rows = map.length;
        int cols = map[0].length;
        return new GameGraph(map, rows, cols);
    }
    
    private GameGraph(int[][] map, int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        
        // 包含虚拟边界：上下左右各增加一层，所以+2
        int totalRows = rows + 2;
        int totalCols = cols + 2;
        
        grid = new GraphVertex[totalRows][totalCols];
        allVertices = new ArrayList<>();
        posMap = new HashMap<>();
        
        // 步骤1: 创建所有顶点（包含虚拟边界顶点）
        // 虚拟边界顶点：row=-1或row=rows或col=-1或col=cols
        for (int i = -1; i <= rows; i++) {
            for (int j = -1; j <= cols; j++) {
                int color;
                
                // 棋盘内部使用实际颜色，边界外使用-1(可通行)
                if (i >= 0 && i < rows && j >= 0 && j < cols) {
                    color = map[i][j];
                } else {
                    color = -1; // 虚拟边界顶点，可通行
                }
                
                int id = allVertices.size();
                GraphVertex v = new GraphVertex(id, i, j, color);
                
                grid[i + 1][j + 1] = v; // 注意索引偏移
                allVertices.add(v);
                posMap.put(i + "," + j, v);
            }
        }
        
        // 步骤2: 建立边（邻接关系）- 无向图
        // 每个顶点添加四个方向的邻居
        for (int i = -1; i <= rows; i++) {
            for (int j = -1; j <= cols; j++) {
                GraphVertex v = grid[i + 1][j + 1];
                
                // 添加四个方向的邻居（右、下、左、上）
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
    
    // 清除顶点：将颜色设为-1，使其变为可通行
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
