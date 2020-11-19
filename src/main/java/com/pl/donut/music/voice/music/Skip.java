package com.pl.donut.music.voice.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.pl.donut.music.voice.music.handler.PlayerManager;

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
      manager.getGuildMusicManager(event.getGuild()).scheduler.nextTrack();
    } else {
      try {
        if (event.getArgs().equals("all")) {
          manager.getGuildMusicManager(event.getGuild()).player.destroy();
        } else {
          int i = Integer.parseInt(event.getArgs().strip());
          while (i > 0) {
            manager.getGuildMusicManager(event.getGuild()).scheduler.nextTrack();
            i--;
          }
        }
        event.getMessage().addReaction("U+2705").queue();
      } catch (Exception e) {
        manager.getGuildMusicManager(event.getGuild()).scheduler.nextTrack();
        event.getMessage().addReaction("U+2705").queue();
      }
    }
  }
}
