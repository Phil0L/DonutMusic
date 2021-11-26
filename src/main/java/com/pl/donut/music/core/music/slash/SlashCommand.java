package com.pl.donut.music.core.music.slash;

import com.jagrosh.jdautilities.command.Command.Category;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public abstract class SlashCommand {

  protected String name;
  protected Category category;
  protected String arguments;
  protected String help;
  protected String description;

  protected abstract void execute(SlashCommandEvent event);

  protected abstract void clicked(ButtonClickEvent event);

  protected void registerButton(Button button){
    SlashCommandClient slashCommandClient = SlashCommandClient.getInstance();
    slashCommandClient.buttonManager.cache(button, this);
  }
}
