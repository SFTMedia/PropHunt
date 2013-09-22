package me.tomski.objects;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Loadout {

    private Player player;
    List<ItemStack> loadout = new ArrayList<ItemStack>();

    public Loadout(Player p) {
        this.player = p;
    }

    public void addItem(ItemStack stack) {
        loadout.add(stack);
    }

    public void giveLoadout() {
        for (ItemStack stack : loadout) {
            player.getInventory().addItem(stack);
        }
        player.updateInventory();
    }
}
