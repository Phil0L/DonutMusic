package com.pl.donut.music.core.record.listener;


import com.pl.donut.music.core.record.Clip;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AudioReceiveListener implements AudioReceiveHandler {
  public static final double STARTING_MB = 0.5;
  public static final int CAP_MB = 16;
  public static final double PCM_MINS = 2;
  public boolean canReceive = true;
  private final double volume;
  private final VoiceChannel voiceChannel;

  public byte[] uncompVoiceData = new byte[(int) (3840 * 50 * 60 * PCM_MINS)]; //3840bytes/array * 50arrays/sec * 60sec = 1 mins
  public int uncompIndex = 0;
  public byte[] compVoiceData = new byte[(int) (1024 * 1024 * STARTING_MB)];    //start with 0.5 MB
  public int compIndex = 0;
  public boolean overwriting = false;

  public byte[] uncompUserVoiceData = new byte[(int) (3840 * 50 * 60 * PCM_MINS)]; //3840bytes/array * 50arrays/sec * 60sec = 1 mins
  public int uncompUserIndex = 0;
  public byte[] compUserVoiceData = new byte[(int) (1024 * 1024 * STARTING_MB)];    //start with 0.5 MB
  public int compUserIndex = 0;
  public boolean overwritingUser = false;

  private final List<OnVoiceData> listeners = new ArrayList<>();
  private final List<AudioReceiveHandler> secondaries = new ArrayList<>();


  public AudioReceiveListener(double volume, VoiceChannel voiceChannel) {
    this.volume = volume;
    this.voiceChannel = voiceChannel;
  }

  @Override
  public boolean canReceiveCombined() {
    return canReceive;
  }

  @Override
  public boolean canReceiveUser() {
    return canReceive;
  }

  @Override
  public void handleCombinedAudio(@NotNull CombinedAudio combinedAudio) {

    // secondaries
    for (AudioReceiveHandler secondary : secondaries){
      if (secondary.canReceiveCombined())
        secondary.handleCombinedAudio(combinedAudio);
    }

    if (uncompIndex == uncompVoiceData.length / 2 || uncompIndex == uncompVoiceData.length) {
      new Thread(() -> {

        if (uncompIndex < uncompVoiceData.length / 2)  //first half
          addCompVoiceData(Clip.encodePcmToMp3(Arrays.copyOfRange(uncompVoiceData, 0, uncompVoiceData.length / 2)));
        else
          addCompVoiceData(Clip.encodePcmToMp3(Arrays.copyOfRange(uncompVoiceData, uncompVoiceData.length / 2, uncompVoiceData.length)));

        for (OnVoiceData lis : listeners) {
          lis.onNewVoiceChannelData(compUserVoiceData, voiceChannel);
        }
      }).start();

      if (uncompIndex == uncompVoiceData.length)
        uncompIndex = 0;
    }

    for (byte b : combinedAudio.getAudioData(volume)) {
      uncompVoiceData[uncompIndex++] = b;
    }
  }

  @Override
  public void handleUserAudio(@NotNull UserAudio userAudio) {

    // secondaries
    for (AudioReceiveHandler secondary : secondaries){
      if (secondary.canReceiveUser())
        secondary.handleUserAudio(userAudio);
    }

    if (uncompUserIndex == uncompUserVoiceData.length / 2 || uncompUserIndex == uncompUserVoiceData.length) {
      new Thread(() -> {
        if (uncompUserIndex < uncompUserVoiceData.length / 2)  //first half
          addCompUserVoiceData(Clip.encodePcmToMp3(Arrays.copyOfRange(uncompUserVoiceData, 0, uncompUserVoiceData.length / 2)));
        else
          addCompUserVoiceData(Clip.encodePcmToMp3(Arrays.copyOfRange(uncompUserVoiceData, uncompUserVoiceData.length / 2, uncompUserVoiceData.length)));

        for (OnVoiceData lis : listeners) {
          lis.onNewVoiceUserData(getUserVoiceData(), userAudio.getUser());
        }
      }).start();

      if (uncompUserIndex == uncompUserVoiceData.length)
        uncompUserIndex = 0;
    }

    for (byte b : userAudio.getAudioData(volume)) {
      uncompUserVoiceData[uncompUserIndex++] = b;
    }
  }

  public byte[] getVoiceData() {
    canReceive = false;

    //flush remaining audio
    byte[] remaining = new byte[uncompIndex];

    int start = uncompIndex < uncompVoiceData.length / 2 ? 0 : uncompVoiceData.length / 2;

    if (uncompIndex - start >= 0) System.arraycopy(uncompVoiceData, start, remaining, 0, uncompIndex - start);

    addCompVoiceData(Clip.encodePcmToMp3(remaining));

    byte[] orderedVoiceData;
    if (overwriting) {
      orderedVoiceData = new byte[compVoiceData.length];
    } else {
      orderedVoiceData = new byte[compIndex + 1];
      compIndex = 0;
    }

    for (int i = 0; i < orderedVoiceData.length; i++) {
      if (compIndex + i < orderedVoiceData.length)
        orderedVoiceData[i] = compVoiceData[compIndex + i];
      else
        orderedVoiceData[i] = compVoiceData[compIndex + i - orderedVoiceData.length];
    }

    wipeMemory();
    canReceive = true;

    return orderedVoiceData;
  }

  public byte[] getUserVoiceData() {
    canReceive = false;

    //flush remaining audio
    byte[] remaining = new byte[uncompUserIndex];

    int start = uncompUserIndex < uncompUserVoiceData.length / 2 ? 0 : uncompUserVoiceData.length / 2;

    if (uncompUserIndex - start >= 0)
      System.arraycopy(uncompUserVoiceData, start, remaining, 0, uncompUserIndex - start);

    addCompVoiceData(Clip.encodePcmToMp3(remaining));

    byte[] orderedVoiceData;
    if (overwritingUser) {
      orderedVoiceData = new byte[compUserVoiceData.length];
    } else {
      orderedVoiceData = new byte[compUserIndex + 1];
      compUserIndex = 0;
    }

    for (int i = 0; i < orderedVoiceData.length; i++) {
      if (compUserIndex + i < orderedVoiceData.length)
        orderedVoiceData[i] = compUserVoiceData[compUserIndex + i];
      else
        orderedVoiceData[i] = compUserVoiceData[compUserIndex + i - orderedVoiceData.length];
    }

    wipeMemory();
    canReceive = true;

    return orderedVoiceData;
  }


  public void addCompVoiceData(byte[] compressed) {
    for (byte b : compressed) {
      if (compIndex >= compVoiceData.length && compVoiceData.length != 1024 * 1024 * CAP_MB) {    //cap at 16MB

        byte[] temp = new byte[compVoiceData.length * 2];
        System.arraycopy(compVoiceData, 0, temp, 0, compVoiceData.length);

        compVoiceData = temp;

      } else if (compIndex >= compVoiceData.length && compVoiceData.length == 1024 * 1024 * CAP_MB) {
        compIndex = 0;

        if (!overwriting) {
          overwriting = true;
          System.out.format("Hit compressed storage cap in %s on %s", voiceChannel.getName(), voiceChannel.getGuild().getName());
        }
      }

      compVoiceData[compIndex++] = b;
    }
  }

  public void addCompUserVoiceData(byte[] compressed) {
    for (byte b : compressed) {
      if (compUserIndex >= compUserVoiceData.length && compUserVoiceData.length != 1024 * 1024 * CAP_MB) {    //cap at 16MB

        byte[] temp = new byte[compUserVoiceData.length * 2];
        System.arraycopy(compUserVoiceData, 0, temp, 0, compUserVoiceData.length);

        compUserVoiceData = temp;

      } else if (compUserIndex >= compUserVoiceData.length && compUserVoiceData.length == 1024 * 1024 * CAP_MB) {
        compUserIndex = 0;

        if (!overwritingUser) {
          overwritingUser = true;
          System.out.format("Hit compressed storage cap in %s on %s", voiceChannel.getName(), voiceChannel.getGuild().getName());
        }
      }


      compUserVoiceData[compUserIndex++] = b;
    }
  }


  public void wipeMemory() {
    System.out.format("Wiped recording data in %s on %s", voiceChannel.getName(), voiceChannel.getGuild().getName());
    uncompIndex = 0;
    compIndex = 0;
    uncompUserIndex = 0;
    compUserIndex = 0;

    compUserVoiceData = new byte[1024 * 1024 / 2];
    compVoiceData = new byte[1024 * 1024 / 2];
    System.gc();
  }


  public byte[] getUncompVoice(int time) {
    canReceive = false;

    if (time > PCM_MINS * 60 * 2) {     //2 mins
      time = (int) (PCM_MINS * 60 * 2);
    }
    int requestSize = 3840 * 50 * time;
    byte[] voiceData = new byte[requestSize];

    for (int i = 0; i < voiceData.length; i++) {
      if (uncompIndex + i < voiceData.length)
        voiceData[i] = uncompVoiceData[uncompIndex + i];
      else
        voiceData[i] = uncompVoiceData[uncompIndex + i - voiceData.length];
    }

    wipeMemory();
    canReceive = true;
    return voiceData;
  }

  public void listen(OnVoiceData listener) {
    if (!listeners.contains(listener))
      listeners.add(listener);
  }

  public void registerSecondary(AudioReceiveHandler handler) {
    if (!secondaries.contains(handler))
      secondaries.add(handler);
  }

  public void stopListening(OnVoiceData listener) {
    listeners.remove(listener);
  }

  public void unregisterSecondary(AudioReceiveHandler handler) {
    secondaries.remove(handler);
  }

  public interface OnVoiceData {

    void onNewVoiceChannelData(byte[] compVoiceData, VoiceChannel channel);

    void onNewVoiceUserData(byte[] compUserVoiceData, User user);
  }

}