package com.pl.donut.music.voice;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.pl.donut.music.voice.music.handler.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.time.OffsetDateTime;

public class Disconnect extends Command {

    public Disconnect(){
        super.name = "disconnect";
        super.aliases = new String[]{"dc","fuckoff","leave"};
        super.category = new Category("Sound");
        super.arguments = "";
        super.help = "disconnects the Bot from his voicechannel";
    }

    @Override
    protected void execute(CommandEvent event) {
        Main.log(event, "Disconnect");

        stopRec(event);
        PlayerManager manager = PlayerManager.getInstance();
        manager.getGuildMusicManager(event.getGuild()).player.destroy();
        event.getGuild().getAudioManager().closeAudioConnection();
        event.reply(new EmbedBuilder().setColor(Color.ORANGE).setTitle("Disconnected").build());
    }

    public void stopRec(CommandEvent event){
        if (event.getGuild().getAudioManager().getReceivingHandler() != null) {
            event.getGuild().getAudioManager().setReceivingHandler(null);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setTitle("Stopped recording your audio");
            embed.setTimestamp(OffsetDateTime.now());

            event.reply(embed.build());
        }
    }
}
