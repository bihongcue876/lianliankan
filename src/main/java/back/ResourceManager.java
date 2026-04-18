package back;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 资源管理器 - 统一管理游戏中的图片和音频资源
 * 采用单例模式，确保全局只有一个资源管理器实例
 * 使用缓存机制避免重复加载相同的资源
 */
public class ResourceManager {
    /** 单例实例 */
    private static ResourceManager instance;
    /** 资源文件的基础路径 */
    private final String resourcePath;
    /** 图片资源缓存，避免重复从磁盘读取 */
    private final Map<String, Image> imageCache = new HashMap<>();

    static {
        // Java 9+ 可以使用 addWarningListener 过滤 PNG 警告
        // Java 8 环境下忽略该功能
    }

    /**
     * 私有构造函数，防止外部直接实例化
     */
    private ResourceManager() {
        // 兼容 IDE 和命令行运行环境
        String basePath = System.getProperty("user.dir");
        File resourcesDir = new File(basePath, "src/main/resources");
        if (resourcesDir.exists()) {
            resourcePath = "src/main/resources/";
        } else {
            resourcePath = "res/";
        }
    }

    /**
     * 获取资源管理器的唯一实例（线程不安全，适合单线程环境）
     * @return ResourceManager 实例
     */
    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    /**
     * 获取资源基础路径
     * @return 资源路径字符串
     */
    public String getResourcePath() {
        return resourcePath;
    }

    /**
     * 加载图片资源
     * @param category 图片分类目录（如 "picture"）
     * @param filename 图片文件名（如 "fruit_bg.bmp"）
     * @return Image 对象，加载失败返回 null
     */
    public Image loadImage(String category, String filename) {
        // 构建缓存键值
        String key = category + "/" + filename;
        
        // 先从缓存中查找，命中则直接返回
        if (imageCache.containsKey(key)) {
            return imageCache.get(key);
        }

        // 拼接完整路径并尝试加载图片
        String fullPath = resourcePath + category + "/" + filename;
        try {
            Image image = ImageIO.read(new File(fullPath));
            imageCache.put(key, image);
            return image;
        } catch (IOException e) {
            System.err.println("图片加载失败: " + fullPath);
            return null;
        }
    }

    /**
     * 加载背景图片
     * @param theme 主题名称（如 "fruit"、"cxk"、"mh"）
     * @return 背景 Image 对象
     */
    public Image loadBackground(String theme) {
        return loadImage("picture", theme + "_bg.bmp");
    }

    /**
     * 加载元素图片
     * @param theme 主题名称（如 "fruit"、"cxk"、"mh"）
     * @return 元素 Image 对象
     */
    public Image loadElement(String theme) {
        return loadImage("picture", theme + "_element.bmp");
    }

    /**
     * 加载掩码图片
     * @param theme 主题名称（如 "fruit"、"cxk"、"mh"）
     * @return 掩码 Image 对象
     */
    public Image loadMask(String theme) {
        return loadImage("picture", theme + "_mask.bmp");
    }

    /**
     * 加载音频资源
     * @param filename 音频文件名
     * @return AudioInputStream 对象，加载失败返回 null
     */
    public AudioInputStream loadAudio(String filename) {
        String fullPath = resourcePath + "audio/" + filename;
        try {
            return AudioSystem.getAudioInputStream(new File(fullPath));
        } catch (Exception e) {
            System.err.println("音频加载失败: " + fullPath);
            return null;
        }
    }
}
