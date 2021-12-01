package com.pl.donut.music.core.record;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.pl.donut.music.Main;
import com.pl.donut.music.core.record.listener.AudioReceiveListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.sourceforge.lame.lowlevel.LameEncoder;
import net.sourceforge.lame.mp3.Lame;
import net.sourceforge.lame.mp3.MPEGMode;

import javax.sound.sampled.AudioFormat;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import static java.lang.Thread.sleep;

public class Clip extends Command {

    static String[] args = {};

    public Clip() {
        super.name = "clip";
        super.aliases = new String[]{"save"};
        super.category = new Category("Sound");
        super.arguments = "[time] [name]";
        super.help = "%clip [x] : creates a clip of the x last seconds\n" +
                "%clip [x] [name] : you can add an additional name";
    }

    @Override
    protected void execute(CommandEvent event) {
        Main.log(event, "Clip");

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);

        if (event.getGuild().getAudioManager().getConnectedChannel() == null) {
            eb.setTitle("I wasnt recording. type %record to start recording");
            event.reply(eb.build());
            return;
        }

        if (event.getArgs() == null) {
            eb.setTitle("You have to provide a time");
            event.reply(eb.build());
        } else {
            args = event.getArgs().split(" ");
        }

        int time;
        try {
            time = Integer.parseInt(args[0]);
        } catch (Exception ex) {
            eb.setTitle("Cannot read entered time");
            event.reply(eb.build());
            return;
        }

        if (time <= 0) {
            eb.setTitle("Time must be greater than 0");
            event.reply(eb.build());
            return;
        }

        writeToFile(event.getGuild(), time, event.getTextChannel());

    }

    public static void writeToFile(Guild guild, int duration, TextChannel tc) {

        AudioReceiveListener ah = (AudioReceiveListener) guild.getAudioManager().getReceivingHandler();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        if (ah == null) {
            eb.setTitle("I wasnt recording");
            tc.sendMessage(eb.build()).queue();
            return;
        }

        File dest;
        File folder;
        try {
            dest = new File("./recordings/" + getPJSaltString() + ".mp3");
            folder = new File("./recordings");
            if (!folder.exists())
                folder.mkdir();
            byte[] voiceData;

            if (duration > 0 && duration <= AudioReceiveListener.PCM_MINS * 60 * 2) {
                voiceData = ah.getUncompVoice(duration);
                voiceData = encodePcmToMp3(voiceData);

            } else {
                voiceData = ah.getVoiceData();
            }

            FileOutputStream fos = new FileOutputStream(dest);
            fos.write(voiceData);
            fos.close();

            if (dest.length() / 1024 / 1024 < 8) {
                final TextChannel channel = tc;
                tc.sendFile(dest).queue(null, (Throwable) -> tc.sendMessage("I don't have permissions to send files in " + channel.getName() + "!").queue());

                new Thread(() -> {
                    try {
                        sleep(1000 * 20);
                    } catch (Exception ignored) {
                    }    //20 second life for files set to discord (no need to save)

                    dest.delete();
                    System.out.println("\tDeleting file " + dest.getName() + "...");

                }).start();

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            eb.setTitle("Unknown error sending file");
            tc.sendMessageEmbeds(eb.build()).queue();
        }
    }

    public static String getPJSaltString() {

        try {
            return args[1];
        } catch (IndexOutOfBoundsException ignored) {
        }

        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 13) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();

        //check for a collision on the 1/2e62 chance that it matches another salt string (lulW)
        File dir = new File("/var/www/html/");
        if (!dir.exists())
            dir = new File("recordings/");

        if (dir.listFiles() != null) {
            for (File f : dir.listFiles()) {
                if (f.getName().equals(saltStr))
                    saltStr = getPJSaltString();
            }
        }

        return saltStr;
    }

    //TODO: move
    public static byte[] encodePcmToMp3(byte[] pcm) {
        LameEncoder encoder = new LameEncoder(new AudioFormat(48000.0f, 16, 2, true, true), 128, MPEGMode.STEREO, Lame.QUALITY_HIGHEST, false);
        ByteArrayOutputStream mp3 = new ByteArrayOutputStream();
        byte[] buffer = new byte[encoder.getPCMBufferSize()];

        int bytesToTransfer = Math.min(buffer.length, pcm.length);
        int bytesWritten;
        int currentPcmPosition = 0;
        while (0 < (bytesWritten = encoder.encodeBuffer(pcm, currentPcmPosition, bytesToTransfer, buffer))) {
            currentPcmPosition += bytesToTransfer;
            bytesToTransfer = Math.min(buffer.length, pcm.length - currentPcmPosition);

            mp3.write(buffer, 0, bytesWritten);
        }

        encoder.close();

        return mp3.toByteArray();
    }

    //TODO: implement somewhere else and better
    public static void killAudioHandlers(Guild g) {
        AudioReceiveListener ah = (AudioReceiveListener) g.getAudioManager().getReceivingHandler();
        if (ah != null) {
            ah.canReceive = false;
            ah.compVoiceData = null;
            g.getAudioManager().setReceivingHandler(null);
        }

//        if (g.getAudioManager().getSendingHandler() instanceof AudioSendListener) {
//            AudioSendListener sh = (AudioSendListener) g.getAudioManager().getSendingHandler();
//            if (sh != null) {
//                sh.canProvide = false;
//                sh.voiceData = null;
//                g.getAudioManager().setSendingHandler(null);
//            }
//        }

        System.out.println("Destroyed audio handlers for " + g.getName());
        System.gc();
    }
}
