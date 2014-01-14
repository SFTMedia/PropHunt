package me.tomski.prophunt;

import me.tomski.arenas.ArenaConfig;
import me.tomski.language.MessageBank;
import me.tomski.objects.Loadout;
import me.tomski.objects.SimpleDisguise;
import me.tomski.utils.PropHuntMessaging;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;

public class DisguiseCraftManager extends DisguiseManager implements Listener {


    private PropHunt plugin;
    public static Map<Integer, SimpleDisguise> blockDisguises = new HashMap<Integer, SimpleDisguise>();
    public static Map<Player, SimpleDisguise> preChosenDisguise = new HashMap<Player, SimpleDisguise>();
    public static Map<Player, Loadout> loadouts = new HashMap<Player, Loadout>();

    public DisguiseCraftAPI dcAPI;

    public DisguiseCraftManager(PropHunt plugin) {
        super(plugin);
        dcAPI = DisguiseCraft.getAPI();
        int i = plugin.loadBlockDisguises();
        plugin.getLogger().log(Level.INFO, "PropHunt: " + i + " disgiuses loaded");
    }

    public DisguiseCraftAPI getDcAPI() {
        return dcAPI;
    }

    private Disguise getDisguise(SimpleDisguise sd) {
        if (sd.getEntityType() == null) {
            LinkedList<String> data = new LinkedList<String>();
            data.add("blockID:" + sd.getID());
            data.add("blockData:" + sd.getDamage());
            int id = getDcAPI().newEntityID();
            return new Disguise(id, data, DisguiseType.FallingBlock);
        } else {
            int id = getDcAPI().newEntityID();
            return new Disguise(id, "", DisguiseType.fromString(sd.getEntityType().name()));
        }

    }

    @Override
    public boolean isDisguised(Player p) {
        return (dcAPI.isDisguised(p));
    }

    @Override
    public void disguisePlayer(Player p, SimpleDisguise d) {
        dcAPI.disguisePlayer(p, getDisguise(d));
    }

    @Override
    public void undisguisePlayer(Player p) {
        dcAPI.undisguisePlayer(p, false);
    }

    @Override
    public void undisguisePlayerEnd(Player p) {
        dcAPI.undisguisePlayer(p, true);
    }

    @Override
    public String getDisguiseName(Player p) {
        if (isDisguised(p)) {
            if (getSimpleDisguise(p) !=null) {
                return parseIdToName(dcAPI.getDisguise(p).getBlockID());
            }
        }
        return "None";
    }

    private String parseIdToName(int id) {
        return Material.getMaterial(id).name();
    }

    @Override
    public void randomDisguise(Player p, ArenaConfig ac) {
        if (preChosenDisguise.containsKey(p)) {
            SimpleDisguise simpleDisguise = preChosenDisguise.get(p);
            Disguise ds = getDisguise(simpleDisguise);
            ds.setEntityID(getDcAPI().newEntityID());
            if (isDisguised(p)) {
                getDcAPI().changePlayerDisguise(p, ds);
                PropHuntMessaging.sendMessage(p, MessageBank.DISGUISE_MESSAGE.getMsg() + parseDisguiseToName(simpleDisguise));
            } else {
                getDcAPI().disguisePlayer(p, ds);
                PropHuntMessaging.sendMessage(p, MessageBank.DISGUISE_MESSAGE.getMsg() + parseDisguiseToName(simpleDisguise));
            }
            preChosenDisguise.remove(p);
            return;
        }
        SimpleDisguise ds = getRandomDisguiseObject(ac.getArenaDisguises());
        if (ds == null) {
            PropHuntMessaging.sendMessage(p, MessageBank.DISGUISE_ERROR.getMsg());
            return;
        }
        if (isDisguised(p)) {
            getDcAPI().changePlayerDisguise(p, getDisguise(ds));
            PropHuntMessaging.sendMessage(p, MessageBank.DISGUISE_MESSAGE.getMsg() + parseDisguiseToName(ds));
        } else {
            getDcAPI().disguisePlayer(p, getDisguise(ds));
            PropHuntMessaging.sendMessage(p, MessageBank.DISGUISE_MESSAGE.getMsg() + parseDisguiseToName(ds));
        }


    }

    @Override
    public void toggleBlockLock(PlayerToggleSneakEvent e) {
        Disguise d = getDcAPI().getDisguise(e.getPlayer());
        if (d.type.equals(DisguiseType.FallingBlock)) {
            if (e.isSneaking()) {
                d.addSingleData("blocklock");
                PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.TOGGLE_BLOCK_LOCK_ON.getMsg());
            } else {
                d.data.remove("blocklock");
                PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.TOGGLE_BLOCK_LOCK_OFF.getMsg());
            }
        }
    }

    @Override
    public SimpleDisguise getSimpleDisguise(Player p) {
        if (dcAPI.getDisguise(p).type.equals(DisguiseType.FallingBlock)) {
            return new SimpleDisguise(dcAPI.getDisguise(p).getBlockID(), (int) dcAPI.getDisguise(p).getBlockData(), null);
        }
        return null;
    }

}
