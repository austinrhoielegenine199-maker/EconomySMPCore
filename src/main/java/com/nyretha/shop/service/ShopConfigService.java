package com.nyretha.shop.service;

import com.nyretha.NyrethaCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ShopConfigService {

    private final NyrethaCore plugin;
    private FileConfiguration shopGuiConfig;
    private final Map<String, FileConfiguration> categoryConfigs = new HashMap<>();

    public ShopConfigService(NyrethaCore plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        categoryConfigs.clear();

        File shopGuiFile = new File(plugin.getDataFolder(), "core/shop/shopgui.yml");
        if (shopGuiFile.exists()) {
            this.shopGuiConfig = YamlConfiguration.loadConfiguration(shopGuiFile);
        }

        File categoriesDir = new File(plugin.getDataFolder(), "core/shop/categories");
        if (categoriesDir.exists() && categoriesDir.isDirectory()) {
            File[] files = categoriesDir.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files != null) {
                for (File file : files) {
                    categoryConfigs.put(file.getName(), YamlConfiguration.loadConfiguration(file));
                }
            }
        }
    }

    public FileConfiguration getMainShopConfig() {
        return shopGuiConfig;
    }

    public FileConfiguration getShopGuiConfig() {
        return shopGuiConfig;
    }

    public FileConfiguration getCategoryConfig(String fileName) {
        return categoryConfigs.get(fileName);
    }

    public Map<String, FileConfiguration> getCategoryConfigs() {
        return categoryConfigs;
    }
}
