package com.pl.donut.music.core.music.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.pl.donut.music.core.music.handler.GuildAudioManager;
import com.pl.donut.music.core.music.handler.PlayerManager;
import com.pl.donut.music.util.ReactionEmoji;

public class Stop extends Command {

  public Stop() {
    super.name = "stop";
    super.aliases = new String[]{};
    super.category = new Category("Sound");
    super.arguments = "";
    super.help = "%stop : stops the bot\n";
  }

  @Override
  protected void execute(CommandEvent event) {
    Main.log(event, "Stop");

    PlayerManager manager = PlayerManager.getInstance();
    GuildAudioManager guildAudioManager = manager.getGuildAudioManager(event.getGuild());
    guildAudioManager.scheduler.clearQueue();
    guildAudioManager.player.destroy();

    event.getMessage().addReaction(ReactionEmoji.CHECKMARK).queue();
  }
}
