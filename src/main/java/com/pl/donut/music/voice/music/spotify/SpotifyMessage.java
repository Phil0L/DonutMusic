package com.pl.donut.music.voice.music.spotify;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.pl.donut.music.voice.music.spotify.Spotify.spotifyApi;

public class SpotifyMessage {
    public Message message;
    public PlaylistSimplified[] playlists;
    public Track[] tracks;
    public long messageID;
    public int index;
    public Guild guild;
    public Timer dieTimer;

    public SpotifyMessage(Message message, PlaylistSimplified[] playlists, int index, Guild guild) {
        this.message = message;
        this.playlists = playlists;
        this.messageID = message.getIdLong();
        this.index = index;
        this.guild = guild;
        this.startTimer();
    }

    public SpotifyMessage(Message message, Track[] tracks, int index, Guild guild) {
        this.message = message;
        this.tracks = tracks;
        this.messageID = message.getIdLong();
        this.index = index;
        this.guild = guild;
        this.startTimer();
    }

    public void incIndex(){
        this.index++;
    }

    public void decIndex(){
        this.index--;
    }

    public Message getMessage() {
        return message;
    }

    public PlaylistSimplified[] getPlaylists() {
        return playlists;
    }

    public int getIndex() {
        return index;
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

    public void show(){
        if (tracks != null){
            if (tracks.length != 0) {
                showTracks(message, tracks, index);
            } else {
                showNoResults();
            }
        }else if (playlists != null){
            if (playlists.length != 0) {
                showPlaylist(message, playlists, index);
            } else {
                showNoResults();
            }
        }
    }

    public void showPlaylist(Message message, PlaylistSimplified[] playlists, int index) {

        if (playlists.length == 0) {
            message.editMessage(new EmbedBuilder().setTitle("No playlist found").setColor(Color.RED).build()).queue();
        } else {
            message.clearReactions().queue();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setAuthor(playlists[index].getOwner().getDisplayName());
            eb.setTitle(playlists[index].getName());
            eb.setColor(new Color(30, 215, 96));
            if (playlists[index].getImages().length != 0)
                eb.setThumbnail(playlists[index].getImages()[0].getUrl());
            eb.setDescription("**Tracks:**\n" +
                //TODO: this takes way too long
                ((getPlaylistsTracks(playlists[index].getId()).length > 0) ? getPlaylistsTracks(playlists[index].getId())[0].getTrack().getArtists()[0].getName() + " - " + getPlaylistsTracks(playlists[index].getId())[0].getTrack().getName() + "\n" : "") +
                ((getPlaylistsTracks(playlists[index].getId()).length > 1) ? getPlaylistsTracks(playlists[index].getId())[1].getTrack().getArtists()[0].getName() + " - " + getPlaylistsTracks(playlists[index].getId())[1].getTrack().getName() + "\n" : "") +
                ((getPlaylistsTracks(playlists[index].getId()).length > 2) ? getPlaylistsTracks(playlists[index].getId())[2].getTrack().getArtists()[0].getName() + " - " + getPlaylistsTracks(playlists[index].getId())[2].getTrack().getName() + "\n" : "") +
                ((getPlaylistsTracks(playlists[index].getId()).length > 3) ? "..." : "")
            );
            message.editMessage(eb.build()).queue();
            message.addReaction("U+1F3B5").queue(); // play
            //message.addReaction("U+2795").queue(); // queue
            if (index != 0)
                message.addReaction("U+2B05").queue(); // left
            if (index >= playlists.length)
                message.addReaction("U+27A1").queue(); // right
        }
    }

    public void showTracks(Message message, Track[] tracks, int index) {
        message.clearReactions().queue();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(tracks[index].getArtists()[0].getName());
        eb.setTitle(tracks[index].getName());
        eb.setColor(new Color(30, 215, 96));
        eb.setThumbnail(tracks[index].getAlbum().getImages()[0].getUrl());

        message.editMessage(eb.build()).queue();
        if (index != 0)
            message.addReaction("U+2B05").queue(); // left
        message.addReaction("U+1F3B5").queue(); // play
        //message.addReaction("U+2795").queue(); // queue
        if (index != tracks.length - 1)
            message.addReaction("U+27A1").queue(); // right

    }

    public void showNoResults() {
        message.clearReactions().queue();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("No Results found");
        eb.setColor(Color.RED);
        message.editMessage(eb.build()).queue();
        Spotify.spotifyMessages.remove(this);

    }

    public static PlaylistTrack[] getPlaylistsTracks(String id) {
        try {
            return spotifyApi.getPlaylistsTracks(id)
                .build().execute()
                .getItems();
        } catch (IOException | SpotifyWebApiException e) {
            e.printStackTrace();
        }
        return null;
    }

}
