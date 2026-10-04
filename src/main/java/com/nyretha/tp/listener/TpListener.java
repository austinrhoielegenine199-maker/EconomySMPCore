package com.nyretha.tp.listener;

import com.nyretha.tp.command.TpCommand;
import com.nyretha.tp.service.TpConfigService;
import com.nyretha.tp.service.TpTaskService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

public class TpListener implements Listener {
    private final TpConfigService configService;
    private final Plugin plugin;

    public TpListener(TpConfigService configService, Plugin plugin) {
        this.configService = configService;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (title.contains("Request")) {
            event.setCancelled(true);
            int slot = event.getRawSlot();

            TpCommand.PendingTpSession session = TpCommand.activeSessions.get(player.getUniqueId());
            if (session == null) return;

            // Cancel Button (Slot 10)
            if (slot == 10) {
                TpCommand.activeSessions.remove(player.getUniqueId());
                player.closeInventory();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cTeleport request cancelled."));
                return;
            }

            // Confirm Button (Slot 16)
            if (slot == 16) {
                TpCommand.activeSessions.remove(player.getUniqueId());
                player.closeInventory();

                Player target = Bukkit.getPlayer(session.targetUuid);
                if (target != null && target.isOnline()) {
                    Player traveler = session.type.equals("tpa") ? player : target;
                    Player destinationHolder = session.type.equals("tpa") ? target : player;

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', configService.getMessage("tpa_sent", "&eSent teleport request.")));
                    
                    TpTaskService.startTeleportCountdown(traveler, destinationHolder.getLocation(), plugin, configService);
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe target player is no longer online."));
                }
            }
        }
    }
}
