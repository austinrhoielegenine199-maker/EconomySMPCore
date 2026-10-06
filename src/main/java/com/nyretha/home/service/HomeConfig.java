package com.nyretha.home.service;

import com.nyretha.NyrethaCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeConfig {

    private final NyrethaCore plugin;
    private File file;
    private FileConfiguration config;

    public HomeConfig(NyrethaCore plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        file = new File(plugin.getDataFolder(), "core/home/homes.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create homes.yml!");
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveHome(UUID uuid, String name, Location loc) {
        String path = "homes." + uuid.toString() + "." + name.toLowerCase();
        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
        save();
    }

    public void removeHome(UUID uuid, String name) {
        config.set("homes." + uuid.toString() + "." + name.toLowerCase(), null);
        save();
    }

    public Map<String, Location> getHomes(UUID uuid) {
        Map<String, Location> homes = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("homes." + uuid.toString());
        if (section != null) {
            for (String key : section.getKeys(false)) {
                World world = Bukkit.getWorld(section.getString(key + ".world", "world"));
                double x = section.getDouble(key + ".x");
                double y = section.getDouble(key + ".y");
                double z = section.getDouble(key + ".z");
                float yaw = (float) section.getDouble(key + ".yaw");
                float pitch = (float) section.getDouble(key + ".pitch");

                if (world != null) {
                    homes.put(key, new Location(world, x, y, z, yaw, pitch));
                }
            }
        }
        return homes;
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save homes.yml!");
        }
    }
}
