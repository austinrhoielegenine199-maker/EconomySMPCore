package com.nyretha.tp.service;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TpConfigService {
    private Map<String, Object> configData = new HashMap<>();

    public void loadConfig() {
        Yaml yaml = new Yaml();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("tp/tpasystem.yml")) {
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
    public String getStringSetting(String key, String defaultValue) {
        Map<String, Object> settings = (Map<String, Object>) configData.get("settings");
        if (settings != null && settings.containsKey(key)) {
            return (String) settings.get(key);
        }
        return defaultValue;
    }
}
