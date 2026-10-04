package com.nyretha.help;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class HelpConfig {

    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration config;

    public HelpConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        File folder = new File(plugin.getDataFolder(), "help");

        if (!folder.exists()) {
            folder.mkdirs();
        }

        file = new File(folder, "help.yml");

        if (!file.exists()) {
            plugin.saveResource("help/help.yml", false);
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
            plugin.getLogger().severe("Could not save help/help.yml!");
            exception.printStackTrace();
        }
    }

    public void reload() {
        load();
    }
}
