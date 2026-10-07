package com.nyretha.sell.manager;

import com.nyretha.sell.model.PriceModel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PriceManager {

    private final JavaPlugin plugin;
    private final Map<String, PriceModel> prices = new ConcurrentHashMap<>();

    public PriceManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadPrices();
    }

    public void loadPrices() {
        prices.clear();
        File folder = new File(plugin.getDataFolder(), "core/sell/prices");
        if (!folder.exists()) folder.mkdirs();

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String category = file.getName().replace(".yml", "");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            if (config.contains("prices")) {
                for (String key : config.getConfigurationSection("prices").getKeys(false)) {
                    double price = config.getDouble("prices." + key);
                    prices.put(key.toUpperCase(), new PriceModel(key, price, category));
                }
            }
        }
    }

    public PriceModel getPrice(ItemStack item) {
        if (item == null) return null;
        String type = item.getType().name();
        return prices.get(type);
    }

    public Map<String, PriceModel> getAllPrices() {
        return Collections.unmodifiableMap(prices);
    }
}
