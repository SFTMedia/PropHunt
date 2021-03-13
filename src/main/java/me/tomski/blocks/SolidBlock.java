package me.tomski.blocks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.FieldAccessException;
import me.tomski.listeners.PropHuntListener;
import me.tomski.objects.SimpleDisguise;
import me.tomski.prophunt.PropHunt;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;


public class SolidBlock {


    public Player owner;
    public Location loc;
    public int id;
    int damage;
    ProtocolManager pm;
    PacketContainer blockChange;
    private SimpleDisguise d;
    public boolean dead = false;

    public SolidBlock(Location loc, Player p, ProtocolManager pm, PropHunt plugin) throws InvocationTargetException {
        this.loc = loc.clone();
        this.pm = pm;
        d = plugin.dm.getSimpleDisguise(p);
        this.id = d.getID();
        this.damage = d.getDamage();

        blockChange = getBlockPacket();
        this.owner = p;
        plugin.hidePlayer(owner, owner.getInventory().getArmorContents());
        PropHuntListener.tempIgnoreUndisguise.add(owner);
    }

    public boolean hasMoved(PropHunt plugin) {
        if (owner.getLocation().getBlockX() != loc.getBlockX()) {
            return true;
        }
        if (owner.getLocation().getBlockZ() != loc.getBlockZ()) {
            return true;
        }
        if (owner.getLocation().getBlockY() != loc.getBlockY()) {
            return true;
        }
        try {
            sendPacket(plugin.getServer().getOnlinePlayers().toArray(new Player[]{}));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    private PacketContainer getBlockPacket() {
        blockChange = pm.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
        try {
            blockChange.getIntegers().
                    write(0, loc.getBlockX()).
                    write(1, loc.getBlockY()).
                    write(2, loc.getBlockZ()).
                    write(3, damage);
            blockChange.getBlocks().write(0, Material.getMaterial(id));
        } catch (FieldAccessException e) {
            System.out.println("PropHunt: Error with block change packet");
        }
        return blockChange;
    }

    public void unSetBlock(PropHunt plugin) throws InvocationTargetException {
        dead = true;
        blockChange = pm.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
        try {
            blockChange.getIntegers().
                    write(0, loc.getBlockX()).
                    write(1, loc.getBlockY()).
                    write(2, loc.getBlockZ()).
                    write(3, 0);
            blockChange.getBlocks().write(0, Material.AIR);
        } catch (FieldAccessException e) {
            System.out.println("PropHunt: Error with block change packet");
        }

        PropHuntListener.tempIgnoreUndisguise.remove(owner);
        sendPacket(plugin.getServer().getOnlinePlayers().toArray(new Player[]{}));
        plugin.dm.disguisePlayer(owner, d);
    }

    public void sendPacket(Player[] players) throws InvocationTargetException {
        for (Player p : players) {
            if (p.equals(owner)) {
                continue;
            }
            pm.sendServerPacket(p, blockChange);
        }
    }
}
