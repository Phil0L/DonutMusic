package com.pl.donut.music.core.music.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.pl.donut.music.core.music.handler.PlayerManager;
import com.pl.donut.music.util.ReactionEmoji;

public class Skip extends Command {

  public Skip() {
    super.name = "skip";
    super.aliases = new String[]{"next"};
    super.category = new Category("Sound");
    super.arguments = "[amount]";
    super.help = """
        %skip : skips the current song
        %skip [x] : skips x songs\s
        %skip all : skips the whole queue""";
  }

  @Override
  protected void execute(CommandEvent event) {
    Main.log(event, "Skip");

    PlayerManager manager = PlayerManager.getInstance();
    if (event.getArgs().isEmpty()) {
      // skip 1
      boolean skipable = manager.getGuildMusicManager(event.getGuild()).scheduler.nextTrack();
      if (skipable)
        event.getMessage().addReaction(ReactionEmoji.CHECKMARK).queue();
      else
        event.reply("There is nothing left to play. How about some songs about donuts?");
    } else {
      try {
        if (event.getArgs().equals("all")) {
          // skip all
          //TODO: stop command
          manager.getGuildMusicManager(event.getGuild()).player.destroy();
        } else {
          // skip with count
          int i = Integer.parseInt(event.getArgs().strip());
          while (i > 0) {
            boolean skipable = manager.getGuildMusicManager(event.getGuild()).scheduler.nextTrack();
            if (!skipable) {
              event.reply("There is nothing left to play. How about some songs about donuts?");
              return;
            }
            i--;
          }
        }
        event.getMessage().addReaction(ReactionEmoji.CHECKMARK).queue();
      } catch (Exception e) {
        // skip 1
        boolean skipable = manager.getGuildMusicManager(event.getGuild()).scheduler.nextTrack();
        if (skipable)
          event.getMessage().addReaction(ReactionEmoji.CHECKMARK).queue();
        else
          event.reply("There is nothing left to play. How about some songs about donuts?");
      }
    }
  }
}
