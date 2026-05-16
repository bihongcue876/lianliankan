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

        // 填充图案：每种图案出现repeat*2次
        int count = 0;
        for (int i = 0; i < picNum; i++) {
            for (int j = 0; j < repeat * 2; j++) {
                map[count / cols][count % cols] = i;
                count++;
            }
        }

        // 随机打乱
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

    // 判断两点是否可连通（从二维数组构建图，然后执行BFS）
    public List<Vertex> isLink(int[][] map, Vertex v1, Vertex v2) {
        if (v1 == null || v2 == null) return null;
        if (v1.color != v2.color) return null;

        // 将二维数组转换为图结构
        GameGraph graph = GameGraph.fromMap(map);
        GraphVertex start = graph.getVertex(v1.row, v1.col);
        GraphVertex target = graph.getVertex(v2.row, v2.col);

        // 在图上执行BFS查找路径
        return bfsLink(graph, start, target);
    }

    // 重载方法：直接在已有图上执行BFS
    public List<Vertex> isLink(GameGraph graph, Vertex v1, Vertex v2) {
        if (v1 == null || v2 == null) return null;
        if (v1.color != v2.color) return null;
        GraphVertex start = graph.getVertex(v1.row, v1.col);
        GraphVertex target = graph.getVertex(v2.row, v2.col);
        return bfsLink(graph, start, target);
    }

    // 查找一对可消除的格子（提示功能）
    public Vertex[] findMatch(int[][] map) {
        GameGraph graph = GameGraph.fromMap(map);
        int rows = map.length;
        int cols = map[0].length;
        
        // 遍历所有格子对，寻找可连通的相同图案
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

    // 查找可消除格子的路径（用于绘制连接线）
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

    // 重排棋盘：打乱剩余图案，确保有解
    public void resetMap(int[][] map, int picNum) {
        List<Integer> elements = new ArrayList<>();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] != -1) {
                    elements.add(map[i][j]);
                }
            }
        }

        // 随机打乱，最多尝试100次
        Random rnd = new Random();
        int attempts = 0;
        do {
            // Fisher-Yates洗牌算法
            for (int i = elements.size() - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                int tmp = elements.get(i);
                elements.set(i, elements.get(j));
                elements.set(j, tmp);
            }

            // 重新填充棋盘
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

        // 如果100次都无解，重新生成棋盘
        if (!hasValidMove(map)) {
            int[][] newMap = initMap(map.length, map[0].length, picNum);
            for (int i = 0; i < map.length; i++) {
                System.arraycopy(newMap[i], 0, map[i], 0, map[0].length);
            }
        }
    }

    // BFS状态类：记录当前顶点、方向、转弯次数、父状态
    private static class BFState {
        GraphVertex node;  // 当前顶点
        int dir;           // 当前方向：0上 1右 2下 3左 4初始
        int turns;         // 已转弯次数
        BFState parent;    // 父状态，用于重建路径

        BFState(GraphVertex node, int dir, int turns, BFState parent) {
            this.node = node;
            this.dir = dir;
            this.turns = turns;
            this.parent = parent;
        }
    }

    /**
     * BFS算法查找两点间的可连通路径
     * 
     * 【图论概念 - 广度优先搜索】
     * BFS从起点开始，逐层向外扩展，直到找到目标或遍历完所有可达顶点
     * 
     * 【连连看的特殊约束】
     * 1. 路径最多转弯2次
     * 2. 只能通过空格(color=-1)或目标顶点
     * 
     * 【状态空间】
     * visited[顶点ID][方向][转弯次数]
     * 三维数组确保不会重复访问相同状态
     */
    private List<Vertex> bfsLink(GameGraph graph, GraphVertex start, GraphVertex target) {
        Queue<BFState> queue = new LinkedList<>();
        int totalNodes = graph.getAllVertices().size();
        
        // 三维访问标记：[顶点ID][方向][转弯次数]
        // 方向：0上 1右 2下 3左 4初始状态
        boolean[][][] visited = new boolean[totalNodes][5][3];
        int dirIndex = 4; // 初始方向索引

        // 初始状态：起点，方向-1，转弯0次
        queue.add(new BFState(start, -1, 0, null));
        visited[start.id][dirIndex][0] = true;

        // BFS主循环
        while (!queue.isEmpty()) {
            BFState cur = queue.poll();
            
            // 找到目标且转弯次数<=2
            if (cur.node == target && cur.turns <= 2) {
                return buildPath(cur);
            }

            // 遍历所有邻居（邻接表遍历）
            for (GraphVertex neighbor : cur.node.neighbors) {
                // 只能通过空格或目标顶点
                if (neighbor.color != -1 && neighbor != target) continue;

                // 计算新方向
                int newDir = getDirection(cur.node, neighbor);
                int newTurns = cur.turns;
                
                // 如果方向改变，转弯次数+1
                if (cur.dir != -1 && newDir != cur.dir) {
                    newTurns++;
                }
                
                // 转弯超过2次，跳过
                if (newTurns > 2) continue;

                // 检查是否访问过该状态
                int newDirIdx = newDir == -1 ? 4 : newDir;
                if (!visited[neighbor.id][newDirIdx][newTurns]) {
                    visited[neighbor.id][newDirIdx][newTurns] = true;
                    queue.add(new BFState(neighbor, newDir, newTurns, cur));
                }
            }
        }
        return null; // 未找到路径
    }

    // 计算从from到to的方向
    private int getDirection(GraphVertex from, GraphVertex to) {
        if (to.row == from.row && to.col == from.col + 1) return 1; // 右
        if (to.row == from.row && to.col == from.col - 1) return 3; // 左
        if (to.col == from.col && to.row == from.row + 1) return 2; // 下
        if (to.col == from.col && to.row == from.row - 1) return 0; // 上
        return -1;
    }

    // 从终点状态回溯构建完整路径
    private List<Vertex> buildPath(BFState endState) {
        List<Vertex> path = new ArrayList<>();
        BFState cur = endState;
        
        // 通过parent指针回溯
        while (cur != null) {
            path.add(new Vertex(cur.node.row, cur.node.col, cur.node.color));
            cur = cur.parent;
        }
        
        // 反转路径（从起点到终点）
        Collections.reverse(path);
        return path;
    }
}
