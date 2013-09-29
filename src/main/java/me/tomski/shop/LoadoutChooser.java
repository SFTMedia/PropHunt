package me.tomski.shop;


import me.tomski.language.MessageBank;
import me.tomski.objects.Loadout;
import me.tomski.prophunt.DisguiseManager;
import me.tomski.prophunt.GameManager;
import me.tomski.prophunt.PropHunt;
import me.tomski.utils.PropHuntMessaging;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LoadoutChooser implements Listener {

    private PropHunt plugin;
    private List<Player> inInventory = new ArrayList<Player>();

    public LoadoutChooser(PropHunt plugin) {
        this.plugin = plugin;
    }

    public void openBlockShop(Player p) {
        if (!GameManager.playersWaiting.contains(p.getName())) {
            PropHuntMessaging.sendMessage(p, MessageBank.NOT_IN_LOBBY.getMsg());
            return;
        }
        Inventory inv = Bukkit.createInventory(p, getShopSize(plugin.getShopSettings().itemChoices.size()), MessageBank.LOADOUT_NAME.getMsg());
        for (ShopItem sI : plugin.getShopSettings().itemChoices) {
            sI.addToInventory(inv, p);
        }
        p.openInventory(inv);
        inInventory.add(p);

    }

    @EventHandler
    public void onInventClick(InventoryClickEvent e) {
        if (inInventory.contains((Player) e.getWhoClicked())) {
            if (e.getCurrentItem() != null) {
                if (!hasPermsForItem((Player) e.getWhoClicked(), e.getCurrentItem())) {
                    PropHuntMessaging.sendMessage((Player) e.getWhoClicked(), MessageBank.NO_ITEM_CHOICE_PERMISSION.getMsg());
                    e.setCancelled(true);
                    return;
                }
                if (e.getCurrentItem().getType().equals(Material.AIR)) {
                    return;
                }
                if (GameManager.playersWaiting.contains(((Player) e.getWhoClicked()).getName())) {
                    if (!DisguiseManager.loadouts.containsKey((Player) e.getWhoClicked())) {
                        DisguiseManager.loadouts.put((Player) e.getWhoClicked(), new Loadout((Player) e.getWhoClicked()));
                        DisguiseManager.loadouts.get((Player) e.getWhoClicked()).addItem(e.getCurrentItem());
                    } else {
                        DisguiseManager.loadouts.get((Player) e.getWhoClicked()).addItem(e.getCurrentItem());
                    }
                    PropHuntMessaging.sendMessage((Player) e.getWhoClicked(), MessageBank.ITEM_CHOSEN.getMsg() + e.getCurrentItem().getItemMeta().getDisplayName());
                    e.getView().close();
                } else {
                    PropHuntMessaging.sendMessage((Player) e.getWhoClicked(), MessageBank.BLOCK_ACCESS_IN_GAME.getMsg());
                }
            }
        }
    }

    private boolean hasPermsForItem(Player player, ItemStack currentItem) {
        if (currentItem.getData().getData() == 0) {
            return player.hasPermission("prophunt.loadout." + currentItem.getTypeId());
        } else {
            return player.hasPermission("prophunt.loadout." + currentItem.getTypeId() + "-" + currentItem.getData().getData());
        }
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent e) {
        if (inInventory.contains(e.getPlayer())) {
            inInventory.remove(e.getPlayer());
        }
    }

    private int getShopSize(int n) {
        return (int) Math.ceil(n / 9.0) * 9;
    }
}
