package com.pl.donut.music.core.music.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.pl.donut.music.core.music.handler.GuildMusicManager;
import com.pl.donut.music.core.music.handler.PlayerManager;
import com.pl.donut.music.util.ReactionEmoji;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

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
            Guild guild = event.getGuild();
            GuildMusicManager guildMusicManager = manager.getGuildMusicManager(guild);
            if (!manager.getGuildMusicManager(event.getGuild()).scheduler.getQueue().isEmpty()) {
                String queue = getQueue(guildMusicManager);
                event.reply(new EmbedBuilder()
                    .setTitle(guildMusicManager.scheduler.getQueue().size() + " Tracks queued:")
                    .setImage("https://raw.githubusercontent.com/Phil0L/DonutMusic/master/imgs/Donut-Bot-Queue.png")
                    .setColor(Color.CYAN)
                    .setDescription(queue).build());
            } else {
                event.reply("No queue");
            }
        }else if (event.getArgs().equals("delete")){
            manager.getGuildMusicManager(event.getGuild()).scheduler.clearQueue();
        }else if (event.getArgs().equals("shuffle")){
            new Shuffle().execute(event);
        }
    }

    @NotNull
    private String getQueue(GuildMusicManager guildMusicManager) {
        StringBuilder desc = new StringBuilder();
        AudioTrack[] tracks = guildMusicManager.scheduler.getQueue().toArray(AudioTrack[]::new);
        for (int i = 0; i < 9; i++) {
            if (i >= tracks.length) break;
            AudioTrack track = tracks[i];
            desc.append(ReactionEmoji.getNumberAsEmoji(i + 1)).append(" ").append(track.getInfo().title).append("\n");
        }
        if (tracks.length == 10){
            AudioTrack track = tracks[9];
            desc.append(ReactionEmoji.getNumberAsEmoji(10)).append(" ").append(track.getInfo().title).append("\n");
        }
        if (tracks.length >= 11){
            int i = tracks.length - 1;
            AudioTrack track = tracks[i];
            desc.append("... ").append(tracks.length - 11).append(" more").append(" ...").append("\n");
            desc.append(ReactionEmoji.getNumberAsEmoji(i + 1)).append(" ").append(track.getInfo().title).append("\n");
        }
        return desc.toString();
    }
}
