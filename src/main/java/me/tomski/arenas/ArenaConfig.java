package me.tomski.arenas;

import java.util.HashMap;
import java.util.Map;

import me.tomski.classes.HiderClass;
import me.tomski.classes.SeekerClass;
import me.tomski.objects.SimpleDisguise;

public class ArenaConfig {

    Map<Integer, SimpleDisguise> arenaDisguises = new HashMap<Integer, SimpleDisguise>();
    HiderClass arenaHiderClass;
    SeekerClass arenaSeekerClass;
    boolean usingDefault;

    public ArenaConfig(Map<Integer, SimpleDisguise> dis, HiderClass hC, SeekerClass sC, boolean def) {
        this.arenaDisguises = dis;
        this.arenaHiderClass = hC;
        this.arenaSeekerClass = sC;
        this.usingDefault = def;
    }

    public Map<Integer, SimpleDisguise> getArenaDisguises() {
        return arenaDisguises;
    }

    public void setArenaDisguises(Map<Integer, SimpleDisguise> arenaDisguises) {
        this.arenaDisguises = arenaDisguises;
    }

    public HiderClass getArenaHiderClass() {
        return arenaHiderClass;
    }

    public void setArenaHiderClass(HiderClass arenaHiderClass) {
        this.arenaHiderClass = arenaHiderClass;
    }

    public SeekerClass getArenaSeekerClass() {
        return arenaSeekerClass;
    }

    public void setArenaSeekerClass(SeekerClass arenaSeekerClass) {
        this.arenaSeekerClass = arenaSeekerClass;
    }

    public boolean isUsingDefault() {
        return usingDefault;
    }

    public void setUsingDefault(boolean usingDefault) {
        this.usingDefault = usingDefault;
    }


}
