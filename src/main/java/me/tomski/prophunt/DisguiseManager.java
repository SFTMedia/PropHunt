package me.tomski.prophunt;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.tomski.objects.Loadout;
import me.tomski.objects.SimpleDisguise;
import me.tomski.utils.PropHuntMessaging;
import me.tomski.arenas.ArenaConfig;
import me.tomski.language.MessageBank;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class DisguiseManager {

    private static PropHunt plugin;
    public static Map<Integer, SimpleDisguise> blockDisguises = new HashMap<Integer, SimpleDisguise>();
    public static Map<Player, SimpleDisguise> preChosenDisguise = new HashMap<Player, SimpleDisguise>();
    public static Map<Player, Loadout> loadouts = new HashMap<Player, Loadout>();

    public DisguiseCraftAPI dcAPI;

    public DisguisePluginType disguisePluginType;

    public DisguiseManager(PropHunt plugin) {
        if (plugin.getServer().getPluginManager().isPluginEnabled("LibsDisguises")) {
            disguisePluginType = DisguisePluginType.LIBSDISGUISES;
            DisguiseAPI.setViewDisguises(true);
        } else if (plugin.getServer().getPluginManager().isPluginEnabled("DisguiseCraft")) {
            disguisePluginType = DisguisePluginType.DISGUISECRAFT;
            dcAPI = DisguiseCraft.getAPI();
        } else {
            plugin.getLogger().warning("Plugin disabling, DisguiseCraft or LibsDisguises not found");
            plugin.getPluginLoader().disablePlugin(plugin);
        }

        DisguiseManager.plugin = plugin;
        int i = DisguiseManager.plugin.loadBlockDisguises();
        DisguiseManager.plugin.getLogger().log(Level.INFO, "PropHunt: " + i + " disgiuses loaded");
    }

    public DisguiseCraftAPI getDcAPI() {
        return dcAPI;
    }

    public boolean isDisguised(Player p) {
        switch (disguisePluginType) {
            case DISGUISECRAFT:
                return (dcAPI.isDisguised(p));
            case LIBSDISGUISES:
                return DisguiseAPI.isDisguised(p);
            default:
                return false;
        }
    }

    public void disguisePlayer(Player p, SimpleDisguise d) {
        switch (disguisePluginType) {
            case DISGUISECRAFT:
                dcAPI.disguisePlayer(p, d.getDisguise(plugin));
            case LIBSDISGUISES:
                DisguiseAPI.disguiseToAll(p, d.getLibsDisguise());
            default:
                return;
        }
    }

    public void undisguisePlayer(Player p) {
        switch (disguisePluginType) {
            case DISGUISECRAFT:
                dcAPI.undisguisePlayer(p);
            case LIBSDISGUISES:
                DisguiseAPI.undisguiseToAll(p);
            default:
                return;
        }
    }

    public String getDisguiseName(Player p) {
        switch (disguisePluginType) {
            case DISGUISECRAFT:
                return parseIdToName(dcAPI.getDisguise(p).getBlockID());
            case LIBSDISGUISES:
                return DisguiseAPI.getDisguise(p).getType().equals(me.libraryaddict.disguise.disguisetypes.DisguiseType.FALLING_BLOCK) ? parseIdToName(((MiscDisguise) DisguiseAPI.getDisguise(p)).getId()) : DisguiseAPI.getDisguise(p).getEntity().getType().name();
            default:
                return "";
        }
    }


    private String parseIdToName(int id) {
        return Material.getMaterial(id).name();
    }

    public void randomDisguise(Player p, ArenaConfig ac) {
        if (preChosenDisguise.containsKey(p)) {
            SimpleDisguise simpleDisguise = preChosenDisguise.get(p);
            if (disguisePluginType.equals(DisguisePluginType.DISGUISECRAFT)) {
                Disguise ds = simpleDisguise.getDisguise(plugin);
                ds.setEntityID(plugin.dm.getDcAPI().newEntityID());
                if (plugin.dm.isDisguised(p)) {
                    plugin.dm.getDcAPI().changePlayerDisguise(p, ds);
                    PropHuntMessaging.sendMessage(p, MessageBank.DISGUISE_MESSAGE.getMsg() + parseDisguiseToName(simpleDisguise));
                } else {
                    plugin.dm.getDcAPI().disguisePlayer(p, ds);
                    PropHuntMessaging.sendMessage(p, MessageBank.DISGUISE_MESSAGE.getMsg() + parseDisguiseToName(simpleDisguise));
                }
                preChosenDisguise.remove(p);
                return;
            }
            if (disguisePluginType.equals(DisguisePluginType.LIBSDISGUISES)) {
                me.libraryaddict.disguise.disguisetypes.Disguise lds = simpleDisguise.getLibsDisguise();
                DisguiseAPI.disguiseToAll(p, lds);
                PropHuntMessaging.sendMessage(p, MessageBank.DISGUISE_MESSAGE.getMsg() + parseDisguiseToName(simpleDisguise));
                preChosenDisguise.remove(p);
                return;
            }
        }

        SimpleDisguise ds = getRandomDisguiseObject(ac.getArenaDisguises());

        if (ds == null) {
            PropHuntMessaging.sendMessage(p, MessageBank.DISGUISE_ERROR.getMsg());
            return;
        }

        if (disguisePluginType.equals(DisguisePluginType.DISGUISECRAFT)) {
            if (plugin.dm.isDisguised(p)) {
                plugin.dm.getDcAPI().changePlayerDisguise(p, ds.getDisguise(plugin));
                PropHuntMessaging.sendMessage(p, MessageBank.DISGUISE_MESSAGE.getMsg() + parseDisguiseToName(ds));
            } else {
                plugin.dm.getDcAPI().disguisePlayer(p, ds.getDisguise(plugin));
                PropHuntMessaging.sendMessage(p, MessageBank.DISGUISE_MESSAGE.getMsg() + parseDisguiseToName(ds));
            }
        }
        if (disguisePluginType.equals(DisguisePluginType.LIBSDISGUISES)) {
            me.libraryaddict.disguise.disguisetypes.Disguise lds = ds.getLibsDisguise();
            DisguiseAPI.disguiseToAll(p, lds);
            PropHuntMessaging.sendMessage(p, MessageBank.DISGUISE_MESSAGE.getMsg() + parseDisguiseToName(ds));
            preChosenDisguise.remove(p);
            return;
        }
    }

    public static String parseDisguiseToName(SimpleDisguise ds) {
        return ds.getName();
    }


    private static SimpleDisguise getRandomDisguiseObject(Map<Integer, SimpleDisguise> disguises) {
        int size = disguises.size();
        Random rnd = new Random();
        int random = rnd.nextInt(size);
        return disguises.get(random + 1);
    }

    public SimpleDisguise getSimpleDisguise(Player p) {
        switch (disguisePluginType) {
            case DISGUISECRAFT:
                if (dcAPI.getDisguise(p).type.equals(DisguiseType.FallingBlock)) {
                    return new SimpleDisguise(dcAPI.getDisguise(p).getBlockID(), (int)dcAPI.getDisguise(p).getBlockData(), null);
                } else {
                    return null;
                }
            case LIBSDISGUISES:
                if (DisguiseAPI.getDisguise(p).getType().equals(me.libraryaddict.disguise.disguisetypes.DisguiseType.FALLING_BLOCK)) {
                    return new SimpleDisguise(((MiscDisguise)DisguiseAPI.getDisguise(p)).getId(), ((MiscDisguise)DisguiseAPI.getDisguise(p)).getData(), null);
                } else {
                    return null;
                }
        }
        return null;
    }
}
