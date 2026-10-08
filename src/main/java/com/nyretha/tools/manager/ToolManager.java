package com.nyretha.tools.manager;

import com.nyretha.Core;
import com.nyretha.tools.model.CustomTool;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolManager {

    private final Core plugin;
    private final Map<String, CustomTool> tools = new HashMap<>();

    public ToolManager(Core plugin) {
        this.plugin = plugin;
        loadTools();
    }

    public void loadTools() {
        tools.clear();
        File file = new File(plugin.getDataFolder(), "core/tools/gui.yml");
        if (!file.exists()) {
            plugin.saveResource("core/tools/gui.yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("Tools");

        if (section != null) {
            for (String key : section.getKeys(false)) {
                String matStr = section.getString(key + ".material", "STONE");
                Material material = Material.matchMaterial(matStr);
                if (material == null) material = Material.STONE;

                String displayName = section.getString(key + ".displayname", key);
                int slot = section.getInt(key + ".slot", 0);
                int page = section.getInt(key + ".page", 1);
                List<String> lore = section.getStringList(key + ".lore");

                tools.put(key.toLowerCase(), new CustomTool(key, material, displayName, slot, page, lore));
            }
        }
    }

    public CustomTool getTool(String name) {
        return tools.get(name.toLowerCase());
    }

    public Map<String, CustomTool> getTools() {
        return tools;
    }
}
