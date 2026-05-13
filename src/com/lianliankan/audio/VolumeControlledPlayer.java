package com.lianliankan.audio;

import javax.sound.sampled.*;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDeviceBase;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class VolumeControlledPlayer {
    private AdvancedPlayer player;
    private VolumeAudioDevice audioDevice;
    private boolean playing = false;
    private Thread playThread;
    private float volume = 0.8f;

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        if (audioDevice != null) {
            audioDevice.setVolume(this.volume);
        }
    }

    public float getVolume() {
        return volume;
    }

    public void play(java.io.InputStream inputStream) {
        stop();
        try {
            audioDevice = new VolumeAudioDevice();
            audioDevice.setVolume(volume);
            player = new AdvancedPlayer(inputStream, audioDevice);
            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                    playing = false;
                }
            });
            playing = true;
            playThread = new Thread(() -> {
                try {
                    player.play();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            });
            playThread.setDaemon(true);
            playThread.start();
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        playing = false;
        if (player != null) {
            player.close();
            player = null;
        }
        if (playThread != null && playThread.isAlive()) {
            playThread.interrupt();
            try {
                playThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            playThread = null;
        }
        audioDevice = null;
    }

    public boolean isPlaying() {
        return playing && player != null;
    }

    private static class VolumeAudioDevice extends AudioDeviceBase {
        private SourceDataLine sourceDataLine;
        private float volume = 0.8f;

        public void setVolume(float volume) {
            this.volume = volume;
            if (sourceDataLine != null && sourceDataLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl control = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(Math.max(volume, 0.0001)) / Math.log(10.0) * 20.0);
                dB = Math.max(control.getMinimum(), Math.min(control.getMaximum(), dB));
                control.setValue(dB);
            }
        }

        protected void createSource() throws JavaLayerException {
            try {
                AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
                sourceDataLine.open(format);
                sourceDataLine.start();
                setVolume(volume);
            } catch (LineUnavailableException e) {
                throw new JavaLayerException("无法创建音频源", e);
            }
        }

        protected void writeImpl(short[] samples, int offs, int len) throws JavaLayerException {
            if (sourceDataLine == null) {
                createSource();
            }
            byte[] buffer = new byte[len * 2];
            for (int i = 0; i < len; i++) {
                short sample = samples[offs + i];
                buffer[i * 2] = (byte) (sample & 0xFF);
                buffer[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
            }
            sourceDataLine.write(buffer, 0, buffer.length);
        }

        @Override
        public void close() {
            if (sourceDataLine != null) {
                sourceDataLine.drain();
                sourceDataLine.close();
                sourceDataLine = null;
            }
        }

        @Override
        public int getPosition() {
            if (sourceDataLine != null) {
                return (int) (sourceDataLine.getMicrosecondPosition() / 1000);
            }
            return 0;
        }

        @Override
        public boolean isOpen() {
            return sourceDataLine != null && sourceDataLine.isOpen();
        }

        @Override
        public void flush() {
            if (sourceDataLine != null) {
                sourceDataLine.drain();
            }
        }
    }
}
