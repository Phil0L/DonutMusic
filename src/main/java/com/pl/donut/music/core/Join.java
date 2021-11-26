package com.pl.donut.music.core;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.time.OffsetDateTime;

public class Join extends Command {

    public Join() {
        super.name = "join";
        super.aliases = new String[]{"j"};
        super.category = new Category("Sound");
        super.arguments = "";
        super.help = "%join : connects the Bot to your Voicechannel";
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
            assert event.getMember().getVoiceState().getChannel() != null;
            VoiceChannel channel = event.getMember().getVoiceState().getChannel();
            audio.openAudioConnection(channel);
            sendJoinMessage(channel, event.getTextChannel());
        } catch (Exception e) {
            event.reply("You have to be in a Voicechannel");
        }
    }

    public void connect(GuildMessageReceivedEvent event){
        AudioManager audio = event.getGuild().getAudioManager();
        if (audio.isConnected())
            return;
        try {
            assert event.getMember() != null;
            assert event.getMember().getVoiceState() != null;
            assert event.getMember().getVoiceState().getChannel() != null;
            VoiceChannel channel = event.getMember().getVoiceState().getChannel();
            audio.openAudioConnection(channel);
            sendJoinMessage(channel, event.getChannel());
        } catch (Exception e) {
            event.getChannel().sendMessage("You have to be in a Voicechannel").queue();
        }
    }

    public void connect(Guild guild, Member member, TextChannel textChannel){
        AudioManager audio = guild.getAudioManager();
        if (audio.isConnected())
            return;
        try {
            assert member.getVoiceState() != null;
            assert member.getVoiceState().getChannel() != null;
            VoiceChannel channel = member.getVoiceState().getChannel();
            audio.openAudioConnection(channel);
            sendJoinMessage(channel, textChannel);
        } catch (Exception e) {
            textChannel.sendMessage("You have to be in a Voicechannel").queue();
        }
    }

    private void sendJoinMessage(VoiceChannel channel, TextChannel textChannel) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.ORANGE);
        embed.setTitle("Joined voice channel '" + channel.getName() + "'. Ready to play some music and eat tons of donuts!");
        embed.setTimestamp(OffsetDateTime.now());
        textChannel.sendMessageEmbeds(embed.build()).queue();
    }
}
