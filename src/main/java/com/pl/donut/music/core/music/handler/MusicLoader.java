package com.pl.donut.music.core.music.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;

public class MusicLoader {

  public void loadOne(TextChannel channel, String trackUrl) {
    PlayerManager player = PlayerManager.getInstance();

    GuildAudioManager musicManager = player.getGuildAudioManager(channel.getGuild());

    player.playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
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
        player.play(musicManager, track);
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
    PlayerManager player = PlayerManager.getInstance();

    GuildAudioManager musicManager = player.getGuildAudioManager(channel.getGuild());

    String positionInQueue = (musicManager.player.getPlayingTrack() == null ? "Now playing" : "" + (musicManager.scheduler.getQueue().size() + 1));
    String timeUntilPlaying = new SimpleDateFormat("mm:ss")
        .format(new Date(musicManager.scheduler.getQueue().parallelStream().mapToLong(AudioTrack::getDuration).sum() +
            (musicManager.player.getPlayingTrack() == null
                ? 0
                : (musicManager.player.getPlayingTrack().getDuration() - musicManager.player.getPlayingTrack().getPosition()))));
    EmbedBuilder embedBuilder = new EmbedBuilder()
        .setTitle(playlist.getName()).setColor(Color.CYAN)
        .setDescription(
            "Playlist added to queue! \n" +
                "0 / " + trackUrls.length + " tracks loaded\n" +
                "Position in queue: " + positionInQueue + " \n" +
                "Estimated time until playing: " + timeUntilPlaying
        ).setTimestamp(OffsetDateTime.now());

    Message message = channel.sendMessageEmbeds(embedBuilder.build()).complete();

    final int[] iLoaded = {0};

    for (String trackUrl : trackUrls)
      player.playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
        @Override
        public void trackLoaded(AudioTrack track) {
          player.play(musicManager, track);
          iLoaded[0] += 1;

          if (message != null){
            embedBuilder.setDescription(
                "Playlist added to queue! \n" +
                    iLoaded[0] + " / " + trackUrls.length + " tracks \n" +
                    "Position in queue: " + positionInQueue + " \n" +
                    "Estimated time until playing: " + timeUntilPlaying
            );
            message.editMessageEmbeds(embedBuilder.build()).queue();
          }
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

  public void loadURL(TextChannel channel, String trackUrl) {
    PlayerManager player = PlayerManager.getInstance();

    GuildAudioManager musicManager = player.getGuildAudioManager(channel.getGuild());
    musicManager.player.setVolume(10);

    player.playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
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
        player.play(musicManager, track);
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
          player.play(musicManager, track);
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

}
