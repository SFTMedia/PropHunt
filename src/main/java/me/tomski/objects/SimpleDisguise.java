package me.tomski.objects;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

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
        this.name = initName();
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

    public Integer getID() {
        return id;
    }

    public int getDamage() {
        return damage;
    }

    public EntityType getEntityType() {
        return ent;
    }
}
