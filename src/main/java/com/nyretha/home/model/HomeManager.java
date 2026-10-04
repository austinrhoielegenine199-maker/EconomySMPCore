package com.nyretha.home.model;

import org.bukkit.Location;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class HomeManager {
    // Map<PlayerUUID, Map<HomeName, Location>>
    private final Map<UUID, Map<String, Location>> homes = new HashMap<>();

    public void setHome(UUID uuid, String name, Location location) {
        homes.computeIfAbsent(uuid, k -> new HashMap<>()).put(name.toLowerCase(), location);
    }

    public Location getHome(UUID uuid, String name) {
        Map<String, Location> playerHomes = homes.get(uuid);
        if (playerHomes == null) return null;
        return playerHomes.get(name.toLowerCase());
    }

    public void removeHome(UUID uuid, String name) {
        Map<String, Location> playerHomes = homes.get(uuid);
        if (playerHomes != null) {
            playerHomes.remove(name.toLowerCase());
        }
    }

    public Set<String> getHomeNames(UUID uuid) {
        Map<String, Location> playerHomes = homes.get(uuid);
        if (playerHomes == null) return Set.of();
        return playerHomes.keySet();
    }
}
