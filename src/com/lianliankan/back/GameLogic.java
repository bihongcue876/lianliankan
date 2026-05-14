package com.lianliankan.back;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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

        GameGraph graph = GameGraph.fromMap(map);
        GraphVertex start = graph.getVertex(v1.row, v1.col);
        GraphVertex target = graph.getVertex(v2.row, v2.col);

        return bfsLink(graph, start, target);
    }

    public List<Vertex> isLink(GameGraph graph, Vertex v1, Vertex v2) {
        if (v1 == null || v2 == null) return null;
        if (v1.color != v2.color) return null;
        GraphVertex start = graph.getVertex(v1.row, v1.col);
        GraphVertex target = graph.getVertex(v2.row, v2.col);
        return bfsLink(graph, start, target);
    }

    public Vertex[] findMatch(int[][] map) {
        GameGraph graph = GameGraph.fromMap(map);
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
                        List<Vertex> path = isLink(graph, v1, v2);
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
        GameGraph graph = GameGraph.fromMap(map);
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
                        List<Vertex> path = isLink(graph, v1, v2);
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

    private static class BFState {
        GraphVertex node;
        int dir;
        int turns;
        BFState parent;

        BFState(GraphVertex node, int dir, int turns, BFState parent) {
            this.node = node;
            this.dir = dir;
            this.turns = turns;
            this.parent = parent;
        }
    }

    private List<Vertex> bfsLink(GameGraph graph, GraphVertex start, GraphVertex target) {
        Queue<BFState> queue = new LinkedList<>();
        int totalNodes = graph.getAllVertices().size();
        boolean[][][] visited = new boolean[totalNodes][5][3];
        int dirIndex = 4;

        queue.add(new BFState(start, -1, 0, null));
        visited[start.id][dirIndex][0] = true;

        while (!queue.isEmpty()) {
            BFState cur = queue.poll();
            if (cur.node == target && cur.turns <= 2) {
                return buildPath(cur);
            }

            for (GraphVertex neighbor : cur.node.neighbors) {
                if (neighbor.color != -1 && neighbor != target) continue;

                int newDir = getDirection(cur.node, neighbor);
                int newTurns = cur.turns;
                if (cur.dir != -1 && newDir != cur.dir) {
                    newTurns++;
                }
                if (newTurns > 2) continue;

                int newDirIdx = newDir == -1 ? 4 : newDir;
                if (!visited[neighbor.id][newDirIdx][newTurns]) {
                    visited[neighbor.id][newDirIdx][newTurns] = true;
                    queue.add(new BFState(neighbor, newDir, newTurns, cur));
                }
            }
        }
        return null;
    }

    private int getDirection(GraphVertex from, GraphVertex to) {
        if (to.row == from.row && to.col == from.col + 1) return 1;
        if (to.row == from.row && to.col == from.col - 1) return 3;
        if (to.col == from.col && to.row == from.row + 1) return 2;
        if (to.col == from.col && to.row == from.row - 1) return 0;
        return -1;
    }

    private List<Vertex> buildPath(BFState endState) {
        List<Vertex> path = new ArrayList<>();
        BFState cur = endState;
        while (cur != null) {
            path.add(new Vertex(cur.node.row, cur.node.col, cur.node.color));
            cur = cur.parent;
        }
        Collections.reverse(path);
        return path;
    }
}