package com.lianliankan.model;

import java.io.Serializable;

public class SettingsState implements Serializable {
    private static final long serialVersionUID = 2L;

    private int themeIndex;
    private int bgmVolume;
    private int sfxVolume;

    public SettingsState(int themeIndex, int bgmVolume, int sfxVolume) {
        this.themeIndex = themeIndex;
        this.bgmVolume = bgmVolume;
        this.sfxVolume = sfxVolume;
    }

    public int getThemeIndex() {
        return themeIndex;
    }

    public int getBgmVolume() {
        return bgmVolume;
    }

    public int getSfxVolume() {
        return sfxVolume;
    }

    public void setThemeIndex(int themeIndex) {
        this.themeIndex = themeIndex;
    }

    public void setBgmVolume(int bgmVolume) {
        this.bgmVolume = bgmVolume;
    }

    public void setSfxVolume(int sfxVolume) {
        this.sfxVolume = sfxVolume;
    }
}
