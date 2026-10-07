package com.nyretha.flakes.service;

import com.nyretha.flakes.model.FlakeBooster;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FlakesService {

    private final JavaPlugin plugin;
    private final Map<UUID, Integer> flakeBalances = new ConcurrentHashMap<>();
    private final Map<UUID, FlakeBooster> activeBoosters = new ConcurrentHashMap<>();
    private String databaseUrl;

    public FlakesService(JavaPlugin plugin) {
        this.plugin = plugin;
        initDatabase();
        startAfkTask();
    }

    private void initDatabase() {
        File folder = new File(plugin.getDataFolder(), "core/flakes");
        if (!folder.exists()) folder.mkdirs();

        File dbFile = new File(folder, "database.db");
        this.databaseUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        try (Connection conn = DriverManager.getConnection(databaseUrl);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS player_flakes (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "flakes INT NOT NULL DEFAULT 0" +
                    ");");

        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to initialize flakes database.db: " + e.getMessage());
        }

        loadAllDataAsync();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }

    public void loadAllDataAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM player_flakes;")) {

                flakeBalances.clear();
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    int flakes = rs.getInt("flakes");
                    flakeBalances.put(uuid, flakes);
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Error loading flakes from database.db: " + e.getMessage());
            }
        });
    }

    public int getFlakes(UUID uuid) {
        return flakeBalances.getOrDefault(uuid, 0);
    }

    public void addFlakes(UUID uuid, int amount) {
        int current = getFlakes(uuid);
        double multiplier = activeBoosters.containsKey(uuid) && !activeBoosters.get(uuid).isExpired() 
                ? activeBoosters.get(uuid).getMultiplier() : 1.0;
        
        int finalAmount = current + (int)(amount * multiplier);
        flakeBalances.put(uuid, finalAmount);
        savePlayerFlakesAsync(uuid, finalAmount);
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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT INTO player_flakes(uuid, flakes) VALUES(?, ?) " +
                    "ON CONFLICT(uuid) DO UPDATE SET flakes = excluded.flakes;";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, uuid.toString());
                stmt.setInt(2, amount);
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save flakes for " + uuid + ": " + e.getMessage());
            }
        });
    }

    public void applyBooster(UUID uuid, double multiplier, long durationHours) {
        activeBoosters.put(uuid, new FlakeBooster(multiplier, durationHours));
    }

    private void startAfkTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                int amount = player.hasPermission("flakes.vip") ? 2 : 1;
                addFlakes(player.getUniqueId(), amount);
            }
        }, 1200L, 1200L); // Every 60 seconds
    }
}
