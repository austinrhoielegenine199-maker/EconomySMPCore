package com.nyretha.help;

import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

public final class HelpConfig {

    private final JavaPlugin plugin;
    private Map<String, Object> configData = Collections.emptyMap();

    public HelpConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        File file = new File(
                plugin.getDataFolder(),
                "core/help/help.yml"
        );

        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();

            File parent = file.getParentFile();

            if (parent != null) {
                parent.mkdirs();
            }

            try {
                plugin.saveResource(
                        "core/help/help.yml",
                        false
                );
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning(
                        "Could not find embedded resource: core/help/help.yml"
                );
                return;
            }
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            Yaml yaml = new Yaml();

            Map<String, Object> loaded =
                    yaml.load(inputStream);

            if (loaded != null) {
                configData = loaded;
            } else {
                configData = Collections.emptyMap();
            }

        } catch (Exception e) {
            plugin.getLogger().severe(
                    "Failed to load core/help/help.yml: " +
                    e.getMessage()
            );
        }
    }

    public void reload() {
        load();
    }

    public Map<String, Object> getConfig() {
        return configData;
    }

    public Map<String, Object> getConfigData() {
        return configData;
    }
}
