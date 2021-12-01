package com.pl.donut.music.core.music.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
  private static PlayerManager INSTANCE;
  public final AudioPlayerManager playerManager;
  public final Map<Long, GuildAudioManager> musicManagers;

  private PlayerManager() {
    this.musicManagers = new HashMap<>();
    this.playerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerRemoteSources(playerManager);
    AudioSourceManagers.registerLocalSource(playerManager);
  }

  public synchronized GuildAudioManager getGuildAudioManager(Guild guild) {
    long guildId = guild.getIdLong();
    GuildAudioManager musicManager = musicManagers.get(guildId);

    if (musicManager == null) {
      musicManager = new GuildAudioManager(playerManager);
      musicManagers.put(guildId, musicManager);
    }

    guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

    return musicManager;
  }

  public void play(GuildAudioManager musicManager, AudioTrack track) {
    musicManager.scheduler.queue(track);
  }

  public static synchronized PlayerManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new PlayerManager();
    }

    return INSTANCE;
  }
}