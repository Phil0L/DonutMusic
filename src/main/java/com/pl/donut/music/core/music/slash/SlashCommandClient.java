package com.pl.donut.music.core.music.slash;

import com.pl.donut.music.Main;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

public class SlashCommandClient extends ListenerAdapter {
  public static SlashCommandClient INSTANCE;

  public SlashCommand[] slashCommands;
  public ButtonManager buttonManager;

  public static SlashCommandClient getInstance(){
    return INSTANCE;
  }

  public SlashCommandClient(SlashCommand[] slashCommands) {
    this.slashCommands = slashCommands;
    this.buttonManager = new ButtonManager();
    INSTANCE = this;
  }

  public SlashCommand getCommandByKeyword(String keyword){
    for (SlashCommand slashCommand : slashCommands){
      if (slashCommand.name.equals(keyword)) {
        return slashCommand;
      }
    }
    return null;
  }

  public SlashCommand getCommandByButton(Button button){
    return buttonManager.request(button);
  }

  public static void main(String[] args) {
    upsertCommands();
  }

  public static void upsertCommands(){
    Main.manager.updateCommands().queue();
    for (SlashCommand slashCommand : getInstance().slashCommands){
      Main.manager.upsertCommand(slashCommand.name, slashCommand.description).queue();
    }
  }

}
