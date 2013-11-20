package me.tomski.prophunt;


import me.tomski.enums.EconomyType;
import me.tomski.prophuntstorage.ShopConfig;
import me.tomski.shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShopSettings {

    public List<ShopItem> blockChoices = new ArrayList<ShopItem>();
    public List<ShopItem> itemChoices = new ArrayList<ShopItem>();

    public static String currencyName;
    public static boolean usingVault;
    public static boolean enabled;
    public static EconomyType economyType;

    public static double pricePerHiderKill;
    public static double pricePerSeekerKill;

    public static double pricePerSecondsHidden;

    public static double priceSeekerWin;
    public static double priceHiderWin;

    private PropHunt plugin;

    private static PropHunt staticPlugin;

    public ShopSettings(PropHunt plugin) {
        staticPlugin = plugin;
        this.plugin = plugin;
    }

    public List<ShopItem> generateBlockChoices(PropHunt plugin, ShopConfig shopConfig) {
        String path = "Disguises";
        Set<String> keys = shopConfig.getShopConfig().getConfigurationSection(path).getKeys(false);
        for (String key : keys) {
            String name = shopConfig.getShopConfig().getString(path + "." + key + ".Name");
            String Id = shopConfig.getShopConfig().getString(path + "." + key + ".Id");
            double cost = shopConfig.getShopConfig().getDouble(path + "." + key + ".Cost");
            ItemStack stack = parseStringToStack(plugin, Id);
            if (stack != null) {
                ShopItem item = new ShopItem(plugin, stack, name, (int) cost, getStackPermission(stack));
                blockChoices.add(item);
                plugin.getLogger().info("Loaded Shop disguise: " + key);
            } else {
                plugin.getLogger().warning("DISABLING SHOP, error with item : " + name);
                return blockChoices;
            }
        }
        return blockChoices;
    }

    public List<ShopItem> generateItemChoices(PropHunt plugin, ShopConfig shopConfig) {
        String path = "Items";
        Set<String> keys = shopConfig.getShopConfig().getConfigurationSection(path).getKeys(false);
        for (String key : keys) {
            String name = shopConfig.getShopConfig().getString(path + "." + key + ".Name");
            String Id = shopConfig.getShopConfig().getString(path + "." + key + ".Id");
            double cost = shopConfig.getShopConfig().getDouble(path + "." + key + ".Cost");
            ItemStack stack = parseITEMStringToStack(Id);
            if (stack != null) {
                ShopItem item = new ShopItem(plugin, stack, name, (int) cost, getStackPermission(stack));
                itemChoices.add(item);
                plugin.getLogger().info("Loaded Shop item: " + key);
            } else {
                plugin.getLogger().warning("DISABLING SHOP, error with item : " + name);
                return itemChoices;
            }
        }
        return itemChoices;
    }

    public static ItemStack getCustomItem(String s) {
        ItemStack stack;
        String mat = staticPlugin.shopConfig.getShopConfig().getString("PropHuntItems." + s);
        if (mat.split(":").length == 2) {
            int id = Integer.valueOf(s.split(":")[0]);
            int damage = Integer.valueOf(s.split(":")[1]);
            stack = new ItemStack(id, 1, (byte) damage);
            return stack;
        } else if (mat.split(":").length == 1) {
            if (Material.getMaterial(Integer.valueOf(s)) != null) {
                stack = new ItemStack(Material.getMaterial(Integer.valueOf(s)), 1);
                return stack;
            }
        } else {
            staticPlugin.getLogger().warning("Error with Custom item: " + s);
            return null;
        }
        return null;
    }

    public void loadShopItems(PropHunt plugin) {
        blockChoices = generateBlockChoices(plugin, plugin.shopConfig);
        if (blockChoices == null)
            enabled = false;
        itemChoices = generateItemChoices(plugin, plugin.shopConfig);
        if (itemChoices == null) {
            enabled = false;
        }
    }

    private String getStackPermission(ItemStack currentItem) {
        if (currentItem.getData().getData() == 0) {
            return "prophunt.blockchooser." + currentItem.getTypeId();
        } else {
            return "prophunt.blockchooser." + currentItem.getTypeId() + "-" + currentItem.getData().getData();
        }
    }


    private ItemStack parseStringToStack(PropHunt plugin, String s) {
        ItemStack stack;
        if (s.split(":").length == 2) {
            int id = Integer.valueOf(s.split(":")[0]);
            int damage = Integer.valueOf(s.split(":")[1]);
            stack = new ItemStack(id, 1, (byte) damage);
            return stack;
        } else if (s.split(":").length == 1) {
            if (Material.getMaterial(Integer.valueOf(s)) != null) {
                stack = new ItemStack(Material.getMaterial(Integer.valueOf(s)), 1);
                return stack;
            }
        } else {
            plugin.getLogger().warning("Error with shop item with ID: " + s);
            return null;
        }
        return null;
    }

    private ItemStack parseITEMStringToStack(String s) {
        ItemStack stack = null;
        String[] enchantsplit = s.split(" ");
        if (enchantsplit.length > 1) {
            //get enchants
            String item = enchantsplit[0];
            String enchants = enchantsplit[1];
            String[] totalenchants = enchants.split(";");
            int ENCHANTID = 0;
            int ENCHANTLEVEL = 0;
            Map<Enchantment, Integer> TOTEnchants = new HashMap<Enchantment, Integer>();
            int itemint = 0;
            try {
                itemint = Integer.parseInt(item);
            } catch (NumberFormatException nfe) {
                return null;
            }
            int i = totalenchants.length;
            for (int z = 0; z < i; ) {
                String[] subsplit = totalenchants[z].split(":");
                try {
                    ENCHANTID = Integer.parseInt(subsplit[0]);
                    ENCHANTLEVEL = Integer.parseInt(subsplit[1]);
                } catch (NumberFormatException nfe) {
                    return null;
                }
                TOTEnchants.put(Enchantment.getById(ENCHANTID), ENCHANTLEVEL);
                z++;

            }
            stack = new ItemStack(itemint, 1);
            stack.addUnsafeEnchantments(TOTEnchants);
            return stack;
        }
        String[] damagesplit = s.split(":");
        if (damagesplit.length > 2) {
            //get damage value
            String id = damagesplit[0];
            String damage = damagesplit[1];
            String amount = damagesplit[2];
            int ID = 0;
            short DAMAGE = 0;
            int AMOUNT = 0;
            try {
                ID = Integer.parseInt(id);
                DAMAGE = Short.parseShort(damage);
                AMOUNT = Integer.parseInt(amount);
            } catch (NumberFormatException NFE) {
                return null;
            }
            stack = new ItemStack(Material.getMaterial(ID), AMOUNT, DAMAGE);
            return stack;
        }
        //simple stack
        String[] normalsplit = s.split(":");
        String id = normalsplit[0];
        String amount = normalsplit[1];
        int ID = 0;
        int AMOUNT = 0;
        try {
            ID = Integer.parseInt(id);
            AMOUNT = Integer.parseInt(amount);
        } catch (NumberFormatException NFE) {
            return null;
        }

        stack = new ItemStack(Material.getMaterial(ID), AMOUNT);
        return stack;
    }

}
