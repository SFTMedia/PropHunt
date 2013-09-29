package me.tomski.utils;

import me.tomski.bungee.Pinger;

import java.io.IOException;

public class PingTimer implements Runnable {

    private Pinger pinger;


    public PingTimer(Pinger ping) {
        this.pinger = ping;
    }

    @Override
    public void run() {
        try {
            pinger.sendServerData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
