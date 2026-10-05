package com.nyretha.tp.listener;

import com.nyretha.tp.command.TpCommand;
import com.nyretha.tp.gui.TpConfirmGui;
import com.nyretha.tp.service.TpConfigService;
import com.nyretha.tp.service.TpTaskService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

public class TpListener implements Listener {
    private final TpConfigService configService;
    private final Plugin plugin;

    public TpListener(TpConfigService configService, Plugin plugin) {
        this.configService = configService;
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage();
        String lowerMsg = msg.toLowerCase();
        Player recipient = event.getPlayer();

        // Handle /tpaccept <player>
        if (lowerMsg.startsWith("/tpaccept")) {
            event.setCancelled(true);
            String[] parts = msg.split(" ");
            if (parts.length < 2) {
                recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /tpaccept <player>"));
                return;
            }

            Player requester = Bukkit.getPlayer(parts[1]);
            if (requester == null || !requester.isOnline()) {
                recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe player who sent the request is no longer online."));
                return;
            }

            TpCommand.PendingTpSession session = TpCommand.activeSessions.get(recipient.getUniqueId());
            if (session == null || !session.requesterUuid.equals(requester.getUniqueId())) {
                recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis teleport request has expired or does not exist."));
                return;
            }

            TpTaskService.sendAcceptedNotification(recipient, configService);
            TpTaskService.sendAcceptedNotification(requester, configService);
            TpConfirmGui.open(recipient, requester, session.type);
            return;
        }

        // Handle /tpdeny <player> (or generic /tpdeny)
        if (lowerMsg.startsWith("/tpdeny")) {
            event.setCancelled(true);
            
            TpCommand.PendingTpSession session = TpCommand.activeSessions.remove(recipient.getUniqueId());
            if (session == null) {
                recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have no pending teleport requests to deny."));
                return;
            }

            recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', configService.getMessage("teleport_denied_message", "&cYou have denied the teleport request.")));

            Player requester = Bukkit.getPlayer(session.requesterUuid);
            if (requester != null && requester.isOnline()) {
                requester.sendMessage(ChatColor.translateAlternateColorCodes('&', configService.getMessage("teleport_denied_target", "&cYour teleport request was denied.")));
            }
        }
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

                Player requester = Bukkit.getPlayer(session.requesterUuid);
                if (requester != null && requester.isOnline()) {
                    Player traveler = session.type.equals("tpa") ? requester : player;
                    Player destinationHolder = session.type.equals("tpa") ? player : requester;

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', configService.getMessage("tpa_sent", "&eTeleport request confirmed.")));
                    
                    TpTaskService.startTeleportCountdown(traveler, destinationHolder.getLocation(), plugin, configService);
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe target player is no longer online."));
                }
            }
        }
    }
}
