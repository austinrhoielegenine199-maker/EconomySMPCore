package com.nyretha.team.listener;

import com.nyretha.team.service.TeamManager;
import com.nyretha.team.service.TeamGuiConfigService;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TeamMenuListener implements Listener {
    private final TeamManager teamManager;
    private final TeamGuiConfigService configService;

    public TeamMenuListener(TeamManager teamManager, TeamGuiConfigService configService) {
        this.teamManager = teamManager;
        this.configService = configService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (title.contains("ᴛᴇᴀᴍ")) {
            event.setCancelled(true);
            int slot = event.getRawSlot();

            if (slot == 45) { // Search
                player.closeInventory();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', configService.getMessage("invite-hint")));
            } else if (slot == 46) { // Sort
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aCycling sort option..."));
            } else if (slot == 48) { // Back
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&ePrevious page..."));
            } else if (slot == 49) { // Info / Refresh
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTeam info refreshed!"));
            } else if (slot == 50) { // Next
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eNext page..."));
            } else if (slot == 52) { // Team Home
                player.closeInventory();
                if (!teamManager.isInTeam(player.getUniqueId()) || !teamManager.hasTeamHome(teamManager.getTeamName(player.getUniqueId()))) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', configService.getMessage("no-home")));
                    return;
                }
                Location homeLoc = teamManager.getTeamHome(teamManager.getTeamName(player.getUniqueId()));
                player.teleport(homeLoc);
                String teleportMsg = configService.getMessage("teleported");
                if (!teleportMsg.isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', teleportMsg));
                }
            } else if (slot == 53) { // PvP Toggle
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aToggling team PvP status..."));
            }
        }
    }
}
