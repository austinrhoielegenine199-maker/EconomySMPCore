package com.nyretha.economy.service;

import com.nyretha.NyrethaCore;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.text.DecimalFormat;

public class EconomyConfig {

    private final NyrethaCore plugin;
    private File file;
    private FileConfiguration config;

    public EconomyConfig(NyrethaCore plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        file = new File(plugin.getDataFolder(), "core/eco/economy.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource("core/eco/economy.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getCurrencySymbol() {
        return config.getString("economy.currency.symbol", "$");
    }

    public boolean isNumberFormattingEnabled() {
        return config.getBoolean("economy.number_formatting.enabled", true);
    }

    public Sound getSound(String path) {
        String soundName = config.getString("economy.sounds." + path, "");
        try {
            return Sound.valueOf(soundName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String formatAmount(double amount) {
        if (isNumberFormattingEnabled()) {
            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            return formatter.format(amount);
        }
        return String.valueOf(amount);
    }

    public String getMessage(String path) {
        return config.getString("messages." + path, "");
    }
}
