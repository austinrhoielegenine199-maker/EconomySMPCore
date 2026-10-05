package com.nyretha.home.service;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class HomeGuiConfigService {
    private Map<String, Object> configData;

    public void loadConfig() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("core/home/homegui.yml")) {
            if (inputStream != null) {
                configData = yaml.load(inputStream);
            } else {
                System.err.println("[HomeGuiConfigService] Could not find homegui.yml in resources!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public String getGuiTitle() {
        if (configData == null) return "ʜᴏᴍᴇѕ";
        try {
            Map<String, Object> gui = (Map<String, Object>) configData.get("gui");
            return (String) gui.getOrDefault("title", "ʜᴏᴍᴇѕ");
        } catch (Exception e) {
            return "ʜᴏᴍᴇѕ";
        }
    }

    @SuppressWarnings("unchecked")
    public int getGuiRows() {
        if (configData == null) return 4;
        try {
            Map<String, Object> gui = (Map<String, Object>) configData.get("gui");
            Object rows = gui.get("rows");
            if (rows instanceof Number) {
                return ((Number) rows).intValue();
            }
        } catch (Exception e) {
            // Fallback
        }
        return 4;
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getSlots(String key) {
        if (configData == null) return List.of();
        try {
            Map<String, Object> slots = (Map<String, Object>) configData.get("slots");
            return (List<Integer>) slots.get(key);
        } catch (Exception e) {
            return List.of();
        }
    }

    @SuppressWarnings("unchecked")
    public int getSlot(String key) {
        if (configData == null) return -1;
        try {
            Map<String, Object> slots = (Map<String, Object>) configData.get("slots");
            Object slotObj = slots.get(key);
            if (slotObj instanceof Number) {
                return ((Number) slotObj).intValue();
            }
        } catch (Exception e) {
            // Fallback
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getItemConfig(String itemName) {
        if (configData == null) return null;
        try {
            Map<String, Object> items = (Map<String, Object>) configData.get("items");
            return (Map<String, Object>) items.get(itemName);
        } catch (Exception e) {
            return null;
        }
    }
}
