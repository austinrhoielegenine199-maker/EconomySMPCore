package com.nyretha.economy.service;

import com.nyretha.NyrethaCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {

    private final NyrethaCore plugin;
    private final Map<UUID, Double> balances = new HashMap<>();
    private File file;
    private FileConfiguration dataConfig;

    public EconomyManager(NyrethaCore plugin) {
        this.plugin = plugin;
        loadData();
    }

    public double getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, 0.0);
    }

    public void setBalance(UUID uuid, double amount) {
        balances.put(uuid, Math.max(0.0, amount));
        saveData();
    }

    public boolean deposit(UUID uuid, double amount) {
        if (amount <= 0) return false;
        balances.put(uuid, getBalance(uuid) + amount);
        saveData();
        return true;
    }

    public boolean withdraw(UUID uuid, double amount) {
        if (amount <= 0) return false;
        double current = getBalance(uuid);
        if (current < amount) return false;

        balances.put(uuid, current - amount);
        saveData();
        return true;
    }

    public boolean has(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }

    public void loadData() {
        file = new File(plugin.getDataFolder(), "core/eco/balances.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create balances.yml!");
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(file);

        if (dataConfig.contains("balances")) {
            var section = dataConfig.getConfigurationSection("balances");
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    balances.put(UUID.fromString(key), section.getDouble(key));
                }
            }
        }
    }

    public void saveData() {
        for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
            dataConfig.set("balances." + entry.getKey().toString(), entry.getValue());
        }
        try {
            dataConfig.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save balances.yml!");
        }
    }
}
