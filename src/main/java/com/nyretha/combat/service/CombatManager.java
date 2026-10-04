package com.nyretha.combat.service;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager {
    private final CombatConfigService configService;
    private final Plugin plugin;
    private final Map<UUID, Integer> combatTimers = new HashMap<>();
    private final Map<UUID, BukkitTask> activeTasks = new HashMap<>();

    public CombatManager(CombatConfigService configService, Plugin plugin) {
        this.configService = configService;
        this.plugin = plugin;
    }

    public boolean isInCombat(UUID uuid) {
        return combatTimers.containsKey(uuid);
    }

    public void tagPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        int duration = configService.getIntSetting("combat_duration_seconds", 15);

        boolean wasInCombat = isInCombat(uuid);
        combatTimers.put(uuid, duration);

        if (!wasInCombat) {
            String enterMsg = configService.getMessage("combat_entered", "&4⚠ ʏᴏᴜ ᴀʀᴇ ɪɴ ᴄᴏᴍʙᴀᴛ! ᴅᴏ ɴᴏᴛ ʟᴇᴀᴠᴇ ᴛʜᴇ ɢᴀᴍᴇ ⚠");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', enterMsg));
            startCombatTask(player);
        }
    }

    public void untagPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        combatTimers.remove(uuid);
        if (activeTasks.containsKey(uuid)) {
            activeTasks.get(uuid).cancel();
            activeTasks.remove(uuid);
        }
        String expireMsg = configService.getMessage("combat_expired", "&aYou are no longer in combat.");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', expireMsg));
    }

    private void startCombatTask(Player player) {
        UUID uuid = player.getUniqueId();
        String actionBarTemplate = configService.getMessage("actionbar_format", "&7Combat: &c%time%s");

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline() || !combatTimers.containsKey(uuid)) {
                cancelTask(uuid);
                return;
            }

            int timeLeft = combatTimers.get(uuid);
            if (timeLeft > 0) {
                // Send Action Bar text above hotbar
                String barText = actionBarTemplate.replace("%time%", String.valueOf(timeLeft));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', barText)));
                
                combatTimers.put(uuid, timeLeft - 1);
            } else {
                untagPlayer(player);
            }
        }, 0L, 20L); // Runs every second

        activeTasks.put(uuid, task);
    }

    private void cancelTask(UUID uuid) {
        if (activeTasks.containsKey(uuid)) {
            activeTasks.get(uuid).cancel();
            activeTasks.remove(uuid);
        }
        combatTimers.remove(uuid);
    }
}
