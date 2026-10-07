package com.nyretha.shop.flakes;

import com.nyretha.shop.Shop;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FlakesManager {

    private final Shop plugin;
    private final ConcurrentHashMap<UUID, Integer> flakeBalances = new ConcurrentHashMap<>();
    private File flakesFile;
    private FileConfiguration flakesConfig;

    public FlakesManager(Shop plugin) {
        this.plugin = plugin;
        loadFlakesData();
    }

    public void loadFlakesData() {
        this.flakesFile = new File(plugin.getDataFolder(), "core/shop/flakes.yml");
        if (!flakesFile.exists()) {
            plugin.saveResource("core/shop/flakes.yml", false);
        }
        this.flakesConfig = YamlConfiguration.loadConfiguration(flakesFile);

        flakeBalances.clear();
        if (flakesConfig.contains("players")) {
            for (String key : flakesConfig.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    int amount = flakesConfig.getInt("players." + key);
                    flakeBalances.put(uuid, amount);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public int getFlakes(UUID uuid) {
        return flakeBalances.getOrDefault(uuid, 0);
    }

    public void addFlakes(UUID uuid, int amount) {
        flakeBalances.put(uuid, getFlakes(uuid) + amount);
        saveAsync();
    }

    public void removeFlakes(UUID uuid, int amount) {
        flakeBalances.put(uuid, Math.max(0, getFlakes(uuid) - amount));
        saveAsync();
    }

    public void saveAsync() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::saveSync);
    }

    public synchronized void saveSync() {
        if (flakesConfig == null || flakesFile == null) return;
        for (var entry : flakeBalances.entrySet()) {
            flakesConfig.set("players." + entry.getKey().toString(), entry.getValue());
        }
        try {
            flakesConfig.save(flakesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save core/shop/flakes.yml: " + e.getMessage());
        }
    }
}
