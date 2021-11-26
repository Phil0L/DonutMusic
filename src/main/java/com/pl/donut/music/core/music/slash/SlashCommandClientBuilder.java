package com.pl.donut.music.core.music.slash;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandClientBuilder {

  public List<SlashCommand> commands;

  public SlashCommandClientBuilder() {
    commands = new ArrayList<>();
  }

  public void addCommand(SlashCommand command){
    commands.add(command);
  }

  public SlashCommandClient build(){
    return new SlashCommandClient(commands.toArray(SlashCommand[]::new));
  }

}
