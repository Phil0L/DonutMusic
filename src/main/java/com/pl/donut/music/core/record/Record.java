package com.pl.donut.music.core.record;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.pl.donut.music.core.Disconnect;
import com.pl.donut.music.core.Join;
import com.pl.donut.music.core.record.listener.AudioReceiveListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Objects;

public class Record extends Command {

  public Record() {
    super.name = "record";
    super.aliases = new String[]{"rec", "r"};
    super.category = new Category("Sound");
    super.arguments = "<stop>";
    super.help = "%record : starts recording the channel the bot is in";
  }

  @Override
  protected void execute(CommandEvent event) {
    Main.log(event, "Record");
    if (event.getArgs().contains(" stop ")) {
      new Disconnect().stopRec(event);
      return;
    }

    if (!event.getGuild().getAudioManager().isConnected())
      new Join().connect(event);
    assert event.getMember().getVoiceState() != null;
    recordVoiceChannel(Objects.requireNonNull(event.getMember().getVoiceState().getChannel()), true, event);

  }

  public static void recordVoiceChannel(VoiceChannel vc, boolean warning, CommandEvent event) {
    //send alert to correct users in the voice channel
    if (warning)
      alert(vc, event);
    //initialize the audio receiver listener
    AudioManager audioManager = vc.getGuild().getAudioManager();
    if (audioManager.getReceivingHandler() == null || !(audioManager.getReceivingHandler() instanceof AudioReceiveListener))
      audioManager.setReceivingHandler(new AudioReceiveListener(1, vc));

  }

  public static void alert(VoiceChannel vc, CommandEvent event) {
    EmbedBuilder embed = new EmbedBuilder();
    embed.setColor(Color.ORANGE);
    embed.setTitle("Your audio is now being recorded in '" + vc.getName() + "'");
    embed.setTimestamp(OffsetDateTime.now());
    event.reply(embed.build());
  }


}
