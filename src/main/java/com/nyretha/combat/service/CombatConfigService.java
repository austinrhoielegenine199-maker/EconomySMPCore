package com.nyretha.combat.service;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombatConfigService {
    private Map<String, Object> configData = new HashMap<>();

    public void loadConfig() {
        Yaml yaml = new Yaml();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("combat/combat.yml")) {
            if (in != null) {
                configData = yaml.load(in);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public String getMessage(String key, String defaultMsg) {
        Map<String, Object> messages = (Map<String, Object>) configData.get("messages");
        if (messages != null && messages.containsKey(key)) {
            return (String) messages.get(key);
        }
        return defaultMsg;
    }

    @SuppressWarnings("unchecked")
    public int getIntSetting(String key, int defaultValue) {
        Map<String, Object> settings = (Map<String, Object>) configData.get("settings");
        if (settings != null && settings.containsKey(key)) {
            return (int) settings.get(key);
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    public List<String> getBlacklistedCommands() {
        List<String> list = (List<String>) configData.get("blacklisted_commands");
        if (list != null) {
            // Convert everything to lowercase for safe checking
            List<String> lowerList = new ArrayList<>();
            for (String cmd : list) {
                lowerList.add(cmd.toLowerCase());
            }
            return lowerList;
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public boolean getOtherSetting(String key, boolean defaultValue) {
        Map<String, Object> others = (Map<String, Object>) configData.get("others");
        if (others != null && others.containsKey(key)) {
            return (boolean) others.get(key);
        }
        return defaultValue;
    }
}
