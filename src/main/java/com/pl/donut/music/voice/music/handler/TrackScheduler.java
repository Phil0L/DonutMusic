package com.pl.donut.music.voice.music.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
    private AudioPlayer player;
    //private BlockingQueue<AudioTrack> queue;
    public ArrayList<AudioTrack> queue;
    public AudioTrack lastPlayed;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new ArrayList<>();

    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        // Calling startTrack with noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.add(track);
        }
        if (player.getPlayingTrack() != null)
            lastPlayed = player.getPlayingTrack();
    }

    public void queueFront(AudioTrack track) {
        if (!player.startTrack(track, false)) {
            queue.add(0, track);
        }
        if (player.getPlayingTrack() != null)
            lastPlayed = player.getPlayingTrack();
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        if (queue.isEmpty())
            player.startTrack(null, false);
        else {
            AudioTrack track = queue.get(0);
            player.startTrack(track, false);
            queue.remove(0);
        }

        if (player.getPlayingTrack() != null)
            lastPlayed = player.getPlayingTrack();
    }

    public void shuffle() {
        Collections.shuffle(queue);
    }

    public ArrayList<AudioTrack> getList() {
        return queue;
    }


    public void playNow(AudioTrack track) {
        this.player.startTrack(track, false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }


}