package com.nyretha.utils;

import org.bukkit.entity.Player;

public class PlayerUtil {

    public static String getShortName(String name, int maxLength) {
        if (name == null) return "";
        if (name.length() <= maxLength) {
            return name;
        }
        return name.substring(0, maxLength);
    }

    public static String getShortName(Player player, int maxLength) {
        return getShortName(player.getName(), maxLength);
    }
}
