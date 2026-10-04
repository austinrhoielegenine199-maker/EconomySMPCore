package com.nyretha.shop.service;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ShopConfigService {
    private Map<String, Object> mainShopConfig = new HashMap<>();
    private final Map<String, Map<String, Object>> categoryConfigs = new HashMap<>();

    @SuppressWarnings("unchecked")
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

        // 2. Dynamically load category files based on the 'file' property inside shopgui.yml
        Map<String, Map<String, Object>> categories = (Map<String, Map<String, Object>>) mainShopConfig.get("categories");
        if (categories != null) {
            for (Map.Entry<String, Map<String, Object>> entry : categories.entrySet()) {
                String categoryKey = entry.getKey().toLowerCase();
                Map<String, Object> catData = entry.getValue();
                
                String fileName = (String) catData.get("file");
                if (fileName != null) {
                    try (InputStream in = getClass().getClassLoader().getResourceAsStream("shop/categories/" + fileName)) {
                        if (in != null) {
                            Map<String, Object> data = yaml.load(in);
                            if (data != null) {
                                categoryConfigs.put(categoryKey, data);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Map<String, Object> getMainShopConfig() {
        return mainShopConfig;
    }

    public Map<String, Object> getCategoryConfig(String categoryName) {
        return categoryConfigs.get(categoryName.toLowerCase());
    }
}
