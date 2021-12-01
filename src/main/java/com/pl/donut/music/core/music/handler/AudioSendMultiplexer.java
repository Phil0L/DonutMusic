package com.pl.donut.music.core.music.handler;

import net.dv8tion.jda.api.audio.AudioSendHandler;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

// Used for multiplex mode Switch and Blend
public class AudioSendMultiplexer implements AudioSendHandler {

    private final SendMultiplex sendMultiplex;
    private AudioSendHandler currentProvider;

    public AudioSendMultiplexer(AudioSendHandler... provider) {
        this.sendMultiplex = SendMultiplex.Switch(provider);
    }

    @Override
    public boolean canProvide() {
        if(sendMultiplex.mode == SendMultiplex.MultiplexMode.Switch) {
            for (AudioSendHandler sendHandler : sendMultiplex.handlers){
                if (sendHandler.canProvide()){
                    currentProvider = sendHandler;
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        return currentProvider.provide20MsAudio();
    }

    @Override
    public boolean isOpus() {
        return currentProvider.isOpus();
    }


    public static class SendMultiplex {
        public enum MultiplexMode {
            None,
            Switch,
            Blend;
        }

        public MultiplexMode mode = MultiplexMode.None;
        public AudioSendHandler[] handlers;
        float blendBalance;

        private SendMultiplex() {
        }

        public static SendMultiplex None() {
            return new SendMultiplex();
        }

        public static SendMultiplex Switch(AudioSendHandler... sendHandler) {
            if(sendHandler == null) {
                throw new RuntimeException("Send handler must not be null.");
            }
            SendMultiplex m = new SendMultiplex();
            m.handlers = sendHandler;
            m.mode = MultiplexMode.Switch;
            return m;
        }

        /*
         * Blend ratio between 0 and 1
         */
        public static SendMultiplex Blend(float blendRatio, AudioSendHandler... sendHandlers) {
            throw new UnsupportedOperationException("Blend mode is not supported yet.");

//                if(blendRatio < 0 || blendRatio > 1) {
//                    throw new RuntimeException("Blend ratio must be between 0 and 1.");
//                } else if(sendHandlers == null || sendHandlers.length == 0) {
//                    throw new RuntimeException("Must provide at least one audio send handler.");
//                }
//
//                SendMultiplex m = new SendMultiplex();
//                m.handlers = sendHandlers;
//                m.mode = MultiplexMode.Blend;
//                m.blendBalance = blendRatio;
//                return m;
        }
    }
}
