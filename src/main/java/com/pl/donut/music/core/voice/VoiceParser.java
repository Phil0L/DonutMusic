package com.pl.donut.music.core.voice;

import com.pl.donut.music.core.record.listener.AudioReceiveListener;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class VoiceParser implements AudioReceiveListener.OnVoiceData {

  public static final int MAX_CACHE = 2000;

  public VoiceChannel voiceChannel;
  public AudioReceiveListener audioReceiveListener;
  private final List<OnUserSpeech> listeners = new ArrayList<>();

  private byte[] cachedVoiceData;
  private int receivedSinceReset;

  public VoiceParser(VoiceChannel voiceChannel) {
    this.voiceChannel = voiceChannel;
    this.audioReceiveListener = (AudioReceiveListener) voiceChannel.getGuild().getAudioManager().getReceivingHandler();
    assert audioReceiveListener != null;
    audioReceiveListener.listen(this);
  }


  @Override
  public void onNewVoiceChannelData(byte[] compVoiceData, VoiceChannel channel) {

  }

  @Override
  public void onNewVoiceUserData(byte[] compUserVoiceData, User user) {
    try {
      byte[] completeUserData = concatenate(cachedVoiceData, compUserVoiceData);

      Configuration configuration = new Configuration();
      configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
      configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
      configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

      StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
      ByteArrayInputStream inputStream = new ByteArrayInputStream(completeUserData);

      System.out.println("Parsing user voice data:");
      recognizer.startRecognition(inputStream);
      SpeechResult result;
      while ((result = recognizer.getResult()) != null) {
        String text = result.getHypothesis();
        System.out.format("Hypothesis: %s\n", text);
        for (OnUserSpeech listener : listeners){
          listener.onNewUserSpeechData(text, user, receivedSinceReset);
        }
      }
      recognizer.stopRecognition();

      receivedSinceReset++;

    } catch (IOException io) {
      io.printStackTrace();
    }
  }

  public void reset(){
    cachedVoiceData = new byte[0];
    receivedSinceReset = 0;
  }

  private byte[] concatenate(byte[] a, byte[] b) {
    if (a == null) return b;
    if (b == null) return a;
    int aLen = a.length;
    int bLen = b.length;

    byte[] c = (byte[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
    System.arraycopy(a, 0, c, 0, aLen);
    System.arraycopy(b, 0, c, aLen, bLen);

    return c;
  }

  public void onUpdatedUserSpeechData(OnUserSpeech listener){
    listeners.add(listener);
  }

  public interface OnUserSpeech{
    void onNewUserSpeechData(String speechToText, User user, int iteration);
  }
}
