package com.pl.donut.music.core.music.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
  private static PlayerManager INSTANCE;
  private final AudioPlayerManager playerManager;
  public final Map<Long, GuildMusicManager> musicManagers;

  private PlayerManager() {
    this.musicManagers = new HashMap<>();

    this.playerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerRemoteSources(playerManager);
    AudioSourceManagers.registerLocalSource(playerManager);
  }

  public synchronized GuildMusicManager getGuildMusicManager(Guild guild) {
    long guildId = guild.getIdLong();
    GuildMusicManager musicManager = musicManagers.get(guildId);

    if (musicManager == null) {
      musicManager = new GuildMusicManager(playerManager);
      musicManagers.put(guildId, musicManager);
    }

    guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

    return musicManager;
  }

  public void loadOne(TextChannel channel, String trackUrl) {
    GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());

    playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        channel.sendMessageEmbeds(new EmbedBuilder()
            .setTitle(track.getInfo().title)
            .setColor(Color.CYAN)
            .setAuthor(track.getInfo().author)
            .setDescription(
                "Track added to queue! \n" +
                    "Duration: " + new SimpleDateFormat("mm:ss").format(new Date(track.getDuration())) + " \n" +
                    "Position in queue: " + (musicManager.player.getPlayingTrack() == null ? "Now playing" : (musicManager.scheduler.getQueue().size() + 1)) + " \n" +
                    "Estimated time until playing: " + new SimpleDateFormat("mm:ss").format(new Date(musicManager.scheduler.getQueue().parallelStream().mapToLong(AudioTrack::getDuration).sum() + (musicManager.player.getPlayingTrack() == null ? 0 : (musicManager.player.getPlayingTrack().getDuration() - musicManager.player.getPlayingTrack().getPosition()))))
            )
            .setTimestamp(OffsetDateTime.now())
            .build()).queue();

        // Play the song
        play(musicManager, track);
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        // Only load the first One
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

  public void loadMultiple(TextChannel channel, PlaylistSimplified playlist, String... trackUrls) {
    GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());

    for (String trackUrl : trackUrls)
      playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
        @Override
        public void trackLoaded(AudioTrack track) {
          play(musicManager, track);
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

    channel.sendMessageEmbeds(new EmbedBuilder()
        .setTitle(playlist.getName())
        .setColor(Color.CYAN)
        .setDescription(
            "Playlist added to queue! \n" +
                "" + playlist.getTracks().getTotal() + " tracks \n" +
                "Position in queue: " + (musicManager.player.getPlayingTrack() == null ? "Now playing" : (musicManager.scheduler.getQueue().size() + 1)) + " \n" +
                "Estimated time until playing: " + new SimpleDateFormat("mm:ss").format(new Date(musicManager.scheduler.getQueue().parallelStream().mapToLong(AudioTrack::getDuration).sum() + (musicManager.player.getPlayingTrack() == null ? 0 : (musicManager.player.getPlayingTrack().getDuration() - musicManager.player.getPlayingTrack().getPosition()))))
        )
        .setTimestamp(OffsetDateTime.now())
        .build()).queue();
  }

  public void loadURL(TextChannel channel, String trackUrl) {
    GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());
    musicManager.player.setVolume(10);

    playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        channel.sendMessageEmbeds(new EmbedBuilder()
            .setTitle(track.getInfo().title)
            .setColor(Color.GREEN)
            .setAuthor(track.getInfo().author)
            .setDescription(
                "Track added to queue! \n" +
                    "Duration: " + new SimpleDateFormat("mm:ss").format(new Date(track.getDuration())) + " \n" +
                    "Position in queue: " + (musicManager.player.getPlayingTrack() == null ? "Now playing" : (musicManager.scheduler.getQueue().size() + 1)) + " \n" +
                    "Estimated time until playing: " + new SimpleDateFormat("mm:ss").format(new Date(musicManager.scheduler.getQueue().parallelStream().mapToLong(AudioTrack::getDuration).sum() + (musicManager.player.getPlayingTrack() == null ? 0 : (musicManager.player.getPlayingTrack().getDuration() - musicManager.player.getPlayingTrack().getPosition()))))
            )
            .setTimestamp(OffsetDateTime.now())
            .build()).queue();
        play(musicManager, track);
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        channel.sendMessageEmbeds(new EmbedBuilder()
            .setTitle(playlist.getName())
            .setColor(Color.GREEN)
            .setDescription(
                "All tracks added to queue! \n" +
                    "" + playlist.getTracks().size() + " tracks \n" +
                    "Duration: " + new SimpleDateFormat("mm:ss").format(new Date(playlist.getTracks().parallelStream().mapToLong(AudioTrack::getDuration).sum())) + " \n" +
                    "Position in queue: " + (musicManager.player.getPlayingTrack() == null ? "Now playing" : (musicManager.scheduler.getQueue().size() + 1)) + " \n" +
                    "Estimated time until playing: " + new SimpleDateFormat("mm:ss").format(new Date(musicManager.scheduler.getQueue().parallelStream().mapToLong(AudioTrack::getDuration).sum() + (musicManager.player.getPlayingTrack() == null ? 0 : (musicManager.player.getPlayingTrack().getDuration() - musicManager.player.getPlayingTrack().getPosition()))))
            )
            .setTimestamp(OffsetDateTime.now())
            .build()).queue();
        for (AudioTrack track : playlist.getTracks())
          play(musicManager, track);
      }

      @Override
      public void noMatches() {
        channel.sendMessage("Nothing found by " + trackUrl).queue();
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        channel.sendMessage("Could not play " + trackUrl).queue();
      }
    });

  }

  private void play(GuildMusicManager musicManager, AudioTrack track) {
    musicManager.scheduler.queue(track);
  }

  public static synchronized PlayerManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new PlayerManager();
    }

    return INSTANCE;
  }
}