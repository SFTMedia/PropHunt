package me.tomski.prophunt;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;

public class PlayerManagement {

    private static Map<String, ItemStack[]> playerInvents = new HashMap<String, ItemStack[]>();
    private static Map<String, Integer> playerXP = new HashMap<String, Integer>();
    private static Map<String, ItemStack[]> playerArmour = new HashMap<String, ItemStack[]>();


    public static void gameStartPlayer(Player p) {
        saveArmour(p);
        clearEffects(p);
        healPlayer(p);
        saveInvent(p);
        saveXp(p);
        clearEffects(p);
    }

    private static void clearPlayer(Player p) {
        p.getInventory().clear();
        p.setTotalExperience(0);
        p.setLevel(0);
        p.updateInventory();
    }

    private static void removeFromMaps(Player p) {
        if (playerInvents.containsKey(p.getName())) {
            playerInvents.remove(p.getName());
        }
        if (playerXP.containsKey(p.getName())) {
            playerXP.remove(p.getName());
        }
        if (playerArmour.containsKey(p.getName())) {
            playerArmour.remove(p.getName());
        }
    }

    @SuppressWarnings("deprecation")
    private static void saveArmour(Player p) {
        playerArmour.put(p.getName(), p.getInventory().getArmorContents().clone());
        p.updateInventory();
    }

    @SuppressWarnings("deprecation")
    private static void saveXp(Player p) {
        playerXP.put(p.getName(), p.getLevel());
        p.setLevel(0);
        p.updateInventory();
    }

    @SuppressWarnings("deprecation")
    private static void saveInvent(Player p) {
        playerInvents.put(p.getName(), p.getInventory().getContents().clone());
        p.getInventory().clear();
        p.updateInventory();
    }

    @SuppressWarnings("deprecation")
    private static void healPlayer(Player p) {
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.updateInventory();
    }

    private static void clearEffects(Player p) {
        for (PotionEffect pe : p.getActivePotionEffects()) {
            p.removePotionEffect(pe.getType());
        }
    }

    public static void gameRestorePlayer(Player p) {
        clearPlayer(p);
        restoreXp(p);
        restoreInvent(p);
        restoreArmour(p);
        removeFromMaps(p);
        clearEffects(p);
        System.out.println("restored " + p.getName());
    }

    @SuppressWarnings("deprecation")
    private static void restoreInvent(Player p) {
        if (playerInvents.containsKey(p.getName())) {
            p.getInventory().setContents(playerInvents.get(p.getName()));
        }
    }

    @SuppressWarnings("deprecation")
    private static void restoreXp(Player p) {
        if (playerXP.containsKey(p.getName())) {
            if (playerXP.get(p.getName()) == null) {
                return;
            }
            p.setTotalExperience(0);
            p.setLevel(playerXP.get(p.getName()));
            p.updateInventory();
        } else {
            p.setExp(0);
            p.setLevel(0);
        }
    }

    @SuppressWarnings("deprecation")
    private static void restoreArmour(Player p) {
        p.getInventory().setArmorContents(playerArmour.get(p.getName()));
        p.updateInventory();
    }

}
