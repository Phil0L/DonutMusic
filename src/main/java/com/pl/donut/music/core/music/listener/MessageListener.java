package com.pl.donut.music.core.music.listener;

import com.pl.donut.music.core.music.commands.Play;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageListener extends ListenerAdapter {

  @Override
  public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
    Message message = event.getMessage();
    String content = message.getContentRaw();
    if (content.startsWith("p ")){
      message.delete().queue();
      new Play().handleQuickPlayCommand(event, content);
    }
  }

}
