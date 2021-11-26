package com.pl.donut.music.core.music.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.pl.donut.music.core.music.handler.GuildMusicManager;
import com.pl.donut.music.core.music.handler.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Song extends Command {

  public Song() {
    super.name = "song";
    super.aliases = new String[]{"playing", "now", "songinfo", "info"};
    super.category = new Category("Sound");
    super.arguments = "";
    super.help = "%song : shows the info of the current song";
  }

  @Override
  protected void execute(CommandEvent event) {
    Main.log(event, "Song");

    PlayerManager manager = PlayerManager.getInstance();
    GuildMusicManager guildMusicManager = manager.getGuildMusicManager(event.getGuild());
    AudioPlayer audioPlayer = guildMusicManager.player;
    AudioTrack track = audioPlayer.getPlayingTrack();
    if (track == null) {
      event.reply("There is no current song playing");
      return;
    }
    AudioTrackInfo trackInfo = track.getInfo();
    EmbedBuilder eb = new EmbedBuilder()
        .setColor(Color.CYAN)
        .setTitle(trackInfo.title)
        .setAuthor(trackInfo.author)
        .setImage("https://raw.githubusercontent.com/Phil0L/DonutMusic/master/imgs/Donut-Bot-Playing.png")
        .setDescription(
            new SimpleDateFormat("mm:ss").format(new Date(track.getPosition())) + " / " +
                new SimpleDateFormat("mm:ss").format(new Date(trackInfo.length)) + "\n");
    event.reply(eb.build());
  }
}
