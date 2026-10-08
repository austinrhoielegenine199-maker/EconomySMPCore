package com.nyretha.sell.manager;

import com.nyretha.Core;
import com.nyretha.sell.model.PriceModel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PriceManager {

    private final Core plugin;

    private final Map<String, PriceModel> prices =
            new ConcurrentHashMap<>();

    public PriceManager(Core plugin) {
        this.plugin = plugin;
        loadPrices();
    }

    public void loadPrices() {
        prices.clear();

        File file = new File(
                plugin.getDataFolder(),
                "core/sell/prices.yml"
        );

        if (!file.exists()) {
            plugin.saveResource(
                    "core/sell/prices.yml",
                    false
            );
        }

        FileConfiguration config =
                YamlConfiguration.loadConfiguration(file);

        if (!config.contains("prices")) {
            return;
        }

        if (config.getConfigurationSection("prices") == null) {
            return;
        }

        for (String key :
                config.getConfigurationSection("prices")
                        .getKeys(false)) {

            double price =
                    config.getDouble("prices." + key);

            prices.put(
                    key.toUpperCase(),
                    new PriceModel(
                            key,
                            price,
                            "general"
                    )
            );
        }
    }

    public PriceModel getPrice(ItemStack item) {
        if (item == null) {
            return null;
        }

        return prices.get(
                item.getType().name()
        );
    }

    public Map<String, PriceModel> getAllPrices() {
        return Collections.unmodifiableMap(prices);
    }
}