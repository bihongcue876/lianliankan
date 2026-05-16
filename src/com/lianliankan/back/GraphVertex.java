package com.lianliankan.back;

import java.util.ArrayList;
import java.util.List;

public class GraphVertex {
    // 顶点唯一标识符，用于visited数组索引
    public final int id;
    
    // 顶点在棋盘上的行列坐标
    public final int row;
    public final int col;
    
    // 颜色/图案值：-1表示空格(可通行)，>=0表示具体图案
    public int color;
    
    // 邻接表：存储所有相邻顶点（上下左右最多4个）
    // 这是图论中邻接表的核心实现，与二维数组的隐式相邻关系不同
    public List<GraphVertex> neighbors;
    
    public GraphVertex(int id, int row, int col, int color) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.color = color;
        this.neighbors = new ArrayList<>(4); // 预设容量4，避免动态扩容
    }
}
