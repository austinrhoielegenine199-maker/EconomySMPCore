package com.nyretha.home.listener;

import com.nyretha.home.gui.ConfirmGUI;
import com.nyretha.home.gui.HomeGUI;
import com.nyretha.home.model.Home;
import com.nyretha.home.service.HomeService;
import com.nyretha.home.service.HomeTeleportService;
import com.nyretha.shop.utils.HexColor;
import com.nyretha.team.service.TeamService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class HomeListener implements Listener {

    private final HomeService homeService;
    private final HomeTeleportService teleportService;
    private final TeamService teamService;

    public HomeListener(HomeService homeService, HomeTeleportService teleportService, TeamService teamService) {
        this.homeService = homeService;
        this.teleportService = teleportService;
        this.teamService = teamService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();

        if (title.equals(HexColor.format("&8ʜᴏᴍᴇꜱ"))) {
            event.setCancelled(true);
            int slot = event.getSlot();

            // Set Team Home
            if (slot == 1) {
                if (teamService != null && teamService.isInTeam(player)) {
                    teamService.setTeamHome(player, player.getLocation());
                    HomeGUI.openGUI(player, homeService, teamService);
                } else {
                    player.sendMessage(HexColor.format("&cYou are not in a team!"));
                }
                return;
            }

            // Teleport Team Home
            if (slot == 10) {
                if (teamService != null && teamService.hasTeamHome(player)) {
                    player.teleport(teamService.getTeamHome(player));
                    player.sendMessage(HexColor.format("&aTeleported to team home!"));
                }
                return;
            }

            // Personal Homes (Beds)
            int[] bedSlots = {12, 13, 14, 15, 16};
            for (int i = 0; i < bedSlots.length; i++) {
                if (slot == bedSlots[i]) {
                    int homeId = i + 1;
                    if (homeService.hasHome(player, homeId)) {
                        Home home = homeService.getHome(player, homeId);
                        teleportService.teleportToHome(player, home, 5);
                        player.closeInventory();
                    } else if (homeId <= homeService.getMaxHomes(player)) {
                        homeService.setHome(player, homeId, "Home " + homeId, player.getLocation());
                        player.sendMessage(HexColor.format("&aHome " + homeId + " set!"));
                        HomeGUI.openGUI(player, homeService, teamService);
                    }
                    return;
                }
            }

            // Delete Personal Homes (Dyes)
            int[] dyeSlots = {21, 22, 23, 24, 25};
            for (int i = 0; i < dyeSlots.length; i++) {
                if (slot == dyeSlots[i]) {
                    int homeId = i + 1;
                    if (homeService.hasHome(player, homeId)) {
                        ConfirmGUI.openConfirm(player, homeId);
                    }
                    return;
                }
            }
        }

        if (title.equals(HexColor.format("&8ᴄᴏɴꜰʀɪᴍ"))) {
            event.setCancelled(true);
            int slot = event.getSlot();

            if (slot == 11) { // Cancel
                HomeGUI.openGUI(player, homeService, teamService);
            } else if (slot == 15) { // Confirm Delete
                var infoItem = event.getInventory().getItem(13);
                if (infoItem != null && infoItem.hasItemMeta()) {
                    String name = infoItem.getItemMeta().getDisplayName();
                    int homeId = Integer.parseInt(name.replaceAll("[^0-9]", ""));
                    homeService.deleteHome(player, homeId);
                    player.sendMessage(HexColor.format("&cHome " + homeId + " deleted!"));
                }
                HomeGUI.openGUI(player, homeService, teamService);
            }
        }
    }
}
