package com.nyretha.shop.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundManager {

    public static void playSound(Player player, String soundName) {
        if (soundName == null || soundName.equalsIgnoreCase("NONE")) return;
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase().replace(".", "_"));
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException ignored) {}
    }
}
