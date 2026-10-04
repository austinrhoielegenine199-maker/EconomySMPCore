package com.nyretha.shop.service;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ShopConfigService {
    private Map<String, Object> mainShopConfig = new HashMap<>();
    private final Map<String, Map<String, Object>> categoryConfigs = new HashMap<>();

    public void loadConfigs() {
        Yaml yaml = new Yaml();
        
        // 1. Load Main Shop Configuration
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("shop/shopgui.yml")) {
            if (in != null) {
                mainShopConfig = yaml.load(in);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Load Category Configurations (gear, flake, flakeshop, end, nether, food)
        String[] categories = {"gear", "flake", "flakeshop", "end", "nether", "food"};
        for (String cat : categories) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("shop/categories/" + cat + ".yml")) {
                if (in != null) {
                    Map<String, Object> data = yaml.load(in);
                    if (data != null) {
                        categoryConfigs.put(cat, data);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, Object> getMainShopConfig() {
        return mainShopConfig;
    }

    public Map<String, Object> getCategoryConfig(String categoryName) {
        Map<String, Object> config = categoryConfigs.get(categoryName.toLowerCase());
        if (config == null && categoryName.equalsIgnoreCase("flake")) {
            config = categoryConfigs.get("flakeshop");
        }
        return config;
    }
}
