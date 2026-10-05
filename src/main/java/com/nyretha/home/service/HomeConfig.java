package com.nyretha.home.service;

import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class HomeConfig {

    private final JavaPlugin plugin;
    private Map<String, Object> configData;

    public HomeConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        Yaml yaml = new Yaml();

        try (
                InputStream inputStream =
                        plugin.getResource(
                                "core/home/homegui.yml"
                        )
        ) {
            if (inputStream != null) {
                configData = yaml.load(inputStream);
            } else {
                plugin.getLogger().warning(
                        "Could not find core/home/homegui.yml!"
                );
            }
        } catch (Exception e) {
            plugin.getLogger().severe(
                    "Failed to load homegui.yml: " +
                    e.getMessage()
            );
        }
    }

    @SuppressWarnings("unchecked")
    public int getTeleportCountdown() {
        if (configData == null) {
            return 5;
        }

        try {
            Map<String, Object> teleport =
                    (Map<String, Object>)
                            configData.get("teleport");

            Object seconds =
                    teleport.get("countdown-seconds");

            if (seconds instanceof Number) {
                return ((Number) seconds).intValue();
            }

        } catch (Exception ignored) {
        }

        return 5;
    }

    @SuppressWarnings("unchecked")
    public String getMessage(String key) {
        if (configData == null) {
            return "";
        }

        try {
            Map<String, Object> teleport =
                    (Map<String, Object>)
                            configData.get("teleport");

            Object message =
                    teleport.get(key);

            if (message == null) {
                return "";
            }

            return String.valueOf(message);

        } catch (Exception ignored) {
            return "";
        }
    }
}
