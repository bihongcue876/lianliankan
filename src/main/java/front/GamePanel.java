package front;

import back.ResourceManager;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 游戏界面面板
 * 负责绘制游戏背景、地图、选中框、连接线等
 */
public class GamePanel extends JPanel {
    /** 资源管理器 */
    private ResourceManager resourceManager;
    /** 当前主题 */
    private String currentTheme = "fruit";
    /** 背景图片 */
    private Image backgroundImage;
    /** 元素图片 */
    private Image elementImage;
    /** 掩码图片 */
    private Image maskImage;
    /** 游戏地图数据 */
    private int[][] gameMap;
    /** 地图行数 */
    public static final int ROW_COUNT = 10;
    /** 地图列数 */
    public static final int COL_COUNT = 16;
    /** 元素块宽度 */
    private int elemWidth;
    /** 元素块高度 */
    private int elemHeight;
    /** 地图起始X坐标 */
    private static final int MAP_START_X = 5;
    /** 地图起始Y坐标 */
    private static final int MAP_START_Y = 50;
    /** 选中的第一个点 */
    private Point selectedFirst = null;
    /** 选中的第二个点 */
    private Point selectedSecond = null;
    /** 连接路径 */
    private List<Point> linkPath = null;

    public GamePanel() {
        resourceManager = ResourceManager.getInstance();
        setLayout(null);
        setPreferredSize(new Dimension(800, 600));
        loadThemeResources(currentTheme);
        initGameMap();
    }

    /**
     * 加载主题资源
     * @param theme 主题名称
     */
    private void loadThemeResources(String theme) {
        backgroundImage = resourceManager.loadBackground(theme);
        elementImage = resourceManager.loadElement(theme);
        maskImage = resourceManager.loadMask(theme);

        if (elementImage != null) {
            elemWidth = elementImage.getWidth(this) / COL_COUNT;
            elemHeight = elementImage.getHeight(this);
        } else {
            elemWidth = 40;
            elemHeight = 40;
        }
    }

    /**
     * 设置当前主题
     * @param theme 主题名称
     */
    public void setTheme(String theme) {
        this.currentTheme = theme;
        loadThemeResources(theme);
        repaint();
    }

    /**
     * 设置游戏地图数据
     * @param map 地图数组
     */
    public void setGameMap(int[][] map) {
        this.gameMap = map;
        repaint();
    }

    /**
     * 设置选中的第一个点
     * @param point 点坐标
     */
    public void setSelectedFirst(Point point) {
        this.selectedFirst = point;
        repaint();
    }

    /**
     * 设置选中的第二个点
     * @param point 点坐标
     */
    public void setSelectedSecond(Point point) {
        this.selectedSecond = point;
        repaint();
    }

    /**
     * 清除选中状态
     */
    public void clearSelection() {
        this.selectedFirst = null;
        this.selectedSecond = null;
        this.linkPath = null;
        repaint();
    }

    /**
     * 设置连接路径（用于显示消除路径）
     * @param path 连接路径点列表
     */
    public void setLinkPath(List<Point> path) {
        this.linkPath = path;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);
        drawGameMap(g);
        drawSelectedFrame(g);
        drawLinkLine(g);
    }

    /**
     * 绘制背景
     */
    private void drawBackground(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(200, 200, 200));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * 绘制游戏地图
     */
    private void drawGameMap(Graphics g) {
        if (gameMap == null || elementImage == null || maskImage == null) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;

        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COL_COUNT; col++) {
                int elemValue = gameMap[row][col];
                if (elemValue < 0) {
                    continue;
                }

                int x = MAP_START_X + col * elemWidth;
                int y = MAP_START_Y + row * elemHeight;

                // 使用掩码处理绘制元素
                drawElementWithMask(g2d, x, y, elemValue);
            }
        }
    }

    /**
     * 使用掩码绘制元素
     */
    private void drawElementWithMask(Graphics2D g2d, int x, int y, int elemValue) {
        int maskX = 0;
        int maskY = elemValue * elemHeight;

        int elemX = 0;
        int elemY = 0;

        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.drawImage(maskImage, x, y, x + elemWidth, y + elemHeight,
                maskX, maskY, maskX + elemWidth, maskY + elemHeight, this);

        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.drawImage(elementImage, x, y, x + elemWidth, y + elemHeight,
                elemX, elemY, elemX + elemWidth, elemY + elemHeight, this);
    }

    /**
     * 绘制选中框
     */
    private void drawSelectedFrame(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(new Color(233, 43, 43));

        if (selectedFirst != null) {
            int x1 = MAP_START_X + selectedFirst.y * elemWidth;
            int y1 = MAP_START_Y + selectedFirst.x * elemHeight;
            g2d.drawRect(x1, y1, elemWidth, elemHeight);
        }

        if (selectedSecond != null) {
            int x2 = MAP_START_X + selectedSecond.y * elemWidth;
            int y2 = MAP_START_Y + selectedSecond.x * elemHeight;
            g2d.drawRect(x2, y2, elemWidth, elemHeight);
        }
    }

    /**
     * 绘制连接线
     */
    private void drawLinkLine(Graphics g) {
        if (linkPath == null || linkPath.isEmpty()) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(4));
        g2d.setColor(new Color(0, 255, 0));

        Point prev = linkPath.get(0);
        int centerX = MAP_START_X + prev.y * elemWidth + elemWidth / 2;
        int centerY = MAP_START_Y + prev.x * elemHeight + elemHeight / 2;
        g2d.drawLine(centerX, centerY, centerX, centerY);

        for (int i = 1; i < linkPath.size(); i++) {
            Point current = linkPath.get(i);
            int x = MAP_START_X + current.y * elemWidth + elemWidth / 2;
            int y = MAP_START_Y + current.x * elemHeight + elemHeight / 2;
            g2d.drawLine(centerX, centerY, x, y);
            centerX = x;
            centerY = y;
        }
    }

    /**
     * 根据鼠标点击位置获取地图坐标
     * @param mouseX 鼠标X坐标
     * @param mouseY 鼠标Y坐标
     * @return 地图坐标点，null表示点击在地图外
     */
    public Point getMapPosition(int mouseX, int mouseY) {
        int col = (mouseX - MAP_START_X) / elemWidth;
        int row = (mouseY - MAP_START_Y) / elemHeight;

        if (row >= 0 && row < ROW_COUNT && col >= 0 && col < COL_COUNT) {
            if (gameMap != null && row < gameMap.length && col < gameMap[0].length) {
                if (gameMap[row][col] >= 0) {
                    return new Point(row, col);
                }
            }
        }
        return null;
    }
    
    /**
     * 开始游戏
     */
    public void startGame() {
        initGameMap();
        generateRandomMap();
        clearSelection();
        repaint();
    }
    
    /**
     * 初始化游戏地图
     */
    private void initGameMap() {
        gameMap = new int[ROW_COUNT][COL_COUNT];
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COL_COUNT; j++) {
                gameMap[i][j] = -1;
            }
        }
    }
    
    /**
     * 生成随机地图（10行16列，16种图片）
     */
    private void generateRandomMap() {
        int totalCells = ROW_COUNT * COL_COUNT;
        int pairCount = totalCells / 2;
        
        int[] elements = new int[totalCells];
        for (int i = 0; i < pairCount; i++) {
            int type = i % 16;
            elements[i * 2] = type;
            elements[i * 2 + 1] = type;
        }
        
        shuffleArray(elements);
        
        int index = 0;
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COL_COUNT; col++) {
                gameMap[row][col] = elements[index++];
            }
        }
    }
    
    /**
     * 数组随机打乱
     */
    private void shuffleArray(int[] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
    
    /**
     * 暂停游戏
     */
    public void pauseGame() {
        JOptionPane.showMessageDialog(this, "游戏已暂停", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 显示提示
     */
    public void showHint() {
        JOptionPane.showMessageDialog(this, "暂无提示", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 重排地图
     */
    public void shuffleMap() {
        if (gameMap == null) {
            return;
        }
        
        List<Integer> elements = new ArrayList<>();
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COL_COUNT; col++) {
                if (gameMap[row][col] >= 0) {
                    elements.add(gameMap[row][col]);
                }
            }
        }
        
        shuffleList(elements);
        
        int index = 0;
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COL_COUNT; col++) {
                if (gameMap[row][col] >= 0) {
                    gameMap[row][col] = elements.get(index++);
                }
            }
        }
        
        clearSelection();
        repaint();
    }
    
    /**
     * List随机打乱
     */
    private void shuffleList(List<Integer> list) {
        Random random = new Random();
        for (int i = list.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }
}
