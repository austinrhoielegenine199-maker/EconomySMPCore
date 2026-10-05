package com.nyretha;

import com.nyretha.core.database.DatabaseService;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class NyrethaCore extends JavaPlugin {

    private static NyrethaCore instance;
    private DatabaseService databaseService;

    @Override
    public void onEnable() {
        instance = this;

        // 1. Ensure the main plugin data folder exists
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // 2. Automatically save all configuration files from the core resource folders
        saveConfigSafely("core/combat/combat.yml");
        saveConfigSafely("core/eco/economy.yml");
        saveConfigSafely("core/help/help.yml");
        saveConfigSafely("core/home/homegui.yml");
        saveConfigSafely("core/moderation/staffmoderation.yml");
        saveConfigSafely("core/ping/ping.yml");
        saveConfigSafely("core/tp/tpasystem.yml");

        // Shop main configs
        saveConfigSafely("core/shop/shopgui.yml");

        // Shop categories folder files
        saveConfigSafely("core/shop/categories/gear.yml");
        saveConfigSafely("core/shop/categories/end.yml");
        saveConfigSafely("core/shop/categories/nether.yml");
        saveConfigSafely("core/shop/categories/food.yml");
        saveConfigSafely("core/shop/categories/flakeshop.yml");

        // 3. Initialize SQLite Database
        databaseService = new DatabaseService(this);

        getLogger().info("NyrethaCore has been enabled successfully with all configs!");
    }

    @Override
    public void onDisable() {
        if (databaseService != null && databaseService.getConnection() != null) {
            // handle closing connection if needed
        }
        getLogger().info("NyrethaCore has been disabled.");
    }

    /**
     * Safely saves a resource file, creating parent directories automatically on the server.
     */
    private void saveConfigSafely(String resourcePath) {
        File file = new File(getDataFolder(), resourcePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try {
                saveResource(resourcePath, false);
            } catch (IllegalArgumentException e) {
                // Skips quietly if the file is missing from resources
            }
        }
    }

    public static NyrethaCore getInstance() {
        return instance;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }
}
