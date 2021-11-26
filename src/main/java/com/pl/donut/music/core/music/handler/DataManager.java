package com.pl.donut.music.core.music.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;

public class DataManager {
  private static DataManager INSTANCE;
  private final AudioPlayerManager playerManager;

  public static synchronized DataManager getInstance() {
    if (INSTANCE == null)
      INSTANCE = new DataManager();
    return INSTANCE;
  }

  private DataManager() {
    this.playerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerRemoteSources(playerManager);
    AudioSourceManagers.registerLocalSource(playerManager);
  }


  public void retrieveTrackInfo(Guild guild, String trackUrl, OnTrackDataReceived callback){
    playerManager.loadItemOrdered(guild, trackUrl, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        callback.onTrackData(track);
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        trackLoaded(playlist.getTracks().get(0));
      }

      @Override
      public void noMatches() {
        new RuntimeException("No matches").printStackTrace();
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        exception.printStackTrace();
      }
    });
  }

  public interface OnTrackDataReceived{
    void onTrackData(AudioTrack track);
  }
}