package com.pl.donut.music.core.music.commands;

import com.jagrosh.jdautilities.command.Command.Category;
import com.pl.donut.music.core.music.handler.PlayerManager;
import com.pl.donut.music.core.music.handler.TrackScheduler;
import com.pl.donut.music.core.music.slash.SlashCommand;
import com.pl.donut.music.util.ReactionEmoji;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public class Controls extends SlashCommand {

  public static final String PlayPause = "play_pause";
  public static final String SkipForward = "skip";
  public static final String SkipBackwards = "skip_reverse";
  public static final String Stop = "stop";

  public Controls() {
    super.name = "controls";
    super.category = new Category("Sound");
    super.arguments = "";
    super.help = "/controls : Shows some clickable buttons to control the best of all music bots. It also has a lot of donuts!";
    super.description = "Shows some clickable buttons to control the best of all music bots. It also has a lot of donuts!";
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    assert event.getGuild() != null;
    PlayerManager manager = PlayerManager.getInstance();
    AudioPlayer player = manager.getGuildAudioManager(event.getGuild()).player;
    TrackScheduler scheduler =manager.getGuildAudioManager(event.getGuild()).scheduler;
    boolean isPlaying = !player.isPaused() && player.getPlayingTrack() != null;
    boolean hasTrack = player.getPlayingTrack() != null;
    boolean hasNextTrack = !scheduler.getQueue().isEmpty();

    Button bPlayPause = Button.success(PlayPause, Emoji.fromUnicode(isPlaying ? ReactionEmoji.PLAY : ReactionEmoji.PAUSE));
    if (!hasTrack) bPlayPause = Button.danger(PlayPause, Emoji.fromUnicode(ReactionEmoji.PAUSE));

    Button bSkip = Button.primary(SkipForward, Emoji.fromUnicode(ReactionEmoji.NEXT));
    if (hasNextTrack) bSkip = Button.danger(SkipForward, Emoji.fromUnicode(ReactionEmoji.NEXT));

    Button bPrevious = Button.danger(SkipBackwards, Emoji.fromUnicode(ReactionEmoji.PREVIOUS));

    Button bStop = Button.primary(Stop, Emoji.fromUnicode(ReactionEmoji.STOP));

    registerButton(bPrevious);
    registerButton(bPlayPause);
    registerButton(bSkip);
    registerButton(bStop);

    event.reply("Test").addActionRow(bPrevious, bPlayPause, bSkip, bStop).queue();
  }

  @Override
  protected void clicked(ButtonClickEvent event) {
    assert event.getButton() != null;
    assert event.getButton().getId() != null;
    switch (event.getButton().getId()){
      case PlayPause:
        break;
      case SkipForward:
        break;
      case SkipBackwards:
        break;
      case Stop:
        break;
      default:
        break;
    }
  }

}
