package com.pl.donut.music.voice.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.exceptions.detailed.NotFoundException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import static com.pl.donut.music.Token.clientId;
import static com.pl.donut.music.Token.clientSecret;
import static com.pl.donut.music.Token.refreshToken;

public class Spotify {

    public static ArrayList<SpotifyMessage> spotifyMessages = new ArrayList<>();

    public Spotify() {
        spotifyMessages = new ArrayList<>();
    }


    static void searchSpotify(@NotNull CommandEvent event, @NotNull String search){
        authorizationCodeRefresh_Sync();

        Message message = event.getTextChannel().sendMessage(new EmbedBuilder().setTitle("Waiting for Spotify").build()).complete();

        String[] args = search.split(" ");
        if (args[0].equals("playlist") || args[0].equals("pl") || args[0].equals("list")) {
            SpotifyMessage spotifyMessage = new SpotifyMessage(
                    message,
                    getPlaylists(search.replaceAll("playlist ", "").replaceAll("pl ", "").replaceAll("list ", "")),
                    0,
                    event.getGuild());
            spotifyMessages.add(spotifyMessage);
            int i = spotifyMessages.size() -1;
            if (spotifyMessages.get(i).playlists.length != 0) {
                showPlaylist(spotifyMessages.get(i).message, spotifyMessages.get(i).playlists, spotifyMessages.get(i).current);
            }else{
                showNoResults(spotifyMessages.get(i).message, spotifyMessage);
            }
        }
        if (args[0].equals("user") || args[0].equals("account")) {
            SpotifyMessage spotifyMessage;
            try {
                spotifyMessage = new SpotifyMessage(
                        message,
                        getUserPlaylists(search.replaceAll("user ", "").replaceAll("account ", "")),
                        0,
                        event.getGuild());
                spotifyMessages.add(spotifyMessage);
            } catch (NotFoundException e) {
                message.editMessage(new EmbedBuilder().setColor(Color.RED).setTitle("No user found").build()).queue();
                spotifyMessage = new SpotifyMessage();
            }
            int i = spotifyMessages.size() -1;
            if (spotifyMessages.get(i).playlists.length != 0) {
                showPlaylist(spotifyMessages.get(i).message, spotifyMessages.get(i).playlists, spotifyMessages.get(i).current);
            }else {
                showNoResults(spotifyMessages.get(i).message, spotifyMessage);
            }
        }
        if (args[0].equals("track") || args[0].equals("song")) {
            SpotifyMessage spotifyMessage = new SpotifyMessage(
                    message,
                    getTracks(search.replaceAll("track ", "").replaceAll("song ", "")),
                    0,
                    event.getGuild());
            spotifyMessages.add(spotifyMessage);
            int i = spotifyMessages.size() -1;
            if (spotifyMessages.get(i).tracks.length != 0) {
                showTracks(spotifyMessages.get(i).message, spotifyMessages.get(i).tracks, spotifyMessages.get(i).current);
            }else {
                showNoResults(spotifyMessages.get(i).message, spotifyMessage);
            }
        }
        if (!args[0].equals("playlist") && !args[0].equals("pl") && !args[0].equals("list") && !args[0].equals("user") && !args[0].equals("account") && !args[0].equals("track") && !args[0].equals("song")){
            message.editMessage(new EmbedBuilder().setTitle("You have to provide the type of what you want to search. \nTypes: [playlist/track/user] \nlike this: %spotify [type] [searchkeyword]").setColor(Color.RED).build()).queue();
        }
    }


    public static void showPlaylist(Message message, PlaylistSimplified[] playlists, int which) {

        if (playlists.length == 0){
            message.editMessage(new EmbedBuilder().setTitle("No playlist found").setColor(Color.RED).build()).queue();
        }else {


            message.clearReactions().queue();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setAuthor(playlists[which].getOwner().getDisplayName());
            eb.setTitle(playlists[which].getName());
            eb.setColor(new Color(30, 215, 96));
            if (playlists[which].getImages().length != 0)
                eb.setThumbnail(playlists[which].getImages()[0].getUrl());

            eb.setDescription("**Tracks:**\n" +
                    ((getPlaylistsTracks(playlists[which].getId()).length > 0) ? getPlaylistsTracks(playlists[which].getId())[0].getTrack().getArtists()[0].getName() + " - " + getPlaylistsTracks(playlists[which].getId())[0].getTrack().getName() + "\n" : "") +
                    ((getPlaylistsTracks(playlists[which].getId()).length > 1) ? getPlaylistsTracks(playlists[which].getId())[1].getTrack().getArtists()[0].getName() + " - " + getPlaylistsTracks(playlists[which].getId())[1].getTrack().getName() + "\n" : "") +
                    ((getPlaylistsTracks(playlists[which].getId()).length > 2) ? getPlaylistsTracks(playlists[which].getId())[2].getTrack().getArtists()[0].getName() + " - " + getPlaylistsTracks(playlists[which].getId())[2].getTrack().getName() + "\n" : "") +
                    ((getPlaylistsTracks(playlists[which].getId()).length > 3) ? "..." : "")

            );

            message.editMessage(eb.build()).queue();
            if (which != 0)
                message.addReaction("U+2B05").queue(); // left
            message.addReaction("U+1F3B5").queue(); // play
            message.addReaction("U+2795").queue(); // queue

            if (which != playlists.length)
                message.addReaction("U+27A1").queue(); // right
        }
    }

    public static void showTracks(Message message, Track[] tracks, int which){
        message.clearReactions().queue();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(tracks[which].getArtists()[0].getName());
        eb.setTitle(tracks[which].getName());
        eb.setColor(new Color(30, 215, 96));
        eb.setThumbnail(tracks[which].getAlbum().getImages()[0].getUrl());

        message.editMessage(eb.build()).queue();
        if (which != 0)
            message.addReaction("U+2B05").queue(); // left
        message.addReaction("U+1F3B5").queue(); // play
        message.addReaction("U+2795").queue(); // queue
        if (which != tracks.length -1)
            message.addReaction("U+27A1").queue(); // right

    }

    public static void showNoResults(Message message, SpotifyMessage spotifyMessage){
        message.clearReactions().queue();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("No Results found");
        eb.setColor(Color.RED);

        message.editMessage(eb.build()).queue();

        spotifyMessages.remove(spotifyMessage);

    }

    private static PlaylistSimplified[] getPlaylists(String name) {
        try {
            return spotifyApi.searchItem(name, ModelObjectType.PLAYLIST.getType())
                    .build().execute()
                    .getPlaylists().getItems();

        } catch (IOException | SpotifyWebApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Track[] getTracks(String name){
        try {
            return spotifyApi.searchItem(name, ModelObjectType.TRACK.getType())
                    .build().execute()
                    .getTracks().getItems();
        } catch (IOException | SpotifyWebApiException e) {
            e.printStackTrace();
        }
        return null;
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

    private static PlaylistSimplified[] getUserPlaylists(String id) throws NotFoundException{
        try {
            return spotifyApi.getListOfUsersPlaylists(id).build().execute().getItems();

        } catch (IOException | SpotifyWebApiException e) {
            e.printStackTrace();
        }
        return null;
    }



    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientId).setClientSecret(clientSecret).setRefreshToken(refreshToken).build();
    private static final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();

    public static void authorizationCodeRefresh_Sync() {
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static int getSpotifyMessageById(long id){
        for (int i = 0; i < spotifyMessages.size(); i++){
            if (spotifyMessages.get(i).messageID == id)
                return i;
        }
        return 0;
    }

}


