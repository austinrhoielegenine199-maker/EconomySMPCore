package com.nyretha;

import com.nyretha.core.database.DatabaseService;
import org.bukkit.plugin.java.JavaPlugin;

public final class NyrethaCore extends JavaPlugin {

    private static NyrethaCore instance;
    private DatabaseService databaseService;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize plugin folder if it doesn't exist
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Initialize SQLite Database by passing 'this' (the plugin instance)
        databaseService = new DatabaseService(this);

        getLogger().info("NyrethaCore has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        // Close database connections safely if needed
        if (databaseService != null && databaseService.getConnection() != null) {
            // handle closing connection if your DatabaseService has a close method
        }
        getLogger().info("NyrethaCore has been disabled.");
    }

    public static NyrethaCore getInstance() {
        return instance;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }
}
