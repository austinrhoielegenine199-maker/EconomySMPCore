package com.nyretha.economy.service;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;

public class EconomyConfigService {
    private Map<String, Object> configData;

    public void loadConfig() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("core/eco/economy.yml")) {
            if (inputStream != null) {
                configData = yaml.load(inputStream);
            } else {
                System.err.println("[EconomyConfig] Could not find economy.yml in resources!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public boolean isNumberFormattingEnabled() {
        if (configData == null) return true;
        try {
            Map<String, Object> economy = (Map<String, Object>) configData.get("economy");
            Map<String, Object> formatting = (Map<String, Object>) economy.get("number_formatting");
            return (boolean) formatting.getOrDefault("enabled", true);
        } catch (Exception e) {
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    public String getCurrencySymbol() {
        if (configData == null) return "$";
        try {
            Map<String, Object> economy = (Map<String, Object>) configData.get("economy");
            Map<String, Object> currency = (Map<String, Object>) economy.get("currency");
            return (String) currency.getOrDefault("symbol", "$");
        } catch (Exception e) {
            return "$";
        }
    }

    @SuppressWarnings("unchecked")
    public String getSound(String type) {
        if (configData == null) return "ENTITY_EXPERIENCE_ORB_PICKUP";
        try {
            Map<String, Object> economy = (Map<String, Object>) configData.get("economy");
            Map<String, Object> sounds = (Map<String, Object>) economy.get("sounds");
            return (String) sounds.getOrDefault(type, "ENTITY_EXPERIENCE_ORB_PICKUP");
        } catch (Exception e) {
            return "ENTITY_EXPERIENCE_ORB_PICKUP";
        }
    }

    @SuppressWarnings("unchecked")
    public double getStartingBalance() {
        if (configData == null) return 1000.0;
        try {
            Map<String, Object> economy = (Map<String, Object>) configData.get("economy");
            Map<String, Object> currency = (Map<String, Object>) economy.get("currency");
            Object start = currency.get("starting_balance");
            if (start instanceof Number) {
                return ((Number) start).doubleValue();
            }
        } catch (Exception e) {
            // Fallback
        }
        return 1000.0;
    }
}
