package com.pl.donut.music.listener;

import com.pl.donut.music.voice.music.Spotify;
import com.pl.donut.music.voice.music.SpotifyMessage;
import com.pl.donut.music.voice.music.handler.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.annotation.Nonnull;
import java.awt.*;

public class ReactionListener extends ListenerAdapter {

  @Override
  public void onGenericGuildMessageReaction(@Nonnull GenericGuildMessageReactionEvent event) {
    if (event.getUser() == null || event.getUser().isBot())
      return;
    for (SpotifyMessage message : Spotify.spotifyMessages) {
      if (message.messageID == event.getMessageIdLong()) {
        foundSpotifyMessage(event, message);
      }
    }
  }

  private void foundSpotifyMessage(GenericGuildMessageReactionEvent event, SpotifyMessage spotifyMessage) {
    spotifyMessage.restartTimer();

    if (event.getReaction().getReactionEmote().getAsCodepoints().equals("U+1f3b5")) {
      play(spotifyMessage, event);
    }

    if (event.getReaction().getReactionEmote().getAsCodepoints().equals("U+27a1")) {
      // next
      if (spotifyMessage.tracks == null)
        Spotify.showPlaylist(spotifyMessage.getMessage(), spotifyMessage.getPlaylists(), spotifyMessage.getCurrent() + 1);
      else
        Spotify.showTracks(spotifyMessage.message, spotifyMessage.tracks, spotifyMessage.current + 1);
      spotifyMessage.incCurrent();
    }

    if (event.getReaction().getReactionEmote().getAsCodepoints().equals("U+2b05")) {
      // prev
      if (spotifyMessage.tracks == null)
        Spotify.showPlaylist(spotifyMessage.getMessage(), spotifyMessage.getPlaylists(), spotifyMessage.getCurrent() - 1);
      else
        Spotify.showTracks(spotifyMessage.message, spotifyMessage.tracks, spotifyMessage.current - 1);
      spotifyMessage.decCurrent();
    }

    if (event.getReaction().getReactionEmote().getAsCodepoints().equals("U+2795")) {
      queue(event, spotifyMessage);
    }
  }

  private void queue(GenericGuildMessageReactionEvent event, SpotifyMessage spotifyMessage) {
    AudioManager audio = event.getGuild().getAudioManager();
    if (!audio.isConnected())
      try {
        audio.openAudioConnection(event.getMember().getVoiceState().getChannel());
      } catch (Exception e) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setTitle("You have to be in a Voicechannel");
        event.getChannel().sendMessage(eb.build()).queue();
      }

    spotifyMessage.getMessage().clearReactions().queue();
    PlayerManager manager = PlayerManager.getInstance();
    if (spotifyMessage.tracks == null) {
      for (int i = 0; i < Spotify.getPlaylistsTracks(spotifyMessage.getPlaylists()[spotifyMessage.getCurrent()].getId()).length && i < 25; i++) {
        String search = "ytsearch:" + Spotify.getPlaylistsTracks(spotifyMessage.getPlaylists()[spotifyMessage.getCurrent()].getId())[i].getTrack().getArtists()[0].getName() + "-" + Spotify.getPlaylistsTracks(spotifyMessage.getPlaylists()[spotifyMessage.getCurrent()].getId())[i].getTrack().getName();
        manager.loadAndQueueSpotify(event.getChannel(), search);
      }
      event.getChannel().sendMessage(new EmbedBuilder().setColor(new Color(30, 215, 96)).setTitle("Queued playlist").build()).queue();
    } else {
      manager.loadAndQueueSpotify(event.getChannel(), "ytsearch:" + spotifyMessage.tracks[spotifyMessage.current].getArtists()[0].getName() + "-" + spotifyMessage.tracks[spotifyMessage.current].getName());
      event.getChannel().sendMessage(new EmbedBuilder().setColor(new Color(30, 215, 96)).setTitle("Queued track").build()).queue();
    }
    Spotify.spotifyMessages.remove(spotifyMessage);
  }


  private void play(SpotifyMessage spotifyMessage, GenericGuildMessageReactionEvent event) {
    AudioManager audio = event.getGuild().getAudioManager();
    if (!audio.isConnected())
      try {
        audio.openAudioConnection(event.getMember().getVoiceState().getChannel());
      } catch (Exception e) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setTitle("You have to be in a Voicechannel");
        event.getChannel().sendMessage(eb.build()).queue();
      }

    spotifyMessage.getMessage().clearReactions().queue();
    PlayerManager manager = PlayerManager.getInstance();
    if (spotifyMessage.tracks == null) {
      String search = "ytsearch:" + Spotify.getPlaylistsTracks(spotifyMessage.getPlaylists()[spotifyMessage.getCurrent()].getId())[0].getTrack().getArtists()[0].getName() + "-" + Spotify.getPlaylistsTracks(spotifyMessage.getPlaylists()[spotifyMessage.getCurrent()].getId())[0].getTrack().getName();
      manager.loadAndPlaySpotify(event.getChannel(), search);
      for (int i = 0; i < Spotify.getPlaylistsTracks(spotifyMessage.getPlaylists()[spotifyMessage.getCurrent()].getId()).length || i < 25; i++) {
        search = "ytsearch:" + Spotify.getPlaylistsTracks(spotifyMessage.getPlaylists()[spotifyMessage.getCurrent()].getId())[i].getTrack().getArtists()[0].getName() + "-" + Spotify.getPlaylistsTracks(spotifyMessage.getPlaylists()[spotifyMessage.getCurrent()].getId())[i].getTrack().getName();
        manager.loadAndQueueFirstSpotify(event.getChannel(), search);
      }
      event.getChannel().sendMessage(new EmbedBuilder().setColor(new Color(30, 215, 96)).setTitle("Queued playlist").build()).queue();
    } else {
      manager.loadAndPlaySpotify(event.getChannel(), "ytsearch:" + spotifyMessage.tracks[spotifyMessage.getCurrent()].getArtists()[0].getName() + "-" + spotifyMessage.tracks[spotifyMessage.current].getName());
      event.getChannel().sendMessage(new EmbedBuilder().setColor(new Color(30, 215, 96)).setTitle("Queued track").build()).queue();
    }
    Spotify.spotifyMessages.remove(spotifyMessage);
  }

}
