package com.pl.donut.music.core.music.listener;

import com.pl.donut.music.core.Join;
import com.pl.donut.music.core.music.handler.MusicLoader;
import com.pl.donut.music.core.music.handler.PlayerManager;
import com.pl.donut.music.core.music.spotify.Spotify;
import com.pl.donut.music.core.music.spotify.SpotifyMessage;
import com.pl.donut.music.util.MessageStore;
import com.pl.donut.music.util.ReactionEmoji;
import com.pl.donut.music.util.SavedMessage;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class ReactionListener extends ListenerAdapter {

  @Override
  public void onGenericGuildMessageReaction(@Nonnull GenericGuildMessageReactionEvent event) throws ConcurrentModificationException {
    if (event.getUser() == null || event.getUser().isBot())
      return;
    for (SavedMessage message : MessageStore.allMessages()) {
      if (message.getMessageID() == event.getMessageIdLong() && message instanceof SpotifyMessage) {
        foundSpotifyMessage(event, (SpotifyMessage) message);
      }
    }
  }

  private void foundSpotifyMessage(GenericGuildMessageReactionEvent event, SpotifyMessage spotifyMessage) {
    if (event.getReaction().getReactionEmote().getAsCodepoints().toUpperCase().equals(ReactionEmoji.PLAY)) {
      queue(event, spotifyMessage);
    }

    if (event.getReaction().getReactionEmote().getAsCodepoints().toUpperCase().equals(ReactionEmoji.NEXT)) {
      spotifyMessage.index++;
      spotifyMessage.show();
    }

    if (event.getReaction().getReactionEmote().getAsCodepoints().toUpperCase().equals(ReactionEmoji.PREVIOUS)) {
      spotifyMessage.index--;
      spotifyMessage.show();
    }

  }

  private void queue(GenericGuildMessageReactionEvent event, SpotifyMessage spotifyMessage) {
    new Join().connect(event.getGuild(), event.getMember(), event.getChannel());

    PlayerManager manager = PlayerManager.getInstance();
    if (spotifyMessage.tracks == null) {
      //PLAYLIST
      List<String> queries = new ArrayList<>();
      PlaylistTrack[] tracks = Spotify.getPlaylistsTracks(spotifyMessage.getCurrentPlaylist().getId());
      int maxLoadCount = 100;
      for (int i = 0; i < tracks.length && i < maxLoadCount; i++) {
        String artist = tracks[i].getTrack().getArtists()[0].getName();
        String title = tracks[i].getTrack().getName();
        String search = "ytsearch:" + artist + "-" + title;
        queries.add(search);
      }
//      manager.loadMultiple(event.getChannel(), spotifyMessage.getCurrentPlaylist(), queries.toArray(String[]::new));
      new MusicLoader().loadMultiple(event.getChannel(), spotifyMessage.getCurrentPlaylist(), queries.toArray(String[]::new));
    } else {
      //SINGLE
      Track track = spotifyMessage.getCurrentTrack();
//      manager.loadOne(event.getChannel(), "ytsearch:" + track.getArtists()[0].getName() + "-" + track.getName());
      new MusicLoader().loadOne(event.getChannel(), "ytsearch:" + track.getArtists()[0].getName() + "-" + track.getName());
    }
  }

}
