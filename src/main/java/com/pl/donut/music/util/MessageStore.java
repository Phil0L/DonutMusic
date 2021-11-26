package com.pl.donut.music.util;

import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageStore {
  private static final List<SavedMessage> savedMessages = new ArrayList<>();

  public static void saveMessage(SavedMessage message){
    savedMessages.add(message);
  }

  public static SavedMessage getMessage(Message message){
    for (SavedMessage savedMessage : savedMessages){
      if (savedMessage.equals(message)) return savedMessage;
    }
    return null;
  }

  public static List<SavedMessage> allMessages() {
    return savedMessages;
  }
}
