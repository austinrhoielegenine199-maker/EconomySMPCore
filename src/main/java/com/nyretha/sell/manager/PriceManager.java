package com.nyretha.sell.manager;

import com.nyretha.Core;
import com.nyretha.sell.model.PriceModel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PriceManager {

    private Core plugin;
    private final Map<String, PriceModel> prices = new ConcurrentHashMap<>();

    public PriceManager() {
        this.plugin = Core.getInstance();
        loadPrices();
    }

    public PriceManager(Core plugin) {
        this.plugin = plugin;
        loadPrices();
    }

    public PriceManager(JavaPlugin plugin) {
        if (plugin instanceof Core) {
            this.plugin = (Core) plugin;
        } else {
            this.plugin = Core.getInstance();
        }
        loadPrices();
    }

    public PriceManager(WorthManager worthManager) {
        this.plugin = Core.getInstance();
        loadPrices();
    }

    public PriceManager(PriceManager priceManager) {
        this.plugin = Core.getInstance();
        loadPrices();
    }

    public void loadPrices() {
        prices.clear();
        if (plugin == null) {
            plugin = Core.getInstance();
        }
        if (plugin == null) return;

        File file = new File(plugin.getDataFolder(), "core/sell/prices.yml");
        if (!file.exists()) {
            plugin.saveResource("core/sell/prices.yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (config.contains("prices")) {
            for (String key : config.getConfigurationSection("prices").getKeys(false)) {
                double price = config.getDouble("prices." + key);
                prices.put(key.toUpperCase(), new PriceModel(key, price, "general"));
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
