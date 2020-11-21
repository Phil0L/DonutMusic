package com.pl.donut.music.voice.music.spotify;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
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

import static com.pl.donut.music.Token.*;

public class Spotify {

  public static ArrayList<SpotifyMessage> spotifyMessages = new ArrayList<>();

  public Spotify() {
    spotifyMessages = new ArrayList<>();
  }


  public static void searchSpotify(@NotNull CommandEvent event, @NotNull String search) {
    authorizationCodeRefresh_Sync();
    Main.info(event, "Spotify Loaded", Main.ANSI_GREEN);
    Message message = event.getTextChannel().sendMessage(new EmbedBuilder().setTitle("Waiting for Spotify").build()).complete();
    Main.info(event, "Starting looking", Main.ANSI_GREEN);
    String[] args = search.split(" ");
    if (args[0].equals("playlist") || args[0].equals("pl") || args[0].equals("list")) {
      // PLAYLIST
      SpotifyMessage spotifyMessage =
          new SpotifyMessage(message, getPlaylists(search.replaceAll("playlist ", "").replaceAll("pl ", "").replaceAll("list ", "")), 0, event.getGuild());
      Main.info(event, "Added Message", Main.ANSI_GREEN);
      spotifyMessages.add(spotifyMessage);
      spotifyMessage.show();
    }
    if (args[0].equals("user") || args[0].equals("account")) {
      // USER
      SpotifyMessage spotifyMessage =
          new SpotifyMessage(message, getUserPlaylists(search.replaceAll("user ", "").replaceAll("account ", "")), 0, event.getGuild());
      Main.info(event, "Added Message", Main.ANSI_GREEN);
      spotifyMessages.add(spotifyMessage);
      spotifyMessage.show();
    }
    if (args[0].equals("tracks") || args[0].equals("track") || args[0].equals("song")) {
      // TRACK
      SpotifyMessage spotifyMessage =
          new SpotifyMessage(message, getTracks(search.replaceAll("track ", "").replaceAll("tracks ", "").replaceAll("song ", "")), 0, event.getGuild());
      Main.info(event, "Added Message", Main.ANSI_GREEN);
      spotifyMessages.add(spotifyMessage);
      spotifyMessage.show();
    }
    if (!args[0].equals("playlist") && !args[0].equals("pl") && !args[0].equals("list") && !args[0].equals("user") && !args[0].equals("account") && !args[0].equals("track") && !args[0].equals("tracks") && !args[0].equals("song")) {
      message.editMessage(new EmbedBuilder().setTitle("You have to provide the type of what you want to search. \nTypes: [playlist/track/user] \nlike this: %spotify [type] [searchkeyword]").setColor(Color.RED).build()).queue();
    }
    Main.info(event, "Showed Message", Main.ANSI_GREEN);
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

  private static Track[] getTracks(String name) {
    try {
      return spotifyApi.searchItem(name, ModelObjectType.TRACK.getType())
          .build().execute()
          .getTracks().getItems();
    } catch (IOException | SpotifyWebApiException e) {
      e.printStackTrace();
    }
    return null;
  }



  private static PlaylistSimplified[] getUserPlaylists(String id) {
    try {
      return spotifyApi.getListOfUsersPlaylists(id).build().execute().getItems();

    } catch (IOException | SpotifyWebApiException e) {
      e.printStackTrace();
    }
    return null;
  }


  public static final SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientId).setClientSecret(clientSecret).setRefreshToken(refreshToken).build();
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

  public static int getSpotifyMessageById(long id) {
    for (int i = 0; i < spotifyMessages.size(); i++) {
      if (spotifyMessages.get(i).messageID == id)
        return i;
    }
    return 0;
  }

}


