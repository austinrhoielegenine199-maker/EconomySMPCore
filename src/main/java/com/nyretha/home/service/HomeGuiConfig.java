package com.nyretha.home.service;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;

public class HomeGuiConfig {
    private Map<String, Object> configData;

    public void loadConfig() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("core/home/homegui.yml")) {
            if (inputStream != null) {
                configData = yaml.load(inputStream);
            } else {
                System.err.println("[HomeGuiConfig] Could not find homegui.yml in resources!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public String getGuiTitle(String path) {
        if (configData == null) return "Homes";
        try {
            Map<String, Object> section = (Map<String, Object>) configData.get(path);
            if (section != null && section.containsKey("title")) {
                return (String) section.get("title");
            }
        } catch (Exception e) {
            // Fallback
        }
        return "Homes";
    }

    @SuppressWarnings("unchecked")
    public int getGuiSize(String path) {
        if (configData == null) return 27;
        try {
            Map<String, Object> section = (Map<String, Object>) configData.get(path);
            if (section != null && section.containsKey("size")) {
                Object size = section.get("size");
                if (size instanceof Number) {
                    return ((Number) size).intValue();
                }
            }
        } catch (Exception e) {
            // Fallback
        }
        return 27;
    }
}
