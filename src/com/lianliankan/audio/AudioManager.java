package com.lianliankan.audio;

import com.lianliankan.model.SettingsState;
import com.lianliankan.util.ResourcePath;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import javazoom.jl.player.Player;

public class AudioManager {
    private static int volume = 80;
    private static String currentTheme = "fruit";
    private static Clip bgmClip = null;
    private static Player mp3Player = null;
    private static Thread mp3Thread = null;
    private static boolean playing = false;
    
    public static void setVolume(int vol) {
        volume = vol;
        if (bgmClip != null && bgmClip.isRunning()) {
            FloatControl control = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);
            float gain = volume / 100.0f;
            float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
            control.setValue(dB);
        }
    }
    
    public static void setTheme(String theme) {
        currentTheme = theme;
    }
    
    public static int getVolume() {
        return volume;
    }
    
    public static void init() {
        try {
            File file = new File(ResourcePath.getSettingsFile());
            if (file.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                SettingsState settings = (SettingsState) ois.readObject();
                ois.close();
                volume = settings.getVolume();
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
        if (bgmClip != null) {
            bgmClip.stop();
            bgmClip.close();
            bgmClip = null;
        }
        if (mp3Player != null) {
            mp3Player.close();
            mp3Player = null;
        }
        if (mp3Thread != null && mp3Thread.isAlive()) {
            mp3Thread.interrupt();
            try {
                mp3Thread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            mp3Thread = null;
        }
    }
    
    private static void playSound(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) return;
            
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float gain = volume / 100.0f;
            float dB = (float) (Math.log(Math.max(gain, 0.0001)) / Math.log(10.0) * 20.0);
            control.setValue(dB);
            
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void playMainBgm() {
        stopBgm();
        try {
            File file = new File(ResourcePath.BGM);
            if (!file.exists()) return;
            
            playing = true;
            mp3Thread = new Thread(() -> {
                while (playing) {
                    try {
                        FileInputStream fis = new FileInputStream(ResourcePath.BGM);
                        mp3Player = new Player(fis);
                        mp3Player.play();
                        if (playing) {
                            Thread.sleep(100);
                        }
                    } catch (Exception e) {
                        if (!(e instanceof InterruptedException)) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            });
            mp3Thread.setDaemon(true);
            mp3Thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
