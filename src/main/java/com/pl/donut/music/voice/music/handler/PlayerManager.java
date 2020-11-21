package com.pl.donut.music.voice.music.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
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

  public void loadOneFromSpotifyAndPlay(TextChannel channel, String trackUrl) {
    GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());
    musicManager.player.setVolume(10);

    playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        channel.sendMessage(new EmbedBuilder()
            .setTitle(track.getInfo().title)
            .setColor(Color.GREEN)
            .setAuthor(track.getInfo().author)
            .setDescription(
                "Track added to queue! \n" +
                    "Duration: " + new SimpleDateFormat("mm:ss").format(new Date(track.getDuration())) + " \n" +
                    "Position in queue: " + (musicManager.player.getPlayingTrack() == null ? "Now playing" : (musicManager.scheduler.queue.size() + 1)) + " \n" +
                    "Estimated time until playing: " + new SimpleDateFormat("mm:ss").format(new Date(musicManager.scheduler.queue.parallelStream().mapToLong(AudioTrack::getDuration).sum() + (musicManager.player.getPlayingTrack() == null ? 0 : (musicManager.player.getPlayingTrack().getDuration() - musicManager.player.getPlayingTrack().getPosition()))))
            )
            .setTimestamp(OffsetDateTime.now())
            .build()).queue();
        play(musicManager, track);
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        trackLoaded(playlist.getTracks().get(0));
      }

      @Override
      public void noMatches() {

      }

      @Override
      public void loadFailed(FriendlyException exception) {

      }
    });
  }

  public void loadMultipleFromSpotifyAndPlay(TextChannel channel, PlaylistSimplified playlist, String... trackUrls) {
    GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());
    musicManager.player.setVolume(10);

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

        }

        @Override
        public void loadFailed(FriendlyException exception) {

        }
      });
    channel.sendMessage(new EmbedBuilder()
        .setTitle(playlist.getName())
        .setColor(Color.GREEN)
        .setDescription(
            "All tracks added to queue! \n" +
                "" + playlist.getTracks().getTotal() + " tracks \n" +
                "Position in queue: " + (musicManager.player.getPlayingTrack() == null ? "Now playing" : (musicManager.scheduler.queue.size() + 1)) + " \n" +
                "Estimated time until playing: " + new SimpleDateFormat("mm:ss").format(new Date(musicManager.scheduler.queue.parallelStream().mapToLong(AudioTrack::getDuration).sum() + (musicManager.player.getPlayingTrack() == null ? 0 : (musicManager.player.getPlayingTrack().getDuration() - musicManager.player.getPlayingTrack().getPosition()))))
        )
        .setTimestamp(OffsetDateTime.now())
        .build()).queue();
  }

  public void loadAndPlay(TextChannel channel, String trackUrl) {
    GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());
    musicManager.player.setVolume(10);

    playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        channel.sendMessage(new EmbedBuilder()
            .setTitle(track.getInfo().title)
            .setColor(Color.GREEN)
            .setAuthor(track.getInfo().author)
            .setDescription(
                "Track added to queue! \n" +
                    "Duration: " + new SimpleDateFormat("mm:ss").format(new Date(track.getDuration())) + " \n" +
                    "Position in queue: " + (musicManager.player.getPlayingTrack() == null ? "Now playing" : (musicManager.scheduler.queue.size() + 1)) + " \n" +
                    "Estimated time until playing: " + new SimpleDateFormat("mm:ss").format(new Date(musicManager.scheduler.queue.parallelStream().mapToLong(AudioTrack::getDuration).sum() + (musicManager.player.getPlayingTrack() == null ? 0 : (musicManager.player.getPlayingTrack().getDuration() - musicManager.player.getPlayingTrack().getPosition()))))
            )
            .setTimestamp(OffsetDateTime.now())
            .build()).queue();
        play(musicManager, track);
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        channel.sendMessage(new EmbedBuilder()
            .setTitle(playlist.getName())
            .setColor(Color.GREEN)
            .setDescription(
                "All tracks added to queue! \n" +
                    "" + playlist.getTracks().size() + " tracks \n" +
                    "Duration: " + new SimpleDateFormat("mm:ss").format(new Date(playlist.getTracks().parallelStream().mapToLong(AudioTrack::getDuration).sum())) + " \n" +
                    "Position in queue: " + (musicManager.player.getPlayingTrack() == null ? "Now playing" : (musicManager.scheduler.queue.size() + 1)) + " \n" +
                    "Estimated time until playing: " + new SimpleDateFormat("mm:ss").format(new Date(musicManager.scheduler.queue.parallelStream().mapToLong(AudioTrack::getDuration).sum() + (musicManager.player.getPlayingTrack() == null ? 0 : (musicManager.player.getPlayingTrack().getDuration() - musicManager.player.getPlayingTrack().getPosition()))))
            )
            .setTimestamp(OffsetDateTime.now())
            .build()).queue();
        for (AudioTrack track : playlist.getTracks())
          play(musicManager, track);
      }

      @Override
      public void noMatches() {
        channel.sendMessage(new EmbedBuilder().setTitle("Nothing found by " + trackUrl).setColor(Color.RED).build()).queue();
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        channel.sendMessage(new EmbedBuilder().setTitle("Could not play " + trackUrl).setColor(Color.RED).build()).queue();
      }
    });

  }

  public void loadOneAndPlay(TextChannel channel, String trackUrl) {
    GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());
    musicManager.player.setVolume(10);

    playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        channel.sendMessage(new EmbedBuilder()
            .setTitle(track.getInfo().title)
            .setColor(Color.GREEN)
            .setAuthor(track.getInfo().author)
            .setDescription(
                "Track added to queue! \n" +
                    "Duration: " + new SimpleDateFormat("mm:ss").format(new Date(track.getDuration())) + " \n" +
                    "Position in queue: " + (musicManager.player.getPlayingTrack() == null ? "Now playing" : (musicManager.scheduler.queue.size() + 1)) + " \n" +
                    "Estimated time until playing: " + new SimpleDateFormat("mm:ss").format(new Date(musicManager.scheduler.queue.parallelStream().mapToLong(AudioTrack::getDuration).sum() + (musicManager.player.getPlayingTrack() == null ? 0 : (musicManager.player.getPlayingTrack().getDuration() - musicManager.player.getPlayingTrack().getPosition()))))
            )
            .setTimestamp(OffsetDateTime.now())
            .build()).queue();
        play(musicManager, track);
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        //TODO: send message to decide on the song
        trackLoaded(playlist.getTracks().get(0));
      }

      @Override
      public void noMatches() {
        channel.sendMessage(new EmbedBuilder().setTitle("Nothing found by " + trackUrl).setColor(Color.RED).build()).queue();
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        channel.sendMessage(new EmbedBuilder().setTitle("Could not play " + trackUrl).setColor(Color.RED).build()).queue();
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