package com.nyretha.ping;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class PingConfig {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public PingConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        File file = new File(
                plugin.getDataFolder(),
                "core/ping/ping.yml"
        );

        if (!file.exists()) {
            File parent = file.getParentFile();

            if (parent != null) {
                parent.mkdirs();
            }

            try {
                plugin.saveResource(
                        "core/ping/ping.yml",
                        false
                );
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning(
                        "Could not find embedded resource: core/ping/ping.yml"
                );
            }
        }

        if (file.exists()) {
            config = YamlConfiguration.loadConfiguration(file);
        } else {
            config = new YamlConfiguration();
        }
    }

    public void reload() {
        load();
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
