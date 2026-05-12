package com.lianliankan.util;

import java.io.File;

public class ResourcePath {
    private static final String BASE_DIR;

    static {
        String base = detectBaseDir();
        BASE_DIR = base;
    }

    private static String detectBaseDir() {
        String[] candidates = {
            "src/resource/",
            "lianlk/src/resource/",
            "resource/"
        };
        for (String candidate : candidates) {
            File testFile = new File(candidate + "picture/fruit_bg.bmp");
            if (testFile.exists()) {
                return candidate;
            }
        }
        return "src/resource/";
    }

    private static String detectSaveDir() {
        String baseForSave = BASE_DIR.contains("lianlk/") ? "lianlk/" : "";
        String dirPath = baseForSave + "src/bak";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath.endsWith("/") ? dirPath : dirPath + "/";
    }

    public static final String PICTURE_DIR = BASE_DIR + "picture/";
    public static final String AUDIO_DIR = BASE_DIR + "audio/";
    public static final String SOURCE_DIR = BASE_DIR + "source/";
    public static final String SAVE_DIR;
    public static final String CONFIG_DIR = BASE_DIR + "config/";

    static {
        SAVE_DIR = detectSaveDir();
    }

    public static final String ICON = SOURCE_DIR + "LLK.png";
    public static final String MAIN_BG = SOURCE_DIR + "llk_main.bmp";
    public static final String LEVEL_BG = SOURCE_DIR + "level.bmp";
    public static final String BGM = AUDIO_DIR + "bgm.mp3";

    public static String getThemeBg(String theme) { return PICTURE_DIR + theme + "_bg.bmp"; }
    public static String getThemeElement(String theme) { return PICTURE_DIR + theme + "_element.bmp"; }
    public static String getThemeMask(String theme) { return PICTURE_DIR + theme + "_mask.bmp"; }
    public static String getThemeClickSound(String theme) { return AUDIO_DIR + theme + "_click.wav"; }
    public static String getThemeClearSound(String theme) { return AUDIO_DIR + theme + "_clear.wav"; }
    public static String getThemeBgm(String theme) { return AUDIO_DIR + theme + "_bgm.wav"; }
    public static String getSaveFile(int mode) { return SAVE_DIR + "mode_" + mode + ".sav"; }
    public static String getHighScoreFile(int mode) { return SAVE_DIR + "highscores_" + mode + ".sav"; }
    public static String getSettingsFile() { return SAVE_DIR + "settings.sav"; }

    public static boolean exists(String path) {
        return new File(path).exists();
    }

    public static boolean hasAnySaveFile() {
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) return false;
        File[] files = dir.listFiles((d, name) -> name.endsWith(".sav") && name.startsWith("mode_"));
        return files != null && files.length > 0;
    }
}
