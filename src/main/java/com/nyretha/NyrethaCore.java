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

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Since your resources root contains the "core" folder, 
        // saveResource looks inside it using these precise paths:
        saveConfigSafely("combat/combat.yml");
        saveConfigSafely("eco/economy.yml");
        saveConfigSafely("help/help.yml");
        saveConfigSafely("home/homegui.yml");
        saveConfigSafely("moderation/staffmoderation.yml");
        saveConfigSafely("ping/ping.yml");
        saveConfigSafely("tp/tpasystem.yml");
        saveConfigSafely("shop/shopgui.yml");
        saveConfigSafely("shop/categories/gear.yml");
        saveConfigSafely("shop/categories/end.yml");
        saveConfigSafely("shop/categories/nether.yml");
        saveConfigSafely("shop/categories/food.yml");
        saveConfigSafely("shop/categories/flakeshop.yml");

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

    private void saveConfigSafely(String resourcePath) {
        File file = new File(getDataFolder(), resourcePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try {
                // Notice we prefix with "core/" here because the physical folder in your jar is "core/"
                saveResource("core/" + resourcePath, false);
                getLogger().info("Successfully extracted: " + resourcePath);
            } catch (IllegalArgumentException e) {
                getLogger().warning("FAILED to find resource in jar: core/" + resourcePath);
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
