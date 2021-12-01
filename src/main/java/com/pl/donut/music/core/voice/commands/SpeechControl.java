package com.pl.donut.music.core.voice.commands;

import com.google.cloud.texttospeech.v1beta1.SsmlVoiceGender;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.core.Join;
import com.pl.donut.music.core.record.listener.AudioReceiveListener;
import com.pl.donut.music.core.voice.vocalcord.CommandChain;
import com.pl.donut.music.core.voice.vocalcord.UserStream;
import com.pl.donut.music.core.voice.vocalcord.VocalCord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.io.File;
import java.time.OffsetDateTime;

public class SpeechControl extends Command implements VocalCord.Callbacks {

  public SpeechControl() {
    super.name = "speechcontrol";
    super.aliases = new String[]{"sc"};
    super.category = new Category("Voice");
    super.arguments = "";
    super.help = "";
  }

  private VocalCord cord;

  @Override
  protected void execute(CommandEvent event) {
    if (!event.getGuild().getAudioManager().isConnected())
      new Join().connect(event);

    assert event.getMember().getVoiceState() != null;
    assert event.getMember().getVoiceState().getChannel() != null;
    VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel();

    //send alert to correct users in the voice channel
    alert(voiceChannel, event);
    //initialize the audio receiver listener
    AudioManager audioManager = event.getGuild().getAudioManager();
    if (audioManager.getReceivingHandler() == null || !(audioManager.getReceivingHandler() instanceof AudioReceiveListener)) {
      audioManager.setReceivingHandler(new AudioReceiveListener(1, voiceChannel));
    }

    if (cord == null)
      cord = cordSetup();
    cord.connect(voiceChannel);
  }

  private VocalCord cordSetup() {
    /*
     * This code will create the bot, make sure to specify the absolute paths of the files you downloaded to native to ensure all libraries
     * are loaded correctly. This is also where you can config VocalCord settings.
     */

    // Windows
    return VocalCord.newConfig(this)
        .withWakeDetection(
            new File("./native/windows/libjni_porcupine.dll").getAbsolutePath(),
//                "C:\\Users\\wdavi\\IdeaProjects\\VocalCord\\native\\windows\\libjni_porcupine.dll",
            new File("./native/windows/libpv_porcupine.dll").getAbsolutePath(),
//                "C:\\Users\\wdavi\\IdeaProjects\\VocalCord\\native\\windows\\libpv_porcupine.dll",
            new File("./native/porcupine_params.pv").getAbsolutePath(),
//                "C:\\Users\\wdavi\\IdeaProjects\\VocalCord\\native\\porcupine_params.pv",
            0.5f,
            new File("./native/windows/donut_de_windows_v2_0_0.ppn").getAbsolutePath() // <- relocated!!
//                "C:\\Users\\wdavi\\IdeaProjects\\VocalCord\\phrases\\windows.ppn"
        )
        .withTTS(SsmlVoiceGender.MALE, true)
        .build();

    // Windows (with closed captioning instead of wake detection)
//        return VocalCord.newConfig(this)
//            .withClosedCaptioning()
//            .withTTS(SsmlVoiceGender.MALE,false)
//            .build();

    // Linux (using WSL)
//        return VocalCord.newConfig(this)
//            .withWakeDetection("/mnt/c/Users/wdavi/IdeaProjects/VocalCord/native/linux/libjni_porcupine.so",
//                "/mnt/c/Users/wdavi/IdeaProjects/VocalCord/native/linux/libpv_porcupine.so",
//                "/mnt/c/Users/wdavi/IdeaProjects/VocalCord/native/porcupine_params.pv",
//                0.5f,
//                "/mnt/c/Users/wdavi/IdeaProjects/VocalCord/phrases/linux.ppn"
//            )
//            .withTTS(SsmlVoiceGender.MALE, true)
//            .build();
  }

  private static void alert(VoiceChannel vc, CommandEvent event) {
    EmbedBuilder embed = new EmbedBuilder();
    embed.setColor(Color.ORANGE);
    embed.setTitle("Your audio is now being recorded in '" + vc.getName() + "'");
    embed.setTimestamp(OffsetDateTime.now());
    event.reply(embed.build());
  }

  @Override
  public boolean canWakeBot(User user) {
    return true;
  }

  @Override
  public void onWake(UserStream userStream, int keywordIndex) {
    cord.say("Yes?");
  }

  @Override
  public CommandChain onTranscribed() {
    // TODO: Example
    return new CommandChain.Builder()
        .addPhrase("hello world", (user, transcript, args) -> cord.say(user.getName() + " said something"))
        .addPhrase("knock knock", (user, transcript, args) -> cord.say("Who's there?"))
          .withFallback(((user, transcript, args) -> cord.say("I'm sorry, I didn't get that"))).withMinThreshold(0.5f).build();
  }
}
