package com.pl.donut.music.voice.music.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.pl.donut.music.voice.music.handler.PlayerManager;


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
        manager.getGuildMusicManager(event.getGuild()).scheduler.shuffle();
    }
}
