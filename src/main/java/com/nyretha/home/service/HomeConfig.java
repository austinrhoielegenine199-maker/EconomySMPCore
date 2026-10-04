package com.nyretha.home.service;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;
import java.util.List;

public class HomeConfig {
    private Map<String, Object> configData;

    public void loadConfig() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("core/home/home.yml")) {
            if (inputStream != null) {
                configData = yaml.load(inputStream);
            } else {
                System.err.println("[HomeConfig] Could not find home.yml in resources!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public int getTeleportCountdown() {
        if (configData == null) return 5;
        try {
            Map<String, Object> teleport = (Map<String, Object>) configData.get("teleport");
            Object seconds = teleport.get("countdown-seconds");
            if (seconds instanceof Number) {
                return ((Number) seconds).intValue();
            }
        } catch (Exception e) {
            // Fallback
        }
        return 5;
    }

    @SuppressWarnings("unchecked")
    public String getMessage(String key) {
        if (configData == null) return "";
        try {
            Map<String, Object> teleport = (Map<String, Object>) configData.get("teleport");
            return (String) teleport.getOrDefault(key, "");
        } catch (Exception e) {
            return "";
        }
    }
}
