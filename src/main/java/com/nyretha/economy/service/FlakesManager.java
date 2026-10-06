package com.nyretha.economy.service;

import com.nyretha.NyrethaCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlakesManager {

    private final NyrethaCore plugin;
    private final Map<UUID, Long> flakesBalance = new HashMap<>();
    private File file;
    private FileConfiguration dataConfig;

    public FlakesManager(NyrethaCore plugin) {
        this.plugin = plugin;
        loadData();
    }

    public long getBalance(UUID uuid) {
        return flakesBalance.getOrDefault(uuid, 0L);
    }

    public void setBalance(UUID uuid, long amount) {
        flakesBalance.put(uuid, Math.max(0L, amount));
        saveData();
    }

    public boolean deposit(UUID uuid, long amount) {
        if (amount <= 0) return false;
        flakesBalance.put(uuid, getBalance(uuid) + amount);
        saveData();
        return true;
    }

    public boolean withdraw(UUID uuid, long amount) {
        if (amount <= 0) return false;
        long current = getBalance(uuid);
        if (current < amount) return false;

        flakesBalance.put(uuid, current - amount);
        saveData();
        return true;
    }

    public boolean has(UUID uuid, long amount) {
        return getBalance(uuid) >= amount;
    }

    public void loadData() {
        file = new File(plugin.getDataFolder(), "core/eco/flakes_data.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create flakes_data.yml!");
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(file);

        if (dataConfig.contains("flakes")) {
            var section = dataConfig.getConfigurationSection("flakes");
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    flakesBalance.put(UUID.fromString(key), section.getLong(key));
                }
            }
        }
    }

    public void saveData() {
        for (Map.Entry<UUID, Long> entry : flakesBalance.entrySet()) {
            dataConfig.set("flakes." + entry.getKey().toString(), entry.getValue());
        }
        try {
            dataConfig.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save flakes_data.yml!");
        }
    }
}
