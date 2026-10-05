package com.nyretha.economy.service;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EconomyConfigService {

    private final JavaPlugin plugin;
    private FileConfiguration economyConfig;
    private File economyFile;

    public EconomyConfigService(JavaPlugin plugin) {
        this.plugin = plugin;
        loadEconomyConfig();
    }

    public void loadEconomyConfig() {
        if (economyFile == null) {
            economyFile = new File(plugin.getDataFolder(), "eco/economy.yml");
        }
        if (!economyFile.exists()) {
            economyFile.getParentFile().mkdirs();
            if (plugin.getResource("eco/economy.yml") != null) {
                plugin.saveResource("eco/economy.yml", false);
            } else {
                try {
                    economyFile.createNewFile();
                } catch (Exception e) {
                    plugin.getLogger().severe("Could not create economy.yml!");
                }
            }
        }
        economyConfig = YamlConfiguration.loadConfiguration(economyFile);

        InputStream defConfigStream = plugin.getResource("eco/economy.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8));
            economyConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (economyConfig == null) {
            loadEconomyConfig();
        }
        return economyConfig;
    }

    public void reloadConfig() {
        if (economyFile == null) {
            economyFile = new File(plugin.getDataFolder(), "eco/economy.yml");
        }
        economyConfig = YamlConfiguration.loadConfiguration(economyFile);
    }

    public String getMessage(String key, String def) {
        return colorize(getConfig().getString("messages." + key, def));
    }

    public String getCurrencySymbol() {
        return colorize(getConfig().getString("currency.symbol", "$"));
    }

    public String getSound(String key) {
        return getConfig().getString("sounds." + key, "");
    }

    public String colorize(String message) {
        if (message == null) return "";

        Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(message);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append('§').append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}
