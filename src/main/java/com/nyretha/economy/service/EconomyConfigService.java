package com.nyretha.economy.service;

import com.nyretha.NyrethaCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class EconomyConfigService {

    private final NyrethaCore plugin;
    private FileConfiguration config;
    private File configFile;

    private FileConfiguration dataConfig;
    private File dataFile;

    public EconomyConfigService(NyrethaCore plugin) {
        this.plugin = plugin;
        reloadConfig();
        loadData();
    }

    public void reloadConfig() {
        configFile = new File(plugin.getDataFolder(), "core/eco/economy.yml");
        if (!configFile.exists()) {
            plugin.saveResource("core/eco/economy.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void loadData() {
        dataFile = new File(plugin.getDataFolder(), "core/eco/userdata.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create userdata.yml!");
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save userdata.yml!");
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    // --- Balance Methods ---

    public double getBalance(UUID uuid) {
        return dataConfig.getDouble("players." + uuid.toString() + ".balance", 0.0);
    }

    public void setBalance(UUID uuid, double amount) {
        dataConfig.set("players." + uuid.toString() + ".balance", Math.max(0.0, amount));
        saveData();
    }

    public void addBalance(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public void removeBalance(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) - amount);
    }

    // --- Flakes Methods ---

    public double getFlakes(UUID uuid) {
        return dataConfig.getDouble("players." + uuid.toString() + ".flakes", 0.0);
    }

    public void setFlakes(UUID uuid, double amount) {
        dataConfig.set("players." + uuid.toString() + ".flakes", Math.max(0.0, amount));
        saveData();
    }

    public void addFlakes(UUID uuid, double amount) {
        setFlakes(uuid, getFlakes(uuid) + amount);
    }

    public void removeFlakes(UUID uuid, double amount) {
        setFlakes(uuid, getFlakes(uuid) - amount);
    }
}
