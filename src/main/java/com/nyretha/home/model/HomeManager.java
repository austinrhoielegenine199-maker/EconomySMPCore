package com.nyretha.home.model;

import org.bukkit.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class HomeManager {

    // UUID -> (HomeName -> Location)
    private final Map<UUID, Map<String, Location>> userHomes = new HashMap<>();

    public void setHome(UUID uuid, String name, Location loc) {
        userHomes.computeIfAbsent(uuid, k -> new HashMap<>()).put(name.toLowerCase(), loc);
    }

    public Location getHome(UUID uuid, String name) {
        Map<String, Location> homes = userHomes.get(uuid);
        return homes != null ? homes.get(name.toLowerCase()) : null;
    }

    public boolean deleteHome(UUID uuid, String name) {
        Map<String, Location> homes = userHomes.get(uuid);
        if (homes != null && homes.containsKey(name.toLowerCase())) {
            homes.remove(name.toLowerCase());
            return true;
        }
        return false;
    }

    public boolean removeHome(UUID uuid, String name) {
        return deleteHome(uuid, name);
    }

    public Set<String> getHomeNames(UUID uuid) {
        Map<String, Location> homes = userHomes.get(uuid);
        return homes != null ? homes.keySet() : Collections.emptySet();
    }

    public Map<String, Location> getHomes(UUID uuid) {
        return userHomes.getOrDefault(uuid, new HashMap<>());
    }

    public void loadHomes(UUID uuid, Map<String, Location> homes) {
        userHomes.put(uuid, homes);
    }
}
