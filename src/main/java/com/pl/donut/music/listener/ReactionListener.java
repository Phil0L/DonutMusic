package com.pl.donut.music.listener;

import com.pl.donut.music.voice.Join;
import com.pl.donut.music.voice.music.spotify.Spotify;
import com.pl.donut.music.voice.music.spotify.SpotifyMessage;
import com.pl.donut.music.voice.music.handler.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class ReactionListener extends ListenerAdapter {

  @Override
  public void onGenericGuildMessageReaction(@Nonnull GenericGuildMessageReactionEvent event) throws ConcurrentModificationException {
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
      queue(event, spotifyMessage);
    }

    if (event.getReaction().getReactionEmote().getAsCodepoints().equals("U+27a1")) {
      // next
      spotifyMessage.incIndex();
      spotifyMessage.show();
    }

    if (event.getReaction().getReactionEmote().getAsCodepoints().equals("U+2b05")) {
      // prev
      spotifyMessage.decIndex();
      spotifyMessage.show();
    }

    if (event.getReaction().getReactionEmote().getAsCodepoints().equals("U+2795")) {
      queue(event, spotifyMessage);
    }
  }

  private void queue(GenericGuildMessageReactionEvent event, SpotifyMessage spotifyMessage) {
    new Join().connect(event.getGuild(), event.getMember(), event.getChannel());
    event.getChannel().sendMessage(new EmbedBuilder().setColor(new Color(30, 215, 96)).setTitle("Looking for your songs on Youtube...").build()).queue();

    spotifyMessage.getMessage().clearReactions().queue();
    PlayerManager manager = PlayerManager.getInstance();
    if (spotifyMessage.tracks == null) {
      List<String> queries = new ArrayList<>();
      for (int i = 0; i < SpotifyMessage.getPlaylistsTracks(spotifyMessage.getPlaylists()[spotifyMessage.getIndex()].getId()).length && i < 25; i++) {
        String search = "ytsearch:" +
            SpotifyMessage.getPlaylistsTracks(spotifyMessage.getPlaylists()[spotifyMessage.getIndex()].getId())[i].getTrack().getArtists()[0].getName() + "-" +
            SpotifyMessage.getPlaylistsTracks(spotifyMessage.getPlaylists()[spotifyMessage.getIndex()].getId())[i].getTrack().getName();
        queries.add(search);
      }
      manager.loadMultipleFromSpotifyAndPlay(event.getChannel(), spotifyMessage.getPlaylists()[spotifyMessage.getIndex()], queries.toArray(String[]::new));
    } else {
      manager.loadOneFromSpotifyAndPlay(event.getChannel(), "ytsearch:" + spotifyMessage.tracks[spotifyMessage.index].getArtists()[0].getName() + "-" + spotifyMessage.tracks[spotifyMessage.index].getName());
    }
    Spotify.spotifyMessages.remove(spotifyMessage);
  }

}
