package com.nyretha.core.database;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseService {

    private final JavaPlugin plugin;
    private Connection connection;

    public DatabaseService(JavaPlugin plugin) {
        this.plugin = plugin;
        initDatabase();
    }

    private void initDatabase() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File dbFile = new File(dataFolder, "database.db");
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
            plugin.getLogger().info("Successfully connected to the SQLite database!");
            
            // Create default tables for player data, homes, or economy caches
            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            plugin.getLogger().severe("Failed to initialize SQLite database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTables() {
        // Example schema structure for data persistence across core features
        String playerTableQuery = "CREATE TABLE IF NOT EXISTS nyretha_players (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "name VARCHAR(16), " +
                "balance DOUBLE DEFAULT 0.0, " +
                "last_seen BIGINT)";

        String homeTableQuery = "CREATE TABLE IF NOT EXISTS nyretha_homes (" +
                "uuid VARCHAR(36), " +
                "home_name VARCHAR(32), " +
                "world VARCHAR(64), " +
                "x DOUBLE, y DOUBLE, z DOUBLE, " +
                "yaw FLOAT, pitch FLOAT, " +
                "PRIMARY KEY (uuid, home_name))";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(playerTableQuery);
            stmt.execute(homeTableQuery);
            plugin.getLogger().info("Database tables verified/created successfully.");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create default tables: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                initDatabase();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("Database connection closed safely.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
