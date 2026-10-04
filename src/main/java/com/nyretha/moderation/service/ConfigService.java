package com.nyretha.moderation.service;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;

public class ConfigService {
    private Map<String, Object> configData;

    public void loadConfig() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("core/moderation/staffmoderation.yml")) {
            if (inputStream != null) {
                configData = yaml.load(inputStream);
            } else {
                System.err.println("[Config] Could not find staffmoderation.yml in resources!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public String getFormattedMessage(String messageType, Map<String, String> placeholders) {
        String fallback = "§4§lYOU ARE BANNED!\n§fReason: " + placeholders.getOrDefault("{reason}", "Unknown") + "\n§fDuration: " + placeholders.getOrDefault("{duration}", "Permanent");

        if (configData == null) return fallback;
        
        try {
            Map<String, Object> moderation = (Map<String, Object>) configData.get("moderation");
            Map<String, Object> messages = (Map<String, Object>) moderation.get("disconnect_messages");
            
            String template = (String) messages.get(messageType);
            if (template == null) return fallback;

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                template = template.replace(entry.getKey(), entry.getValue());
            }
            
            return template;
        } catch (Exception e) {
            return fallback;
        }
    }

    @SuppressWarnings("unchecked")
    public String getWebhookUrl() {
        if (configData == null) return "";
        try {
            Map<String, Object> moderation = (Map<String, Object>) configData.get("moderation");
            Map<String, Object> webhooks = (Map<String, Object>) moderation.get("webhooks");
            Boolean enabled = (Boolean) webhooks.get("enabled");
            if (enabled != null && enabled) {
                return (String) webhooks.get("url");
            }
        } catch (Exception e) {
            // Ignored
        }
        return "";
    }
}
