package pl.aogiri.tsbot;


import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;

import java.util.List;

public class Main {
    private static Bot bot;

    public static void main(String[] args) {
	System.out.println("Hi bot");
    bot = new Bot();
    bot.build();
    bot.setCheckChannels();

//    bot.disconnect();
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
