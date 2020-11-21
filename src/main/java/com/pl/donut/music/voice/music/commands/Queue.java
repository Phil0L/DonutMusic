package com.pl.donut.music.voice.music.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.pl.donut.music.voice.music.handler.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;

public class Queue extends Command {

    public Queue(){
        super.name = "queue";
        super.aliases = new String[]{"q", "que"};
        super.category = new Category("Sound");
        super.arguments = "[action]";
        super.help = """
            %queue : shows the current queue
            %queue delete : skips the hole queue
            %queue shuffle : shuffles the queue""";
    }

    @Override
    protected void execute(CommandEvent event) {
        Main.log(event, "Queue");

        PlayerManager manager = PlayerManager.getInstance();
        if (event.getArgs().isEmpty()) {

            if (!manager.getGuildMusicManager(event.getGuild()).scheduler.getList().isEmpty()) {
                StringBuilder desc = new StringBuilder();
                int i = 0;
                for (AudioTrack track : manager.getGuildMusicManager(event.getGuild()).scheduler.getList()) {
                    i++;
                    if (desc.length() > 1900) {
                        desc.append("...\n");
                        desc.append("#").append(manager.getGuildMusicManager(event.getGuild()).scheduler.queue.size()).append(": ").append(manager.getGuildMusicManager(event.getGuild()).scheduler.queue.get(manager.getGuildMusicManager(event.getGuild()).scheduler.queue.size() - 1).getInfo().title);
                        break;
                    }
                    desc.append("#").append(i).append(": ").append(track.getInfo().title).append("\n");
                }

                event.reply(new EmbedBuilder().setTitle("Current Queue: ").setColor(Color.CYAN).setDescription(desc.toString()).build());
            } else {
                event.reply(new EmbedBuilder().setTitle("No queue").setColor(Color.RED).build());
            }
        }else if (event.getArgs().equals("delete")){
            manager.getGuildMusicManager(event.getGuild()).scheduler.queue = new ArrayList<>();
        }else if (event.getArgs().equals("shuffle")){
            manager.getGuildMusicManager(event.getGuild()).scheduler.shuffle();
        }
    }
}
