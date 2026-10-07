package com.nyretha.pay.service;

import com.nyretha.pay.model.PayTransaction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PayService {

    private final JavaPlugin plugin;
    private final Map<UUID, List<PayTransaction>> transactionLogs = new ConcurrentHashMap<>();
    private final Set<UUID> toggledPay = ConcurrentHashMap.newKeySet();
    private String databaseUrl;

    public PayService(JavaPlugin plugin) {
        this.plugin = plugin;
        initDatabase();
    }

    private void initDatabase() {
        File folder = new File(plugin.getDataFolder(), "core/pay");
        if (!folder.exists()) folder.mkdirs();

        File dbFile = new File(folder, "database.db");
        this.databaseUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        try (Connection conn = DriverManager.getConnection(databaseUrl);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS pay_transactions (" +
                    "id VARCHAR(36) PRIMARY KEY, " +
                    "sender VARCHAR(16), " +
                    "receiver VARCHAR(16), " +
                    "amount DOUBLE, " +
                    "date VARCHAR(32));");

            stmt.execute("CREATE TABLE IF NOT EXISTS pay_toggles (" +
                    "uuid VARCHAR(36) PRIMARY KEY);");

        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to initialize pay database.db: " + e.getMessage());
        }

        loadDataAsync();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }

    public void loadDataAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = getConnection()) {
                // Load Toggles
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT uuid FROM pay_toggles;")) {
                    toggledPay.clear();
                    while (rs.next()) {
                        toggledPay.add(UUID.fromString(rs.getString("uuid")));
                    }
                }

                // Load Transaction History
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM pay_transactions;")) {
                    transactionLogs.clear();
                    while (rs.next()) {
                        PayTransaction tx = new PayTransaction(
                                rs.getString("id"),
                                rs.getString("sender"),
                                rs.getString("receiver"),
                                rs.getDouble("amount"),
                                rs.getString("date")
                        );
                        Player senderPlayer = Bukkit.getPlayerExact(tx.getSender());
                        Player receiverPlayer = Bukkit.getPlayerExact(tx.getReceiver());

                        if (senderPlayer != null) {
                            transactionLogs.computeIfAbsent(senderPlayer.getUniqueId(), k -> new ArrayList<>()).add(tx);
                        }
                        if (receiverPlayer != null) {
                            transactionLogs.computeIfAbsent(receiverPlayer.getUniqueId(), k -> new ArrayList<>()).add(tx);
                        }
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Error loading pay data from database.db: " + e.getMessage());
            }
        });
    }

    public boolean isPayDisabled(Player player) {
        return toggledPay.contains(player.getUniqueId());
    }

    public void togglePay(Player player) {
        UUID uuid = player.getUniqueId();
        boolean nowDisabled;

        if (toggledPay.contains(uuid)) {
            toggledPay.remove(uuid);
            nowDisabled = false;
        } else {
            toggledPay.add(uuid);
            nowDisabled = true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = getConnection()) {
                if (nowDisabled) {
                    try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO pay_toggles(uuid) VALUES(?);")) {
                        stmt.setString(1, uuid.toString());
                        stmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM pay_toggles WHERE uuid=?;")) {
                        stmt.setString(1, uuid.toString());
                        stmt.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to toggle pay setting for " + player.getName() + ": " + e.getMessage());
            }
        });
    }

    public void executeDirectPayment(Player sender, Player receiver, double amount) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        PayTransaction tx = new PayTransaction(id, sender.getName(), receiver.getName(), amount, date);

        transactionLogs.computeIfAbsent(sender.getUniqueId(), k -> new ArrayList<>()).add(tx);
        transactionLogs.computeIfAbsent(receiver.getUniqueId(), k -> new ArrayList<>()).add(tx);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT INTO pay_transactions(id, sender, receiver, amount, date) VALUES(?, ?, ?, ?, ?);";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, tx.getId());
                stmt.setString(2, tx.getSender());
                stmt.setString(3, tx.getReceiver());
                stmt.setDouble(4, tx.getAmount());
                stmt.setString(5, tx.getDate());
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to record pay transaction: " + e.getMessage());
            }
        });
    }

    public List<PayTransaction> getHistory(UUID uuid) {
        return transactionLogs.getOrDefault(uuid, Collections.emptyList());
    }
}
