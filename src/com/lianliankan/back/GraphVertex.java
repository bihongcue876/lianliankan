package com.lianliankan.back;

import java.util.ArrayList;
import java.util.List;

public class GraphVertex {
    public final int id;
    public final int row;
    public final int col;
    public int color;
    public List<GraphVertex> neighbors;

    public GraphVertex(int id, int row, int col, int color) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.color = color;
        this.neighbors = new ArrayList<>(4);
    }
}
