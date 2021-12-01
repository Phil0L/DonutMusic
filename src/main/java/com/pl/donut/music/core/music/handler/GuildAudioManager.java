package com.pl.donut.music.core.music.handler;

import com.pl.donut.music.core.voice.vocalcord.TTSEngine;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.audio.AudioSendHandler;

/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class GuildAudioManager {
    /**
     * Audio player for the guild.
     */
    public final AudioPlayer player;

    /**
     * Text to speech player for the guild
     */
    public final TTSEngine ttsEngine;
    /**
     * Track scheduler for the player.
     */
    public final TrackScheduler scheduler;

    /**
     * Creates a player and a track scheduler.
     * @param manager Audio player manager to use for creating the player.
     */
    public GuildAudioManager(AudioPlayerManager manager) {
        this.player = manager.createPlayer();
        this.scheduler = new TrackScheduler(player);
        this.player.addListener(scheduler);
        this.ttsEngine = new TTSEngine();
    }

    /**
     * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    public AudioSendHandler getSendHandler() {
        return new AudioSendMultiplexer(ttsEngine, new MusicSendHandler(player));
    }
}
