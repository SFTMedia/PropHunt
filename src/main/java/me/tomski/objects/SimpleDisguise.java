package me.tomski.objects;

import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.tomski.prophunt.PropHunt;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

import java.util.LinkedList;

public class SimpleDisguise {

    private int id;
    private int damage;
    private EntityType ent;
    private String name;

    public SimpleDisguise(int id, int damage, EntityType ent) {
        this.id = id;
        this.damage = damage;
        this.ent = ent;
        this.name = initName();
    }

    public SimpleDisguise(String configString) {
        if (configString.startsWith("e:")) {
            ent = EntityType.fromName(configString.substring(2));
        } else if (configString.split(":").length == 2) {
            this.id = Integer.parseInt(configString.split(":")[0]);
            this.damage = Integer.parseInt(configString.split(":")[1]);
        } else {
            this.id = Integer.parseInt(configString);
        }
    }

    private String initName() {
        if (ent == null) {
            return Material.getMaterial(id).name();
        } else {
            return ent.name().toLowerCase().replaceAll("_", " ");
        }
    }

    public String getName() {
        return name;
    }

    public Disguise getDisguise(PropHunt plugin) {
        if (ent == null) {
            LinkedList<String> data = new LinkedList<String>();
            data.add("blockID:" + id);
            data.add("blockData:" + damage);
            return new Disguise(plugin.dm.getDcAPI().newEntityID(), data, DisguiseType.FallingBlock);
        } else {
            return new Disguise(plugin.dm.getDcAPI().newEntityID(), "", DisguiseType.fromString(ent.name()));
        }

    }

    public me.libraryaddict.disguise.disguisetypes.Disguise getLibsDisguise() {
        if (ent == null) {
            return new MiscDisguise(me.libraryaddict.disguise.disguisetypes.DisguiseType.FALLING_BLOCK, id, damage);
        } else {
            return new me.libraryaddict.disguise.disguisetypes.MobDisguise(me.libraryaddict.disguise.disguisetypes.DisguiseType.getType(ent));
        }
    }


    public Integer getID() {
        return id;
    }

    public int getDamage() {
        return damage;
    }
}
