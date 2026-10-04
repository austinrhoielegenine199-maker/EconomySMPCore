package com.nyretha.home.listener;

import com.nyretha.home.model.HomeManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeRenameListener implements Listener {
    private static final Map<UUID, String> renamingHomes = new HashMap<>();
    private final HomeManager homeManager;
    private final Plugin plugin;

    public HomeRenameListener(HomeManager homeManager, Plugin plugin) {
        this.homeManager = homeManager;
        this.plugin = plugin;
    }

    public static void startRenaming(UUID playerUuid, String oldName) {
        renamingHomes.put(playerUuid, oldName);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!renamingHomes.containsKey(uuid)) {
            return;
        }

        event.setCancelled(true);
        String oldName = renamingHomes.remove(uuid);
        String newName = event.getMessage().trim();

        if (newName.equalsIgnoreCase("cancel")) {
            player.sendMessage("§cHome rename cancelled.");
            return;
        }

        if (newName.isEmpty() || newName.contains(" ")) {
            player.sendMessage("§cInvalid home name. Rename cancelled.");
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            Location loc = homeManager.getHome(uuid, oldName);
            if (loc == null) {
                player.sendMessage("§cOriginal home no longer exists.");
                return;
            }

            homeManager.removeHome(uuid, oldName);
            homeManager.setHome(uuid, newName, loc);
            player.sendMessage("§aSuccessfully renamed home §e" + oldName + " §ato §e" + newName + "§a!");
        });
    }
}
