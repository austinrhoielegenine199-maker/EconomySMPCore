package com.nyretha.economy.service;

import com.nyretha.NyrethaCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class EconomyConfigService {

    private final NyrethaCore plugin;
    private File configFile;
    private FileConfiguration config;

    private File dataFile;
    private FileConfiguration dataConfig;

    public EconomyConfigService(NyrethaCore plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "core/eco/economy.yml");
        if (!configFile.exists()) {
            plugin.saveResource("core/eco/economy.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

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

    public void reloadConfig() {
        loadConfig();
    }

    public void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save userdata.yml!");
        }
    }

    // --- Economy Balance ---

    public double getBalance(UUID uuid) {
        double defaultBal = config.getDouble("starting-balance", 1000.0);
        return dataConfig.getDouble("users." + uuid.toString() + ".balance", defaultBal);
    }

    public void setBalance(UUID uuid, double amount) {
        dataConfig.set("users." + uuid.toString() + ".balance", Math.max(0, amount));
        saveData();
    }

    public void addBalance(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public void removeBalance(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) - amount);
    }

    // --- Flakes Currency ---

    public double getFlakes(UUID uuid) {
        double defaultFlakes = config.getDouble("starting-flakes", 0.0);
        return dataConfig.getDouble("users." + uuid.toString() + ".flakes", defaultFlakes);
    }

    public void setFlakes(UUID uuid, double amount) {
        dataConfig.set("users." + uuid.toString() + ".flakes", Math.max(0, amount));
        saveData();
    }

    public void addFlakes(UUID uuid, double amount) {
        setFlakes(uuid, getFlakes(uuid) + amount);
    }

    public void removeFlakes(UUID uuid, double amount) {
        setFlakes(uuid, getFlakes(uuid) - amount);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
