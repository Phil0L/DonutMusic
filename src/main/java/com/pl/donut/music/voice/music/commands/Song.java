package com.pl.donut.music.voice.music.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.pl.donut.music.voice.music.handler.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Song extends Command {

  public Song() {
    super.name = "song";
    super.aliases = new String[]{"playing", "now", "songinfo"};
    super.category = new Category("Sound");
    super.arguments = "";
    super.help = "%song : shows the info of the current song";
  }

  @Override
  protected void execute(CommandEvent event) {
    Main.log(event, "Song");

    PlayerManager manager = PlayerManager.getInstance();
    if (manager.getGuildMusicManager(event.getGuild()).player.getPlayingTrack() == null) {
      EmbedBuilder eb = new EmbedBuilder()
          .setColor(Color.RED)
          .setAuthor("There is no current song playing");
      event.reply(eb.build());
      return;
    }
    //TODO: send failed info
    AudioTrackInfo ati = manager.getGuildMusicManager(event.getGuild()).player.getPlayingTrack().getInfo();

    EmbedBuilder eb = new EmbedBuilder()
        .setColor(Color.CYAN)
        .setTitle(ati.title)
        .setAuthor(ati.author)
        .setDescription("Length: " + new SimpleDateFormat("mm:ss").format(new Date(ati.length)) + " ms\nURL: " + ati.uri);
    event.reply(eb.build());
  }
}
