package com.nyretha.team.service;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;

public class TeamGuiConfigService {
    private Map<String, Object> configData;

    public void loadConfig() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("team/teamgui.yml")) {
            if (inputStream != null) {
                configData = yaml.load(inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getGuiTitle(int page) {
        if (configData == null) return "ᴛᴇᴀᴍ (Page " + page + ")";
        try {
            String titleFormat = (String) configData.getOrDefault("title", "ᴛᴇᴀᴍ (Page %page%)");
            return titleFormat.replace("%page%", String.valueOf(page));
        } catch (Exception e) {
            return "ᴛᴇᴀᴍ";
        }
    }

    public int getGuiSize() {
        if (configData == null) return 54;
        try {
            Object size = configData.get("size");
            if (size instanceof Number) {
                return ((Number) size).intValue();
            }
        } catch (Exception ignored) {}
        return 54;
    }

    @SuppressWarnings("unchecked")
    public String getMessage(String messageKey) {
        if (configData == null) return "";
        try {
            Map<String, Object> messages = (Map<String, Object>) configData.get("messages");
            if (messages != null) {
                return (String) messages.getOrDefault(messageKey, "");
            }
        } catch (Exception ignored) {}
        return "";
    }
}
