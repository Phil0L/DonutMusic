package com.pl.donut.music.core.music.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.pl.donut.music.core.music.handler.PlayerManager;
import com.pl.donut.music.util.ReactionEmoji;
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
            if (i > 50){
                if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
                    manager.getGuildAudioManager(event.getGuild()).player.setVolume(i);
                    event.getMessage().addReaction(ReactionEmoji.CHECKMARK).queue();
                }else {
                    event.reply("You have to be an Administrator to change the volume to " + i);
                }
            }else{
                manager.getGuildAudioManager(event.getGuild()).player.setVolume(i);
                event.getMessage().addReaction(ReactionEmoji.CHECKMARK).queue();
            }
        } catch (Exception e){
            // volume request
            PlayerManager manager = PlayerManager.getInstance();
            int volume = manager.getGuildAudioManager(event.getGuild()).player.getVolume();
            event.reply(new EmbedBuilder().setColor(Color.BLUE).setTitle("The current Volume is **" + volume + "**.").build());
        }
    }
}
