package com.nyretha.tpa.service;

import com.nyretha.tpa.model.TPARequest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TPAService {

    private final JavaPlugin plugin;
    private final Map<UUID, TPARequest> pendingRequests = new ConcurrentHashMap<>();
    private final Set<UUID> disabledTpa = ConcurrentHashMap.newKeySet();

    public TPAService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void sendRequest(Player sender, Player target, boolean isHere) {
        TPARequest request = new TPARequest(sender, target, isHere);
        pendingRequests.put(target.getUniqueId(), request);

        // Expire after 30 seconds
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pendingRequests.get(target.getUniqueId()) == request) {
                pendingRequests.remove(target.getUniqueId());
            }
        }, 600L);
    }

    public TPARequest getRequest(UUID targetUUID) {
        return pendingRequests.get(targetUUID);
    }

    public void removeRequest(UUID targetUUID) {
        pendingRequests.remove(targetUUID);
    }

    public boolean isTpaDisabled(Player player) {
        return disabledTpa.contains(player.getUniqueId());
    }

    public void toggleTpa(Player player) {
        if (disabledTpa.contains(player.getUniqueId())) {
            disabledTpa.remove(player.getUniqueId());
        } else {
            disabledTpa.add(player.getUniqueId());
        }
    }
}
