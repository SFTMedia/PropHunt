package me.tomski.prophunt;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.tomski.arenas.ArenaConfig;
import me.tomski.language.MessageBank;
import me.tomski.objects.Loadout;
import me.tomski.objects.SimpleDisguise;
import me.tomski.utils.PropHuntMessaging;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class LibsDisguiseManager extends DisguiseManager {

    private static PropHunt plugin;
    public static Map<Integer, SimpleDisguise> blockDisguises = new HashMap<Integer, SimpleDisguise>();
    public static Map<Player, SimpleDisguise> preChosenDisguise = new HashMap<Player, SimpleDisguise>();
    public static Map<Player, Loadout> loadouts = new HashMap<Player, Loadout>();


    public LibsDisguiseManager(PropHunt plugin) {
        super(plugin);
        int i = plugin.loadBlockDisguises();
        plugin.getLogger().log(Level.INFO, "PropHunt: " + i + " disgiuses loaded");
    }

    private Disguise getLibsDisguise(SimpleDisguise sd) {
        if (sd.getEntityType() == null) {
            return new MiscDisguise(DisguiseType.FALLING_BLOCK, sd.getID(), sd.getDamage());
        } else {
            return new MobDisguise(DisguiseType.getType(sd.getEntityType()));
        }
    }

    @Override
    public boolean isDisguised(Player p) {
        return DisguiseAPI.isDisguised(p);
    }

    @Override
    public void disguisePlayer(Player p, SimpleDisguise d) {
        Disguise dis = getLibsDisguise(d);
        dis.setViewSelfDisguise(true);
        DisguiseAPI.disguiseToAll(p, dis);
    }

    @Override
    public void undisguisePlayer(Player p) {
        DisguiseAPI.undisguiseToAll(p);
    }

    @Override
    public void undisguisePlayerEnd(Player p) {
        DisguiseAPI.undisguiseToAll(p);
    }

    @Override
    public String getDisguiseName(Player p) {
        return DisguiseAPI.getDisguise(p).getType().equals(me.libraryaddict.disguise.disguisetypes.DisguiseType.FALLING_BLOCK) ? parseIdToName(((MiscDisguise) DisguiseAPI.getDisguise(p)).getId()) : DisguiseAPI.getDisguise(p).getEntity().getType().name();
    }

    private String parseIdToName(int id) {
        return Material.getMaterial(id).name();
    }

    @Override
    public void randomDisguise(Player p, ArenaConfig ac) {
        if (preChosenDisguise.containsKey(p)) {
            SimpleDisguise simpleDisguise = preChosenDisguise.get(p);
            disguisePlayer(p, simpleDisguise);
            PropHuntMessaging.sendMessage(p, MessageBank.DISGUISE_MESSAGE.getMsg() + parseDisguiseToName(simpleDisguise));
            preChosenDisguise.remove(p);
            return;
        }
        SimpleDisguise ds = getRandomDisguiseObject(ac.getArenaDisguises());
        if (ds == null) {
            PropHuntMessaging.sendMessage(p, MessageBank.DISGUISE_ERROR.getMsg());
            return;
        }
        disguisePlayer(p, ds);
        PropHuntMessaging.sendMessage(p, MessageBank.DISGUISE_MESSAGE.getMsg() + parseDisguiseToName(ds));
        preChosenDisguise.remove(p);
    }

    public static String parseDisguiseToName(SimpleDisguise ds) {
        return ds.getName();
    }

    @Override
    public SimpleDisguise getSimpleDisguise(Player p) {
        if (DisguiseAPI.getDisguise(p).getType().equals(me.libraryaddict.disguise.disguisetypes.DisguiseType.FALLING_BLOCK)) {
            return new SimpleDisguise(((MiscDisguise) DisguiseAPI.getDisguise(p)).getId(), ((MiscDisguise) DisguiseAPI.getDisguise(p)).getData(), null);
        }
        return null;
    }

}
