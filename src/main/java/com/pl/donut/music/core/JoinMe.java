package com.pl.donut.music.core;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.awt.*;

public class JoinMe extends Command {

    public JoinMe(){
        super.name = "joinme";
        super.aliases = new String[]{"join-me", "jm"};
        super.category = new Category("Sound");
        super.arguments = "@[user]";
        super.help = "%joinme @[user] : asks the user to join your Voicechannel";
        super.cooldown = 10;
    }

    @Override
    protected void execute(CommandEvent event) {
        Main.log(event, "Join me");

        assert event.getMember().getVoiceState() != null;
        if (event.getMember().getVoiceState().getChannel() != null) {
            event.getTextChannel().deleteMessageById(event.getTextChannel().getLatestMessageId()).queue();
            Member member = event.getMessage().getMentionedMembers().get(0);
            VoiceChannel channel = event.getMember().getVoiceState().getChannel();
            member.getUser().openPrivateChannel().complete().sendMessage(event.getMember().getEffectiveName() + " invites you to join his/her current Voicechannel " + channel.createInvite().complete().getUrl()).queue();
        }else
            event.reply(new EmbedBuilder().setTitle("You hav to be in a Voicechannel to use this command").setColor(Color.RED).build());
    }
}
