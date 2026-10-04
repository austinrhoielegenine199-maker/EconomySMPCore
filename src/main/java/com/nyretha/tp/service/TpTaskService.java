package com.nyretha.tp.service;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TpTaskService {

    public static void startTeleportCountdown(Player traveler, Location destination, Plugin plugin, TpConfigService configService) {
        int totalSeconds = configService.getIntSetting("countdown_seconds", 5);
        String actionbarMsgTemplate = configService.getMessage("teleporting_actionbar", "&7Teleporting in &b%time%s");
        String tickSoundName = configService.getStringSetting("ticking_sound", "BLOCK_NOTE_BLOCK_PLING");
        String endSoundName = configService.getStringSetting("completion_sound", "ENTITY_ENDER_PEARL_THROW");

        Location startLocation = traveler.getLocation().clone();

        new BukkitRunnable() {
            int countdown = totalSeconds;

            @Override
            public void run() {
                if (!traveler.isOnline()) {
                    cancel();
                    return;
                }

                // Verify movement or damage cancellation check
                Location currentLoc = traveler.getLocation();
                if (currentLoc.getWorld() != startLocation.getWorld() || currentLoc.distanceSquared(startLocation) > 0.25) {
                    traveler.sendMessage(ChatColor.translateAlternateColorCodes('&', configService.getMessage("teleport_cancelled", "&cTeleport cancelled because you moved!")));
                    cancel();
                    return;
                }

                if (countdown > 0) {
                    String msg = actionbarMsgTemplate.replace("%time%", String.valueOf(countdown));
                    traveler.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', msg)));

                    try {
                        Sound sound = Sound.valueOf(tickSoundName);
                        traveler.playSound(traveler.getLocation(), sound, 0.5f, 1.0f);
                    } catch (Exception ignored) {}

                    countdown--;
                } else {
                    traveler.teleport(destination);
                    try {
                        Sound sound = Sound.valueOf(endSoundName);
                        traveler.playSound(destination, sound, 1.0f, 1.0f);
                    } catch (Exception ignored) {}

                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
