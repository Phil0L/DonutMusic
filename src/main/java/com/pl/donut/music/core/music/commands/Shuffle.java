package com.pl.donut.music.core.music.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.pl.donut.music.core.music.handler.PlayerManager;
import com.pl.donut.music.util.ReactionEmoji;


public class Shuffle extends Command {

    public Shuffle(){
        super.name = "shuffle";
        super.aliases = new String[]{};
        super.category = new Category("Sound");
        super.arguments = "";
        super.help = "%shuffle : shuffles the queue";
    }

    @Override
    protected void execute(CommandEvent event) {
        Main.log(event, "Shuffle");

        PlayerManager manager = PlayerManager.getInstance();
        manager.getGuildAudioManager(event.getGuild()).scheduler.shuffle();

        event.getMessage().addReaction(ReactionEmoji.CHECKMARK).queue();
    }
}
