package com.pl.donut.music.voice.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.pl.donut.music.voice.music.handler.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.awt.*;

public class Volume extends Command {

    public Volume(){
        super.name = "volume";
        super.aliases = new String[]{};
        super.category = new Category("Sound");
        super.arguments = "[percentage]";
        super.help = "%volume: shows the current volume \n" +
            "%volume [percentage] : sets the volume";
    }

    @Override
    protected void execute(CommandEvent event) {
        Main.log(event, "Volume");

        try {
            int i = Integer.parseInt(event.getArgs());
            PlayerManager manager = PlayerManager.getInstance();
            EmbedBuilder eb = new EmbedBuilder();
            if (i > 50){
                if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
                    manager.getGuildMusicManager(event.getGuild()).player.setVolume(i);
                    event.reply(eb.setColor(Color.GREEN).setTitle("Changed the volume to " + i).build());
                }else {
                    event.reply(eb.setColor(Color.RED).setTitle("You have to be an Administrator to change the volume to " + i).build());
                }
            }else{
                manager.getGuildMusicManager(event.getGuild()).player.setVolume(i);
                event.reply(eb.setColor(Color.GREEN).setTitle("Changed the volume to " + i).build());
            }


        } catch (Exception e){
            PlayerManager manager = PlayerManager.getInstance();
            int volume = manager.getGuildMusicManager(event.getGuild()).player.getVolume();
            event.reply(new EmbedBuilder().setColor(Color.BLUE).setTitle("The current Volume is **" + volume + "**.").build());
        }
    }
}
