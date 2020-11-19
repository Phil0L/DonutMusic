package com.pl.donut.music.voice.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.pl.donut.music.voice.Join;
import com.pl.donut.music.voice.music.handler.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.pagination.MessagePaginationAction;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Play extends Command {

  public static Player player;

  public Play() {
    super.name = "play";
    super.aliases = new String[]{"p", "playnow", "search"};
    super.category = new Category("Sound");
    super.arguments = "[url]";
    super.help = """
        %play [link] : plays the song/playlist of the link. possible links from youtube, soundcloud or bandcamp
        %play playlist [search] : searches for a playlist with spotify. react with eiter the "arrow left" or the "arrow right" to navigate threw the results, react with "musical_note" to play the playlist or react with "+" to queue the playlist. aliases: pl, list
        %play song [search] : searches for a song with spotify. react with eiter the "arrow left" or the "arrow right" to navigate threw the results, react with "musical_note" to play the song or react with "+" to queue the song. aliases: track
        %play user [userID] : lists the users playlists on spotify. react with eiter the "arrow left" or the "arrow right" to navigate threw the playlists, react with "musical_note" to play the playlist or react with "+" to queue the playlist. aliases: account, member
        %play [search] : searches for a song on youtube and plays the first result""";
  }

  @Override
  protected void execute(CommandEvent event) {
    Main.log(event, "Play");
    TextChannel channel = event.getTextChannel();

    if (event.getArgs().isEmpty()) {
      channel.sendMessage("Please provide some arguments, type %help play for further information").queue();
    } else {
      getPlayer(event, player -> {
        Main.info(event, "Player loaded: " + player.name(), Main.ANSI_GREEN);
        Play.player = player;
        String input;
        AudioManager audio = event.getGuild().getAudioManager();
        if (!audio.isConnected())
          new Join().connect(event);

        if (!isUrl(event.getArgs().trim())) {
          Main.info(event, "Searching for songs with " + event.getArgs(), Main.ANSI_BLUE);
          if (Play.player == Player.YOUTUBE) {
            input = "ytsearch: " + event.getArgs();
            PlayerManager manager = PlayerManager.getInstance();
            manager.loadAndPlayTrack(event.getTextChannel(), input);
          } else if (Play.player == Player.SPOTIFY)
            Spotify.searchSpotify(event, event.getArgs());

        } else {
          Main.info(event, "Loading song or playlist " + event.getArgs(), Main.ANSI_BLUE);
          input = event.getArgs().strip();
          PlayerManager manager = PlayerManager.getInstance();
          manager.loadAndPlay(event.getTextChannel(), input);

        }
      });
    }
  }

  private boolean isUrl(String input) {
    try {
      new URL(input);

      return true;
    } catch (MalformedURLException ignored) {
      return false;
    }
  }

  enum Player {
    NONE, YOUTUBE, SPOTIFY
  }

  interface PlayerLoaded {
    void loaded(Player player);
  }

  private void getPlayer(CommandEvent event, PlayerLoaded callback) {
    if (player != Player.NONE && player != null) {
      callback.loaded(player);
      return;
    }

    // reload player
    if (event.getGuild().getTextChannelById(778667444020838401L) != null) {
      TextChannel channel = event.getGuild().getTextChannelById(778667444020838401L);
      assert channel != null;
      MessagePaginationAction history = channel.getIterableHistory();
      history.takeAsync(20).thenApply(list -> {
        String result;
        List<Message> messages = list.stream().filter(message ->
            !message.getEmbeds().isEmpty() &&
                !message.getEmbeds().get(0).isEmpty() &&
                message.getEmbeds().get(0).getAuthor() != null &&
                message.getEmbeds().get(0).getAuthor().getName() != null &&
                message.getEmbeds().get(0).getAuthor().getName().equals("Donut") &&
                message.getEmbeds().get(0).getTitle() != null &&
                message.getEmbeds().get(0).getTitle().equals("Player:")

        ).collect(Collectors.toList());

        // no player found
        if (messages.isEmpty()) {
          EmbedBuilder embed = new EmbedBuilder();
          embed.setColor(Color.BLUE);
          embed.setAuthor("Donut");
          embed.setTitle("Player:");
          embed.setDescription("Youtube");
          embed.setTimestamp(OffsetDateTime.now());
          if (event.getGuild().getTextChannelById(778667444020838401L) != null)
            event.getGuild().getTextChannelById(778667444020838401L).sendMessage(embed.build()).queue();
          callback.loaded(Player.YOUTUBE);
          return true;
        }

        // player found
        result = messages.get(0).getEmbeds().get(0).getDescription();
        assert result != null;
        if (result.equals("Youtube") || result.equals("Spotify"))
          player = (result.equals("Youtube") ? Player.YOUTUBE : Player.SPOTIFY);
        callback.loaded(player);
        return true;
      });
    }
  }

}
