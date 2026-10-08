package com.nyretha.shop.flakes;

import com.nyretha.Core;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FlakesManager {

    private final Core plugin;
    private final ConcurrentHashMap<UUID, Integer> flakeBalances = new ConcurrentHashMap<>();
    private File databaseFile;
    private String databaseUrl;

    public FlakesManager(Core plugin) {
        this.plugin = plugin;
        initDatabase();
    }

    private void initDatabase() {
        File dataFolder = new File(plugin.getDataFolder(), "core/shop");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        this.databaseFile = new File(dataFolder, "database.db");
        this.databaseUrl = "jdbc:sqlite:" + databaseFile.getAbsolutePath();

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            plugin.getLogger().warning("SQLite JDBC driver not found natively, relying on bundled driver.");
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String createTableSQL = "CREATE TABLE IF NOT EXISTS player_flakes (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "flakes INT NOT NULL DEFAULT 0" +
                    ");";
            stmt.execute(createTableSQL);

        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to initialize database.db for Flakes: " + e.getMessage());
        }

        loadAllDataAsync();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }

    public void loadAllDataAsync() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String query = "SELECT uuid, flakes FROM player_flakes;";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                flakeBalances.clear();
                while (rs.next()) {
                    try {
                        UUID uuid = UUID.fromString(rs.getString("uuid"));
                        int flakes = rs.getInt("flakes");
                        flakeBalances.put(uuid, flakes);
                    } catch (IllegalArgumentException ignored) {}
                }

            } catch (SQLException e) {
                plugin.getLogger().severe("Error loading player flakes from database.db: " + e.getMessage());
            }
        });
    }

    public int getFlakes(UUID uuid) {
        return flakeBalances.getOrDefault(uuid, 0);
    }

    public void addFlakes(UUID uuid, int amount) {
        int newAmount = getFlakes(uuid) + amount;
        flakeBalances.put(uuid, newAmount);
        savePlayerFlakesAsync(uuid, newAmount);
    }

    public void removeFlakes(UUID uuid, int amount) {
        int newAmount = Math.max(0, getFlakes(uuid) - amount);
        flakeBalances.put(uuid, newAmount);
        savePlayerFlakesAsync(uuid, newAmount);
    }

    public void setFlakes(UUID uuid, int amount) {
        int newAmount = Math.max(0, amount);
        flakeBalances.put(uuid, newAmount);
        savePlayerFlakesAsync(uuid, newAmount);
    }

    private void savePlayerFlakesAsync(UUID uuid, int amount) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String upsertSQL = "INSERT INTO player_flakes(uuid, flakes) VALUES(?, ?) " +
                    "ON CONFLICT(uuid) DO UPDATE SET flakes = excluded.flakes;";

            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(upsertSQL)) {

                pstmt.setString(1, uuid.toString());
                pstmt.setInt(2, amount);
                pstmt.executeUpdate();

            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save flakes for UUID " + uuid + ": " + e.getMessage());
            }
        });
    }

    public synchronized void saveSync() {
        if (flakeBalances.isEmpty()) return;

        String upsertSQL = "INSERT INTO player_flakes(uuid, flakes) VALUES(?, ?) " +
                "ON CONFLICT(uuid) DO UPDATE SET flakes = excluded.flakes;";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(upsertSQL)) {

            conn.setAutoCommit(false);
            for (var entry : flakeBalances.entrySet()) {
                pstmt.setString(1, entry.getKey().toString());
                pstmt.setInt(2, entry.getValue());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to batch save player flakes on shutdown: " + e.getMessage());
        }
    }
}
