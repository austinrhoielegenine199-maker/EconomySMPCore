package com.nyretha.shop.utils;

import com.nyretha.shop.Shop;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PlayerDataManager {

    private final Shop plugin;
    private File file;
    private FileConfiguration config;

    public PlayerDataManager(Shop plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        this.file = new File(plugin.getDataFolder(), "core/shop/playerdata.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create core/shop/playerdata.yml");
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() { return config; }

    public void saveAsync() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::saveAllSync);
    }

    public synchronized void saveAllSync() {
        if (config == null || file == null) return;
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save core/shop/playerdata.yml: " + e.getMessage());
        }
    }
}
