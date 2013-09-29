package me.tomski.prophuntstorage;

import me.tomski.prophunt.PropHunt;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;


public class ShopConfig {


    public FileConfiguration StorageFilef = null;
    private File customConfigFile = null;
    private PropHunt plugin;

    public ShopConfig(PropHunt plugin) {
        this.plugin = plugin;
        getShopConfig().options().copyDefaults(true);
        saveShopConfig();
    }

    public void reloadShopConfig() {
        if (customConfigFile == null) {
            customConfigFile = new File(plugin.getDataFolder(), "Shop.yml");
        }
        StorageFilef = YamlConfiguration.loadConfiguration(customConfigFile);
        InputStream defConfigStream = plugin.getResource("Shop.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration
                    .loadConfiguration(defConfigStream);
            StorageFilef.setDefaults(defConfig);
        }
    }

    public FileConfiguration getShopConfig() {
        if (StorageFilef == null) {
            this.reloadShopConfig();
        }
        return StorageFilef;
    }

    public void saveShopConfig() {
        if (StorageFilef == null || customConfigFile == null) {
            return;
        }
        try {
            getShopConfig().save(customConfigFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE,
                    "Could not save config to " + customConfigFile, ex);
        }
    }

}
