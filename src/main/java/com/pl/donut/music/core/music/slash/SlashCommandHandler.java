package com.pl.donut.music.core.music.slash;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommandHandler extends ListenerAdapter {

  @Override
  public void onButtonClick(@NotNull ButtonClickEvent event) {
    SlashCommandClient slashCommandClient = SlashCommandClient.getInstance();
    SlashCommand command = slashCommandClient.getCommandByButton(event.getButton());
    if (command == null) return;

    command.clicked(event);
  }

  @Override
  public void onSlashCommand(@NotNull SlashCommandEvent event) {
    SlashCommandClient slashCommandClient = SlashCommandClient.getInstance();
    SlashCommand command = slashCommandClient.getCommandByKeyword(event.getName());
    if (command == null) return;

    command.execute(event);
  }
}
