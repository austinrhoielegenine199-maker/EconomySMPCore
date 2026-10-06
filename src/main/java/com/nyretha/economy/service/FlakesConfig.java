package com.nyretha.economy.service;

import com.nyretha.NyrethaCore;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.text.DecimalFormat;

public class FlakesConfig {

    private final NyrethaCore plugin;
    private File file;
    private FileConfiguration config;

    public FlakesConfig(NyrethaCore plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        file = new File(plugin.getDataFolder(), "core/eco/flakes.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource("core/eco/flakes.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getCurrencySymbol() {
        return config.getString("flakes.symbol", "❄");
    }

    public boolean isNumberFormattingEnabled() {
        return config.getBoolean("flakes.number_formatting.enabled", true);
    }

    public Sound getSound(String path) {
        String soundName = config.getString("flakes.sounds." + path, "");
        try {
            return Sound.valueOf(soundName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String formatAmount(long amount) {
        if (isNumberFormattingEnabled()) {
            DecimalFormat formatter = new DecimalFormat("#,###");
            return formatter.format(amount);
        }
        return String.valueOf(amount);
    }

    public String getMessage(String path) {
        return config.getString("messages." + path, "");
    }
}
