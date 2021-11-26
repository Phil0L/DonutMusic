package com.pl.donut.music.core.music.commands;

import com.jagrosh.jdautilities.command.Command.Category;
import com.pl.donut.music.core.music.slash.SlashCommand;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public class Controls extends SlashCommand {

  public static final String PlayPause = "play_pause";

  public Controls() {
    super.name = "controls";
    super.category = new Category("Sound");
    super.arguments = "";
    super.help = "/controls : Shows some clickable buttons to control the best of all music bots. It also has a lot of donuts!";
    super.description = "Shows some clickable buttons to control the best of all music bots. It also has a lot of donuts!";
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    Button bPlay = Button.success(PlayPause, Emoji.fromUnicode("U+25B6"));
    registerButton(bPlay);
    event.reply("Test").addActionRow(bPlay).queue();
  }

  @Override
  protected void clicked(ButtonClickEvent event) {
    assert event.getButton() != null;
    assert event.getButton().getId() != null;
    switch (event.getButton().getId()){
      case PlayPause:
        break;
      default:
        break;
    }
  }

}
