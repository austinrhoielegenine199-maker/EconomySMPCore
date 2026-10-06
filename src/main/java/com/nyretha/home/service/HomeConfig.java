package com.nyretha.home.service;

import com.nyretha.NyrethaCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeConfig {

    private final NyrethaCore plugin;
    private File homesFile;
    private FileConfiguration homesConfig;
    private File guiFile;
    private FileConfiguration guiConfig;

    public HomeConfig(NyrethaCore plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        homesFile = new File(plugin.getDataFolder(), "core/home/homes.yml");
        if (!homesFile.exists()) {
            try {
                homesFile.getParentFile().mkdirs();
                homesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create homes.yml!");
            }
        }
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);

        guiFile = new File(plugin.getDataFolder(), "core/home/homegui.yml");
        if (!guiFile.exists()) {
            plugin.saveResource("core/home/homegui.yml", false);
        }
        guiConfig = YamlConfiguration.loadConfiguration(guiFile);
    }

    public FileConfiguration getGuiConfig() {
        return guiConfig;
    }

    public void saveHome(UUID uuid, String name, Location loc) {
        String path = "homes." + uuid.toString() + "." + name.toLowerCase();
        homesConfig.set(path + ".name", name);
        homesConfig.set(path + ".world", loc.getWorld().getName());
        homesConfig.set(path + ".x", loc.getX());
        homesConfig.set(path + ".y", loc.getY());
        homesConfig.set(path + ".z", loc.getZ());
        homesConfig.set(path + ".yaw", loc.getYaw());
        homesConfig.set(path + ".pitch", loc.getPitch());
        save();
    }

    public void removeHome(UUID uuid, String name) {
        homesConfig.set("homes." + uuid.toString() + "." + name.toLowerCase(), null);
        save();
    }

    public Map<String, Location> getHomes(UUID uuid) {
        Map<String, Location> homes = new HashMap<>();
        if (homesConfig.contains("homes." + uuid.toString())) {
            var section = homesConfig.getConfigurationSection("homes." + uuid.toString());
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    World world = Bukkit.getWorld(section.getString(key + ".world", "world"));
                    double x = section.getDouble(key + ".x");
                    double y = section.getDouble(key + ".y");
                    double z = section.getDouble(key + ".z");
                    float yaw = (float) section.getDouble(key + ".yaw");
                    float pitch = (float) section.getDouble(key + ".pitch");

                    if (world != null) {
                        homes.put(key.toLowerCase(), new Location(world, x, y, z, yaw, pitch));
                    }
                }
            }
        }
        return homes;
    }

    private void save() {
        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save homes.yml!");
        }
    }
}
