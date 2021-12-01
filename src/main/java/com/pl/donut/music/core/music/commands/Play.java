package com.pl.donut.music.core.music.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.pl.donut.music.core.Join;
import com.pl.donut.music.core.music.handler.MusicLoader;
import com.pl.donut.music.core.music.handler.PlayerManager;
import com.pl.donut.music.core.music.spotify.Spotify;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.MalformedURLException;
import java.net.URL;

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
        %play [search] : searches for a song""";
  }

  @Override
  protected void execute(CommandEvent event) {
    Main.log(event, "Play");
    TextChannel channel = event.getTextChannel();

    if (event.getArgs().isEmpty()) {
      channel.sendMessage("Please provide some arguments, type %help play for further information").queue();
    } else {
      Player player = getPlayer();
      Main.info(event, "Player loaded: " + player.name(), Main.ANSI_GREEN);
      Play.player = player;
      handlePlayCommand(event, event.getArgs(), player);

    }
  }

  public void handlePlayCommand(CommandEvent event, String input, Player player) {
    AudioManager audio = event.getGuild().getAudioManager();
    if (!audio.isConnected())
      new Join().connect(event);

    if (!isUrl(event.getArgs().trim())) {
      Main.info(event, "Searching for songs: '" + input + "' with: " + player.name(), Main.ANSI_BLUE);
      Spotify.searchSpotify(event, input);
    } else {
      // URL
      Main.info(event, "Loading song or playlist " + input, Main.ANSI_BLUE);
      input = input.strip();
//      PlayerManager manager = PlayerManager.getInstance();
//      manager.loadURL(event.getTextChannel(), input);
      new MusicLoader().loadURL(event.getTextChannel(), input);
    }
  }

  public void handleQuickPlayCommand(GuildMessageReceivedEvent event, String input) {
    AudioManager audio = event.getGuild().getAudioManager();
    if (!audio.isConnected())
      new Join().connect(event);

    Main.info(event.getGuild(), "Playing first song for: '" + input + "' with: " + Player.YOUTUBE.name(), Main.ANSI_BLUE);

    // YOUTUBE
    input = "ytsearch: " + input;
//    PlayerManager manager = PlayerManager.getInstance();
//    manager.loadOne(event.getChannel(), input);
    new MusicLoader().loadOne(event.getChannel(), input);

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

  private Player getPlayer() {
    if (player != Player.NONE && player != null) {
      return player;
    }
    // unknown player -> return default
    player = Player.SPOTIFY;
    return player;
  }
}
