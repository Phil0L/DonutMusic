package com.pl.donut.music;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.discordbots.api.client.DiscordBotListAPI;

import javax.security.auth.login.LoginException;

public class Main {
  public static ShardManager manager;
  public static DiscordBotListAPI dblapi;

  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_BLACK = "\u001B[30m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_YELLOW = "\u001B[33m";
  public static final String ANSI_BLUE = "\u001B[34m";
  public static final String ANSI_PURPLE = "\u001B[35m";
  public static final String ANSI_CYAN = "\u001B[36m";
  public static final String ANSI_WHITE = "\u001B[37m";


  public static void main(String[] args) throws LoginException {
    new Main();
  }

  private Main() throws LoginException {
    setup();
  }

  private void setup() throws LoginException {
    DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
    builder.setToken(Token.BOT_TOKEN);
    builder.setActivity(Activity.of(Activity.ActivityType.DEFAULT, "reloading"));
    CommandClientBuilder cmd = new CommandClientBuilder();
    cmd.setPrefix("%");
    cmd.setOwnerId("653675331328409618");
    cmd.setHelpWord("generichelp");
    cmd.setActivity(Activity.of(Activity.ActivityType.DEFAULT, "%status | in dev rn"));

    dblapi = new DiscordBotListAPI.Builder()
        .token(Token.DBL_TOKEN)
        .botId("653675331328409618")
        .build();

//        cmd.addCommand(new Join());
//        cmd.addCommand(new Disconnect());
//        cmd.addCommand(new Record());
//        cmd.addCommand(new Clip());
//        cmd.addCommand(new Soundboard());
//        cmd.addCommand(new Play());
//        cmd.addCommand(new Volume());
//        cmd.addCommand(new Pause());
//        cmd.addCommand(new Skip());
//        cmd.addCommand(new Song());
//        cmd.addCommand(new Shuffle());
//        cmd.addCommand(new Queue());
//        cmd.addCommand(new JoinMe());

    CommandClient client = cmd.build();
    builder.addEventListeners(client);
//        builder.addEventListeners(new Listener());
    Main.manager = builder.build();


  }

  public static void log(CommandEvent event, String command) {
    System.out.println("[" + event.getGuild().getName() + "]:[" + event.getMember().getEffectiveName() + "]: " + Main.ANSI_YELLOW + "Triggered " + command + Main.ANSI_RESET + ": " + event.getMessage().getContentRaw());
  }


}
