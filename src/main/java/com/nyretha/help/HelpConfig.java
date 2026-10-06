package com.nyretha.help;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class HelpConfig {

    private final JavaPlugin plugin;
    private FileConfiguration config;

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
