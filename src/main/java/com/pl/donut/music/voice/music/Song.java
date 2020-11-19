package com.pl.donut.music.voice.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.pl.donut.music.voice.music.handler.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class Song extends Command {

    public Song() {
        super.name = "song";
        super.aliases = new String[]{"playing", "now", "songinfo"};
        super.category = new Category("Sound");
        super.arguments = "";
        super.help = "%song : shows the info of the current song";
    }

    @Override
    protected void execute(CommandEvent event) {
        Main.log(event, "Song");

        PlayerManager manager = PlayerManager.getInstance();
        if (manager.getGuildMusicManager(event.getGuild()).player.getPlayingTrack() == null)
            return;
            //TODO: send failed info
        AudioTrackInfo ati = manager.getGuildMusicManager(event.getGuild()).player.getPlayingTrack().getInfo();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.CYAN);
        eb.setTitle(ati.title);
        eb.setAuthor(ati.author);
        eb.setDescription("Length: " + ati.length + " ms\nURI: " + ati.uri);
        event.reply(eb.build());
    }
}
