package com.lianliankan.util;

import javax.swing.*;
import java.awt.*;

public class UIUtils {
    public static JButton createButton(String text, int x, int y, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setFont(new Font("黑体", Font.PLAIN, 14));
        return btn;
    }
}
