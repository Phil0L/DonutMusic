package com.pl.donut.music.voice.music.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.pagination.MessagePaginationAction;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
      return;
    }

    if (event.getGuild().getTextChannelById(778667444020838401L) != null) {
      TextChannel channel = event.getGuild().getTextChannelById(778667444020838401L);
      assert channel != null;
      MessagePaginationAction history = channel.getIterableHistory();
      history.takeAsync(20).thenApply(list -> {

        String player = "";
        if (event.getArgs().toLowerCase().contains("youtube"))
          player = "Youtube";
        if (event.getArgs().toLowerCase().contains("spotify"))
          player = "Spotify";

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
          embed.setDescription(player);
          embed.setTimestamp(OffsetDateTime.now());
          if (event.getGuild().getTextChannelById(778667444020838401L) != null)
            event.getGuild().getTextChannelById(778667444020838401L).sendMessage(embed.build()).queue();
          embed = new EmbedBuilder();
          embed.setColor(Color.GREEN);
          embed.setTitle("Player changed! \nNow using " + player + " as a player");
          embed.setTimestamp(OffsetDateTime.now());
          event.reply(embed.build());
          return true;
        }

        // player found
        Message message = messages.get(0);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.BLUE);
        embed.setAuthor("Donut");
        embed.setTitle("Player:");
        embed.setDescription(player);
        embed.setTimestamp(OffsetDateTime.now());
        message.editMessage(embed.build()).queue();
        Play.player = player.equals("Youtube") ? Play.Player.YOUTUBE : Play.Player.SPOTIFY;
        embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setTitle("Player changed! \nNow using " + player + " as a player");
        embed.setTimestamp(OffsetDateTime.now());
        event.reply(embed.build());
        return true;
      });
    }
  }
}
