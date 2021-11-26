package com.pl.donut.music.core.music.spotify;

import com.pl.donut.music.util.SavedMessage;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public class SpotifyMessage extends SavedMessage {
  public static final String ID_PLAY = "button_action_play";
  public static final String ID_NEXT = "button_action_next";
  public static final String ID_PREVIOUS = "button_action_previous";


  public PlaylistSimplified[] playlists;
  public Track[] tracks;

  public int index;

  public SpotifyMessage(Message message, PlaylistSimplified[] playlists) {
    super(message);
    this.playlists = playlists;
    this.index = 0;
  }

  public SpotifyMessage(Message message, Track[] tracks) {
    super(message);
    this.tracks = tracks;
    this.index = 0;
  }

  public PlaylistSimplified[] getPlaylists() {
    return playlists;
  }

  public Track[] getTracks() {
    return tracks;
  }

  public Track getCurrentTrack(){
    return tracks[index];
  }

  public PlaylistSimplified getCurrentPlaylist(){
    return playlists[index];
  }

  public int getIndex() {
    return index;
  }

  public Guild getGuild() {
    return getMessage().getGuild();
  }

  public void show(){
    new SpotifyMessageDisplayer(this).show();
  }


}
