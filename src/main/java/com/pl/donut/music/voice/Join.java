package com.pl.donut.music.voice;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.time.OffsetDateTime;

public class Join extends Command {

    public Join() {
        super.name = "join";
        super.aliases = new String[]{"j"};
        super.category = new Category("Sound");
        super.arguments = "";
        super.help = "%join : connects the Bot to your voicecannel";
    }

    @Override
    protected void execute(CommandEvent event) {
        Main.log(event, "Join");
        connect(event);
    }

    public void connect(CommandEvent event){
        AudioManager audio = event.getGuild().getAudioManager();
        if (audio.isConnected())
            return;
        try {
            assert event.getMember().getVoiceState() != null;
            audio.openAudioConnection(event.getMember().getVoiceState().getChannel());
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.GREEN);
            embed.setTitle("Joined voice channel '" + event.getMember().getVoiceState().getChannel().getName() + "'. Ready to play some music!");
            embed.setTimestamp(OffsetDateTime.now());
            event.reply(embed.build());
        } catch (Exception e) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.RED);
            eb.setTitle("You have to be in a Voicechannel");
            event.getTextChannel().sendMessage(eb.build()).queue();
        }
    }

    public void connect(Guild guild, Member member, TextChannel textChannel){
        AudioManager audio = guild.getAudioManager();
        if (audio.isConnected())
            return;
        try {
            assert member.getVoiceState() != null;
            audio.openAudioConnection(member.getVoiceState().getChannel());
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.GREEN);
            embed.setTitle("Joined voice channel '" + member.getVoiceState().getChannel().getName() + "'. Ready to play some music!");
            embed.setTimestamp(OffsetDateTime.now());
            textChannel.sendMessage(embed.build()).queue();
        } catch (Exception e) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.RED);
            eb.setTitle("You have to be in a Voicechannel");
            textChannel.sendMessage(eb.build()).queue();
        }
    }


}
