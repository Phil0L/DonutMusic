package com.pl.donut.music.core.music.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.time.OffsetDateTime;

public class Player extends Command{

  public Player() {
    super.name = "player";
    super.aliases = new String[]{};
    super.category = new Command.Category("Sound");
    super.arguments = "<Youtube|Spotify>";
    super.help = "%player Youtube: changes the player to search through Youtube.\n" +
        "%player Spotify: changes the player to search through Spotify.";
  }

  @Override
  protected void execute(CommandEvent event) {
    Main.log(event, "Player");

    if (!(event.getArgs().toLowerCase().contains("youtube") || event.getArgs().toLowerCase().contains("spotify"))){
      EmbedBuilder embed = new EmbedBuilder();
      embed.setColor(Color.RED);
      embed.setTitle("No correct player defined. \nUse '%player Youtube' or '%player Spotify' ");
      embed.setTimestamp(OffsetDateTime.now());
      event.reply(embed.build());
    }
  }
}
