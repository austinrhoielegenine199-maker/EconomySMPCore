package com.nyretha.economy.service;

import com.nyretha.NyrethaCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class EconomyConfigService {

    private final NyrethaCore plugin;
    private FileConfiguration config;
    private File configFile;

    public EconomyConfigService(NyrethaCore plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        configFile = new File(plugin.getDataFolder(), "core/eco/economy.yml");
        if (!configFile.exists()) {
            plugin.saveResource("core/eco/economy.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
