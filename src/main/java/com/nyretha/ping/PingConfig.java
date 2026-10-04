package com.nyretha.ping;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class PingConfig {

    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration config;

    public PingConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        file = new File(plugin.getDataFolder(), "ping.yml");

        if (!file.exists()) {
            plugin.saveResource("ping.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().severe("Could not save ping.yml!");
            exception.printStackTrace();
        }
    }

    public void reload() {
        load();
    }
}
