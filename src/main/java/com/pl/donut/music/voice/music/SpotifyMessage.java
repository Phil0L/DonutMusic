package com.pl.donut.music.voice.music;

import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.util.Timer;
import java.util.TimerTask;

public class SpotifyMessage {
    public Message message;
    public PlaylistSimplified[] playlists;
    public Track[] tracks;
    public long messageID;
    public int current;
    public Guild guild;
    public Timer dieTimer;

    public SpotifyMessage(Message message, PlaylistSimplified[] playlists, int current, Guild guild) {
        this.message = message;
        this.playlists = playlists;
        this.messageID = message.getIdLong();
        this.current = current;
        this.guild = guild;
        this.startTimer();
    }

    public SpotifyMessage(Message message, Track[] tracks, int current, Guild guild) {
        this.message = message;
        this.tracks = tracks;
        this.messageID = message.getIdLong();
        this.current = current;
        this.guild = guild;
        this.startTimer();
    }

    public SpotifyMessage() {

    }

    public void incCurrent(){
        this.current++;
    }

    public void decCurrent(){
        this.current--;
    }

    public Message getMessage() {
        return message;
    }

    public PlaylistSimplified[] getPlaylists() {
        return playlists;
    }

    public long getMessageID() {
        return messageID;
    }

    public int getCurrent() {
        return current;
    }

    public Guild getGuild() {
        return guild;
    }

    public void startTimer(){
        this.dieTimer = new Timer(false);
        this.dieTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                message.clearReactions().queue();
                Spotify.spotifyMessages.remove(SpotifyMessage.this);
                dieTimer.cancel();
            }
        }, 60 * 1000);
    }

    public void restartTimer(){
        this.dieTimer.cancel();
        this.startTimer();
    }
}
