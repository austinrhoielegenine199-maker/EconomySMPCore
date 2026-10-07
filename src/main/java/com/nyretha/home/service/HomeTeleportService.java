package com.nyretha.home.service;

import com.nyretha.home.model.Home;
import com.nyretha.shop.utils.HexColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HomeTeleportService {

    private final JavaPlugin plugin;
    private final Map<UUID, BukkitTask> pendingTeleports = new ConcurrentHashMap<>();

    public HomeTeleportService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void teleportToHome(Player player, Home home, int delaySeconds) {
        if (player.hasPermission("home.bypass") || delaySeconds <= 0) {
            player.teleport(home.getLocation());
            player.sendMessage(HexColor.format("&aYou have been teleported to home " + home.getName() + "!"));
            return;
        }

        if (pendingTeleports.containsKey(player.getUniqueId())) {
            player.sendMessage(HexColor.format("&cYou are already teleporting!"));
            return;
        }

        Location startLoc = player.getLocation().clone();

        BukkitTask task = new BukkitRunnable() {
            int countdown = delaySeconds;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancelTeleport(player.getUniqueId());
                    return;
                }

                if (player.getLocation().distanceSquared(startLoc) > 0.1) {
                    player.sendMessage(HexColor.format("&cTeleport cancelled because you moved!"));
                    cancelTeleport(player.getUniqueId());
                    return;
                }

                if (countdown <= 0) {
                    player.teleport(home.getLocation());
                    player.sendMessage(HexColor.format("&aYou have been teleported to home " + home.getName() + "!"));
                    cancelTeleport(player.getUniqueId());
                    return;
                }

                player.sendMessage(HexColor.format("&fTeleporting in &b" + countdown + " &fseconds..."));
                countdown--;
            }
        }.runTaskTimer(plugin, 0L, 20L);

        pendingTeleports.put(player.getUniqueId(), task);
    }

    public void cancelTeleport(UUID uuid) {
        BukkitTask task = pendingTeleports.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }
}
