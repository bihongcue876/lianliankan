package com.lianliankan.model;

import java.io.Serializable;

public class SettingsState implements Serializable {
    private static final long serialVersionUID = 1L;

    private int themeIndex;
    private int volume;

    public SettingsState(int themeIndex, int volume) {
        this.themeIndex = themeIndex;
        this.volume = volume;
    }

    public int getThemeIndex() {
        return themeIndex;
    }

    public int getVolume() {
        return volume;
    }

    public void setThemeIndex(int themeIndex) {
        this.themeIndex = themeIndex;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
