package com.pl.donut.music.voice.record.Listeners;

import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;
import java.util.Arrays;


public class AudioSendListener implements AudioSendHandler {
    public byte[][] voiceData;
    public boolean canProvide;
    int index;

    public AudioSendListener(byte[] data) {
        canProvide = true;
        voiceData = new byte[data.length / 3840][3840];
        for (int i = 0; i < voiceData.length; i++) {
            voiceData[i] = Arrays.copyOfRange(data, i * 3840, i * 3840 + 3840);
        }
    }

    @Override
    public boolean canProvide() {
        return canProvide;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        if (index == voiceData.length - 1)
            canProvide = false;
        return ByteBuffer.wrap(voiceData[index++]);
    }

    @Override
    public boolean isOpus() {
        return false;
    }
}