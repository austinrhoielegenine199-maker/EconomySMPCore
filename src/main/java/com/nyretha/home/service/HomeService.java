package com.nyretha.home.service;

import com.nyretha.home.model.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HomeService {

    private final JavaPlugin plugin;
    private final Map<UUID, Map<Integer, Home>> playerHomes = new ConcurrentHashMap<>();
    private String databaseUrl;

    public HomeService(JavaPlugin plugin) {
        this.plugin = plugin;
        initDatabase();
    }

    private void initDatabase() {
        File folder = new File(plugin.getDataFolder(), "core/home");
        if (!folder.exists()) folder.mkdirs();

        File dbFile = new File(folder, "database.db");
        this.databaseUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        try (Connection conn = DriverManager.getConnection(databaseUrl);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS player_homes (" +
                    "uuid VARCHAR(36), " +
                    "home_id INT, " +
                    "name VARCHAR(32), " +
                    "world VARCHAR(64), " +
                    "x DOUBLE, y DOUBLE, z DOUBLE, " +
                    "yaw FLOAT, pitch FLOAT, " +
                    "PRIMARY KEY (uuid, home_id));");

        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to initialize home database.db: " + e.getMessage());
        }

        loadAllHomesAsync();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }

    public void loadAllHomesAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM player_homes;");
                 ResultSet rs = stmt.executeQuery()) {

                playerHomes.clear();
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    int homeId = rs.getInt("home_id");
                    String name = rs.getString("name");
                    String world = rs.getString("world");
                    double x = rs.getDouble("x");
                    double y = rs.getDouble("y");
                    double z = rs.getDouble("z");
                    float yaw = rs.getFloat("yaw");
                    float pitch = rs.getFloat("pitch");

                    if (Bukkit.getWorld(world) != null) {
                        Location loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                        Home home = new Home(uuid, homeId, name, loc);
                        playerHomes.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(homeId, home);
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to load homes from database.db: " + e.getMessage());
            }
        });
    }

    public boolean hasHome(Player player, int id) {
        Map<Integer, Home> homes = playerHomes.get(player.getUniqueId());
        return homes != null && homes.containsKey(id);
    }

    public Home getHome(Player player, int id) {
        Map<Integer, Home> homes = playerHomes.get(player.getUniqueId());
        return homes != null ? homes.get(id) : null;
    }

    public Map<Integer, Home> getPlayerHomes(Player player) {
        return playerHomes.getOrDefault(player.getUniqueId(), Map.of());
    }

    public void setHome(Player player, int id, String name, Location loc) {
        Home home = new Home(player.getUniqueId(), id, name, loc);
        playerHomes.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>()).put(id, home);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT INTO player_homes(uuid, home_id, name, world, x, y, z, yaw, pitch) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT(uuid, home_id) DO UPDATE SET name=excluded.name, world=excluded.world, " +
                    "x=excluded.x, y=excluded.y, z=excluded.z, yaw=excluded.yaw, pitch=excluded.pitch;";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, player.getUniqueId().toString());
                stmt.setInt(2, id);
                stmt.setString(3, name);
                stmt.setString(4, loc.getWorld().getName());
                stmt.setDouble(5, loc.getX());
                stmt.setDouble(6, loc.getY());
                stmt.setDouble(7, loc.getZ());
                stmt.setFloat(8, loc.getYaw());
                stmt.setFloat(9, loc.getPitch());
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save home for " + player.getName() + ": " + e.getMessage());
            }
        });
    }

    public void deleteHome(Player player, int id) {
        Map<Integer, Home> homes = playerHomes.get(player.getUniqueId());
        if (homes != null) {
            homes.remove(id);
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM player_homes WHERE uuid=? AND home_id=?;")) {
                stmt.setString(1, player.getUniqueId().toString());
                stmt.setInt(2, id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to delete home for " + player.getName() + ": " + e.getMessage());
            }
        });
    }

    public int getMaxHomes(Player player) {
        for (int i = 10; i >= 1; i--) {
            if (player.hasPermission("home.limit." + i)) {
                return i;
            }
        }
        return 3;
    }
}
