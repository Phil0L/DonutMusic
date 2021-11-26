package com.pl.donut.music;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.pl.donut.music.core.Disconnect;
import com.pl.donut.music.core.JoinMe;
import com.pl.donut.music.core.music.commands.*;
import com.pl.donut.music.core.music.slash.SlashCommandClient;
import com.pl.donut.music.core.music.slash.SlashCommandClientBuilder;
import com.pl.donut.music.core.record.Clip;
import com.pl.donut.music.core.record.Record;
import com.pl.donut.music.core.music.slash.SlashCommandHandler;
import com.pl.donut.music.core.music.listener.MessageListener;
import com.pl.donut.music.core.music.listener.ReactionListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.Compression;
import org.discordbots.api.client.DiscordBotListAPI;

import javax.security.auth.login.LoginException;

public class Main {
  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_BLACK = "\u001B[30m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_YELLOW = "\u001B[33m";
  public static final String ANSI_BLUE = "\u001B[34m";
  public static final String ANSI_PURPLE = "\u001B[35m";
  public static final String ANSI_CYAN = "\u001B[36m";
  public static final String ANSI_WHITE = "\u001B[37m";
  public static JDA manager;
  public static DiscordBotListAPI dblapi;


  private Main() throws LoginException {
    setup();
  }

  public static void main(String[] args) throws LoginException {
    new Main();
  }

  public static void log(CommandEvent event, String command) {
    System.out.println("[" + event.getGuild().getName() + "]:[" + event.getMember().getEffectiveName() + "]: " + Main.ANSI_YELLOW + "Triggered " + command + Main.ANSI_RESET + ": " + event.getMessage().getContentRaw());
  }

  public static void info(CommandEvent event, String command) {
    System.out.println("[" + event.getGuild().getName() + "]:[" + event.getMember().getEffectiveName() + "]: " + Main.ANSI_YELLOW + command + Main.ANSI_RESET);
  }

  public static void info(Guild guild, String command) {
    System.out.println("[" + guild.getName() + "]: " + Main.ANSI_YELLOW + command + Main.ANSI_RESET);
  }

  public static void info(CommandEvent event, String command, String color) {
    System.out.println("[" + event.getGuild().getName() + "]:[" + event.getMember().getEffectiveName() + "]: " + color + command + Main.ANSI_RESET);
  }

  public static void info(Guild guild, String command, String color) {
    System.out.println("[" + guild.getName() + "]: " + color + command + Main.ANSI_RESET);
  }


  private void setup() throws LoginException {
    JDABuilder builder = JDABuilder.createDefault(Token.BOT_TOKEN);
    builder.setActivity(Activity.of(Activity.ActivityType.DEFAULT, "reloading"));
    builder.setBulkDeleteSplittingEnabled(false);
    builder.setCompression(Compression.NONE);

    CommandClientBuilder commandClientBuilder = new CommandClientBuilder();
    commandClientBuilder.setPrefix("%");
    commandClientBuilder.setOwnerId("653675331328409618");
    commandClientBuilder.setHelpWord("generichelp");
    commandClientBuilder.setActivity(Activity.listening("Donut ASMR"));
    commandClientBuilder.addCommand(new Disconnect());
    commandClientBuilder.addCommand(new Record());
    commandClientBuilder.addCommand(new Clip());
    commandClientBuilder.addCommand(new Play());
    commandClientBuilder.addCommand(new Volume());
    commandClientBuilder.addCommand(new Pause());
    commandClientBuilder.addCommand(new Skip());
    commandClientBuilder.addCommand(new Song());
    commandClientBuilder.addCommand(new Shuffle());
    commandClientBuilder.addCommand(new Queue());
    commandClientBuilder.addCommand(new JoinMe());
    CommandClient commandClient = commandClientBuilder.build();
    builder.addEventListeners(new EventWaiter(), commandClient);

    SlashCommandClientBuilder slashCommandClientBuilder = new SlashCommandClientBuilder();
    slashCommandClientBuilder.addCommand(new Controls());
    SlashCommandClient slashCommandClient = slashCommandClientBuilder.build();
    builder.addEventListeners(new SlashCommandHandler(), slashCommandClient);


    builder.addEventListeners(new ReactionListener());
    builder.addEventListeners(new MessageListener());
    Main.manager = builder.build();
    slashCommandClient.upsertCommands();



  }


}
