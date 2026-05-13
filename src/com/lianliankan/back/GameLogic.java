package com.lianliankan.back;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameLogic {

    public int[][] initMap(int rows, int cols, int picNum) {
        int[][] map = new int[rows][cols];
        int totalCells = rows * cols;
        int repeat = totalCells / (picNum * 2);
        if (totalCells % (picNum * 2) != 0) throw new GameException("关卡数据不匹配");

        int count = 0;
        for (int i = 0; i < picNum; i++) {
            for (int j = 0; j < repeat * 2; j++) {
                map[count / cols][count % cols] = i;
                count++;
            }
        }

        Random rnd = new Random();
        for (int i = 0; i < totalCells; i++) {
            int idx1 = rnd.nextInt(totalCells);
            int idx2 = rnd.nextInt(totalCells);
            int tmp = map[idx1 / cols][idx1 % cols];
            map[idx1 / cols][idx1 % cols] = map[idx2 / cols][idx2 % cols];
            map[idx2 / cols][idx2 % cols] = tmp;
        }
        return map;
    }

    public void clear(int[][] map, Vertex v1, Vertex v2) {
        map[v1.row][v1.col] = -1;
        map[v2.row][v2.col] = -1;
    }

    public boolean isBlank(int[][] map) {
        for (int[] row : map) {
            for (int v : row) {
                if (v != -1) return false;
            }
        }
        return true;
    }

    public List<Vertex> isLink(int[][] map, Vertex v1, Vertex v2) {
        if (v1 == null || v2 == null) return null;
        if (v1.color != v2.color) return null;

        List<Vertex> path = new ArrayList<>();

        path.clear();
        path.add(v1);
        if (linkInRow(map, v1, v2, path)) {
            return new ArrayList<>(path);
        }

        path.clear();
        path.add(v1);
        if (linkInCol(map, v1, v2, path)) {
            return new ArrayList<>(path);
        }

        path.clear();
        path.add(v1);
        if (oneCornerLink(map, v1, v2, path)) {
            return new ArrayList<>(path);
        }

        path.clear();
        path.add(v1);
        if (twoCornerLink(map, v1, v2, path)) {
            return new ArrayList<>(path);
        }

        return null;
    }

    private boolean isBlank(int[][] map, int row, int col) {
        if (row < 0 || row >= map.length || col < 0 || col >= map[0].length) {
            return true;
        }
        return map[row][col] == -1;
    }

    private boolean linkInRow(int[][] map, Vertex v1, Vertex v2, List<Vertex> path) {
        if (v1.row != v2.row) return false;
        int col1 = Math.min(v1.col, v2.col);
        int col2 = Math.max(v1.col, v2.col);
        for (int col = col1 + 1; col < col2; col++) {
            if (!isBlank(map, v1.row, col)) return false;
        }
        path.add(new Vertex(v2.row, v2.col, v2.color));
        return true;
    }

    private boolean linkInCol(int[][] map, Vertex v1, Vertex v2, List<Vertex> path) {
        if (v1.col != v2.col) return false;
        int row1 = Math.min(v1.row, v2.row);
        int row2 = Math.max(v1.row, v2.row);
        for (int row = row1 + 1; row < row2; row++) {
            if (!isBlank(map, row, v1.col)) return false;
        }
        path.add(new Vertex(v2.row, v2.col, v2.color));
        return true;
    }

    private boolean oneCornerLink(int[][] map, Vertex v1, Vertex v2, List<Vertex> path) {
        Vertex corner1 = new Vertex(v1.row, v2.col, -1);
        if (isBlank(map, corner1.row, corner1.col)) {
            int originalSize = path.size();
            path.add(corner1);
            if (linkInRow(map, v1, corner1, path) && linkInCol(map, corner1, v2, path)) {
                return true;
            }
            while (path.size() > originalSize) {
                path.remove(path.size() - 1);
            }
        }

        Vertex corner2 = new Vertex(v2.row, v1.col, -1);
        if (isBlank(map, corner2.row, corner2.col)) {
            int originalSize = path.size();
            path.add(corner2);
            if (linkInCol(map, v1, corner2, path) && linkInRow(map, corner2, v2, path)) {
                return true;
            }
            while (path.size() > originalSize) {
                path.remove(path.size() - 1);
            }
        }
        return false;
    }

    private boolean twoCornerLink(int[][] map, Vertex v1, Vertex v2, List<Vertex> path) {
        for (int i = -1; i <= map.length; i++) {
            if (i == v1.row || i == v2.row) continue;
            if (isBlank(map, i, v1.col) && isBlank(map, i, v2.col)) {
                Vertex p1 = new Vertex(i, v1.col, -1);
                Vertex p2 = new Vertex(i, v2.col, -1);
                int originalSize = path.size();
                path.add(p1);
                path.add(p2);
                if (linkInCol(map, v1, p1, path) && linkInRow(map, p1, p2, path) && linkInCol(map, p2, v2, path)) {
                    return true;
                }
                while (path.size() > originalSize) {
                    path.remove(path.size() - 1);
                }
            }
        }

        for (int j = -1; j <= map[0].length; j++) {
            if (j == v1.col || j == v2.col) continue;
            if (isBlank(map, v1.row, j) && isBlank(map, v2.row, j)) {
                Vertex p1 = new Vertex(v1.row, j, -1);
                Vertex p2 = new Vertex(v2.row, j, -1);
                int originalSize = path.size();
                path.add(p1);
                path.add(p2);
                if (linkInRow(map, v1, p1, path) && linkInCol(map, p1, p2, path) && linkInRow(map, p2, v2, path)) {
                    return true;
                }
                while (path.size() > originalSize) {
                    path.remove(path.size() - 1);
                }
            }
        }
        return false;
    }

    public Vertex[] findMatch(int[][] map) {
        int rows = map.length;
        int cols = map[0].length;
        for (int r1 = 0; r1 < rows; r1++) {
            for (int c1 = 0; c1 < cols; c1++) {
                if (map[r1][c1] == -1) continue;
                for (int r2 = 0; r2 < rows; r2++) {
                    for (int c2 = 0; c2 < cols; c2++) {
                        if (r1 == r2 && c1 == c2) continue;
                        if (map[r2][c2] == -1) continue;
                        if (map[r1][c1] != map[r2][c2]) continue;
                        Vertex v1 = new Vertex(r1, c1, map[r1][c1]);
                        Vertex v2 = new Vertex(r2, c2, map[r2][c2]);
                        List<Vertex> path = isLink(map, v1, v2);
                        if (path != null) {
                            return new Vertex[]{v1, v2};
                        }
                    }
                }
            }
        }
        return null;
    }

    public List<Vertex> findMatchPath(int[][] map) {
        int rows = map.length;
        int cols = map[0].length;
        for (int r1 = 0; r1 < rows; r1++) {
            for (int c1 = 0; c1 < cols; c1++) {
                if (map[r1][c1] == -1) continue;
                for (int r2 = 0; r2 < rows; r2++) {
                    for (int c2 = 0; c2 < cols; c2++) {
                        if (r1 == r2 && c1 == c2) continue;
                        if (map[r2][c2] == -1) continue;
                        if (map[r1][c1] != map[r2][c2]) continue;
                        Vertex v1 = new Vertex(r1, c1, map[r1][c1]);
                        Vertex v2 = new Vertex(r2, c2, map[r2][c2]);
                        List<Vertex> path = isLink(map, v1, v2);
                        if (path != null) {
                            return path;
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean hasValidMove(int[][] map) {
        return findMatch(map) != null;
    }

    public void resetMap(int[][] map, int picNum) {
        List<Integer> elements = new ArrayList<>();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] != -1) {
                    elements.add(map[i][j]);
                }
            }
        }

        Random rnd = new Random();
        int attempts = 0;
        do {
            for (int i = elements.size() - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                int tmp = elements.get(i);
                elements.set(i, elements.get(j));
                elements.set(j, tmp);
            }

            int idx = 0;
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    if (map[i][j] != -1) {
                        map[i][j] = elements.get(idx++);
                    }
                }
            }
            attempts++;
        } while (!hasValidMove(map) && attempts < 100);

        if (!hasValidMove(map)) {
            int[][] newMap = initMap(map.length, map[0].length, picNum);
            for (int i = 0; i < map.length; i++) {
                System.arraycopy(newMap[i], 0, map[i], 0, map[0].length);
            }
        }
    }
}