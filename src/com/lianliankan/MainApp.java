package com.lianliankan;

import com.lianliankan.ui.MainFrame;
import javax.swing.SwingUtilities;
import java.io.File;

public class MainApp {
    public static void main(String[] args) {
        try {
            System.out.println("当前工作目录: " + new File(".").getAbsolutePath());
            System.out.println("项目根目录: " + new File("").getCanonicalPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
