package com.pl.donut.music.util;

public class ReactionEmoji {
  public static final String PREVIOUS = "U+2B05";
  public static final String PLAY = "U+1F3B5";
  public static final String NEXT = "U+27A1";
  public static final String QUEUE = "U+2795";
  public static final String CHECKMARK = "U+2705";

  public static final String ONE = "U+0031";
  public static final String TWO = "U+0032";
  public static final String THREE = "U+0033";
  public static final String FOUR = "U+0034";
  public static final String FIVE = "U+0035";
  public static final String SIX = "U+0036";
  public static final String SEVEN = "U+0037";
  public static final String EIGHT = "U+0038";
  public static final String NINE = "U+0039";
  public static final String TEN = "U+1F51F";
  public static final String STOP = "U+23F9";

  public static String getNumberAsEmoji(int number){
    return switch (number) {
      case 1 -> ":one:";
      case 2 -> ":two:";
      case 3 -> ":three:";
      case 4 -> ":four:";
      case 5 -> ":five:";
      case 6 -> ":six:";
      case 7 -> ":seven:";
      case 8 -> ":eight:";
      case 9 -> ":nine:";
      case 10 -> ":keycap_ten:";
      default -> ":stop_button:";
    };
  }

}
