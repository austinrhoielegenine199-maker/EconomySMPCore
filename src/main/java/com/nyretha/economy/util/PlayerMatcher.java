package com.nyretha.economy.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerMatcher {

    public static Player matchPlayer(String partialName) {
        if (partialName == null || partialName.isBlank()) return null;
        
        Player exact = Bukkit.getPlayerExact(partialName);
        if (exact != null) return exact;

        Player bestMatch = null;
        String lowerPartial = partialName.toLowerCase();

        for (Player online : Bukkit.getOnlinePlayers()) {
            String name = online.getName();
            if (name.toLowerCase().startsWith(lowerPartial)) {
                return online;
            }
            if (name.toLowerCase().contains(lowerPartial)) {
                bestMatch = online;
            }
        }

        return bestMatch;
    }
}
