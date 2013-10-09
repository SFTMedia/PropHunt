package me.tomski.prophunt;


import me.tomski.arenas.ArenaConfig;
import me.tomski.objects.Loadout;
import me.tomski.objects.SimpleDisguise;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DisguiseManager implements Listener {

    private static PropHunt plugin;
    public static Map<Integer, SimpleDisguise> blockDisguises = new HashMap<Integer, SimpleDisguise>();
    public static Map<Player, SimpleDisguise> preChosenDisguise = new HashMap<Player, SimpleDisguise>();
    public static Map<Player, Loadout> loadouts = new HashMap<Player, Loadout>();

    boolean shouldDisable = false;

    public DisguiseManager(PropHunt plugin) {

    }

    public boolean isDisguised(Player p) {
        return false;
    }

    public void disguisePlayer(Player p, SimpleDisguise d) {
    }

    public void undisguisePlayer(Player p) {
    }

    public String getDisguiseName(Player p) {
        return "";
    }


    private String parseIdToName(int id) {
        return Material.getMaterial(id).name();
    }

    public void randomDisguise(Player p, ArenaConfig ac) {

    }

    public static String parseDisguiseToName(SimpleDisguise ds) {
        return ds.getName();
    }


    public static SimpleDisguise getRandomDisguiseObject(Map<Integer, SimpleDisguise> disguises) {
        int size = disguises.size();
        Random rnd = new Random();
        int random = rnd.nextInt(size);
        return disguises.get(random + 1);
    }

    public SimpleDisguise getSimpleDisguise(Player p) {
        return null;
    }

    public boolean shouldDisable() {
        return shouldDisable;
    }

    public void toggleBlockLock(PlayerToggleSneakEvent e) {

    }
}
