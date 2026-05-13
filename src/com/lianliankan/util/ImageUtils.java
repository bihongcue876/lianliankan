package com.lianliankan.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    public static BufferedImage loadImage(String relativePath) {
        if (ResourcePath.isJarMode()) {
            return loadImageFromResource(relativePath);
        }
        try {
            File file = new File(relativePath);
            if (!file.exists()) {
                return loadImageFromResource(relativePath);
            }
            return ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            return loadImageFromResource(relativePath);
        }
    }

    private static BufferedImage loadImageFromResource(String relativePath) {
        try {
            InputStream is = ResourcePath.getResourceAsStream(relativePath);
            if (is == null) {
                System.err.println("资源文件不存在: " + relativePath);
                return null;
            }
            return ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage[] buildElementImages(BufferedImage elementBmp, BufferedImage maskBmp, int num) {
        BufferedImage[] elementImages = new BufferedImage[num];
        int elemHeight = 35;
        int elemWidth = 35;
        for (int i = 0; i < num; i++) {
            BufferedImage elemPart = elementBmp.getSubimage(0, i * 40, 40, 40);
            BufferedImage maskPart = maskBmp.getSubimage(0, i * 40, 40, 40);
            BufferedImage result = new BufferedImage(elemWidth, elemHeight, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < elemHeight; y++) {
                for (int x = 0; x < elemWidth; x++) {
                    int srcX = x * 40 / elemWidth;
                    int srcY = y * 40 / elemHeight;
                    int rgb = elemPart.getRGB(srcX, srcY);
                    int maskRgb = maskPart.getRGB(srcX, srcY);
                    int gray = (maskRgb & 0xff) > 128 ? 255 : 0;
                    int alpha = gray;
                    result.setRGB(x, y, (alpha << 24) | (rgb & 0x00ffffff));
                }
            }
            elementImages[i] = result;
        }
        return elementImages;
    }
}
