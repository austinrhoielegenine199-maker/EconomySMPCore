package com.nyretha.economy.service;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class EconomyConfigService {
    private final Plugin plugin;
    private File configFile;
    private FileConfiguration config;

    public EconomyConfigService(Plugin plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "eco/economy.yml");
        }
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("eco/economy.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        InputStream defStream = plugin.getResource("eco/economy.yml");
        if (defStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defStream, StandardCharsets.UTF_8));
            config.setDefaults(defConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public String getMessage(String path, String def) {
        String raw = getConfig().getString("messages." + path, def);
        return ChatColor.translateAlternateColorCodes('&', raw.replaceAll("&#([0-9a-fA-F]{6})", "§x§$1§$2§$3§$4§$5§$6"));
    }

    public String getCurrencySymbol() {
        return getConfig().getString("economy.currency.symbol", "$");
    }

    public String getSound(String key) {
        return getConfig().getString("economy.sounds." + key, null);
    }
}
