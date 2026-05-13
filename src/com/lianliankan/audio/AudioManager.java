package com.lianliankan.audio;

import com.lianliankan.model.SettingsState;
import com.lianliankan.util.ResourcePath;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class AudioManager {
    private static int bgmVolume = 80;
    private static int sfxVolume = 80;
    private static String currentTheme = "fruit";
    private static VolumeControlledPlayer bgmPlayer = null;
    private static boolean playing = false;
    private static Thread bgmThread = null;
    
    public static void setBgmVolume(int vol) {
        bgmVolume = Math.max(0, Math.min(100, vol));
        if (bgmPlayer != null) {
            bgmPlayer.setVolume(bgmVolume / 100.0f);
        }
    }
    
    public static void setSfxVolume(int vol) {
        sfxVolume = Math.max(0, Math.min(100, vol));
    }
    
    public static void setTheme(String theme) {
        currentTheme = theme;
    }
    
    public static int getBgmVolume() {
        return bgmVolume;
    }
    
    public static int getSfxVolume() {
        return sfxVolume;
    }
    
    public static void init() {
        try {
            File file = new File(ResourcePath.getSettingsFile());
            if (file.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                SettingsState settings = (SettingsState) ois.readObject();
                ois.close();
                bgmVolume = settings.getBgmVolume();
                sfxVolume = settings.getSfxVolume();
                String[] themes = {"fruit", "cxk", "mh"};
                int themeIndex = settings.getThemeIndex();
                if (themeIndex >= 0 && themeIndex < themes.length) {
                    currentTheme = themes[themeIndex];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playClick() {
        playSound(ResourcePath.getThemeClickSound(currentTheme));
    }

    public static void playClear() {
        playSound(ResourcePath.getThemeClearSound(currentTheme));
    }

    public static boolean isPlaying() {
        return playing;
    }

    public static void playBgm() {
        playMainBgm();
    }

    public static void stopBgm() {
        playing = false;
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer = null;
        }
        if (bgmThread != null && bgmThread.isAlive()) {
            bgmThread.interrupt();
            try {
                bgmThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            bgmThread = null;
        }
    }
    
    private static void playSound(String path) {
        try {
            InputStream is = null;
            if (ResourcePath.isJarMode()) {
                is = ResourcePath.getResourceAsStream(path);
            } else {
                File file = new File(path);
                if (file.exists()) {
                    is = new FileInputStream(file);
                }
            }
            if (is == null) return;
            
            if (!(is instanceof BufferedInputStream)) {
                is = new BufferedInputStream(is);
            }
            
            AudioInputStream ais = AudioSystem.getAudioInputStream(is);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float gain = sfxVolume / 100.0f;
                float dB = (float) (Math.log(Math.max(gain, 0.0001)) / Math.log(10.0) * 20.0);
                dB = Math.max(control.getMinimum(), Math.min(control.getMaximum(), dB));
                control.setValue(dB);
            }
            
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void playMainBgm() {
        stopBgm();
        playing = true;
        bgmThread = new Thread(() -> {
            while (playing) {
                try {
                    InputStream is = null;
                    if (ResourcePath.isJarMode()) {
                        is = ResourcePath.getResourceAsStream(ResourcePath.BGM);
                    } else {
                        File file = new File(ResourcePath.BGM);
                        if (file.exists()) {
                            is = new FileInputStream(file);
                        }
                    }
                    if (is == null) {
                        break;
                    }
                    bgmPlayer = new VolumeControlledPlayer();
                    bgmPlayer.setVolume(bgmVolume / 100.0f);
                    bgmPlayer.play(is);
                    
                    while (playing && bgmPlayer.isPlaying()) {
                        Thread.sleep(100);
                    }
                    
                    if (bgmPlayer != null) {
                        bgmPlayer.stop();
                        bgmPlayer = null;
                    }
                } catch (Exception e) {
                    if (!(e instanceof InterruptedException)) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        });
        bgmThread.setDaemon(true);
        bgmThread.start();
    }
}
