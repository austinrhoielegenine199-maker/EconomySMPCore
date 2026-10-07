package com.nyretha.shop.utils;

import com.nyretha.shop.Shop;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LangManager {

    private final Shop plugin;
    private FileConfiguration langConfig;

    public LangManager(Shop plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "core/shop/lang.yml");
        if (!file.exists()) {
            plugin.saveResource("core/shop/lang.yml", false);
        }
        this.langConfig = YamlConfiguration.loadConfiguration(file);
    }

    public String get(String path) {
        return langConfig.getString(path, "&cMissing message: " + path);
    }
}
