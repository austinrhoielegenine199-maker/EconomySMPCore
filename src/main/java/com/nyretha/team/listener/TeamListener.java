package com.nyretha.team.listener;

import com.nyretha.shop.utils.HexColor;
import com.nyretha.team.gui.TeamGUI;
import com.nyretha.team.model.Team;
import com.nyretha.team.service.TeamService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TeamListener implements Listener {

    private final TeamService teamService;

    public TeamListener(TeamService teamService) {
        this.teamService = teamService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();

        if (title.startsWith(HexColor.format("&8ᴛᴇᴀᴍ"))) {
            event.setCancelled(true);
            int slot = event.getSlot();

            if (slot == 52) { // Set Team Home Button
                teamService.setTeamHome(player, player.getLocation());
                player.sendMessage(HexColor.format("&aTeam home has been set!"));
                TeamGUI.openGUI(player, teamService, 1);
            } else if (slot == 53) { // PVP Toggle Button
                Team team = teamService.getTeam(player);
                if (team != null) {
                    team.setPvpEnabled(!team.isPvpEnabled());
                    player.sendMessage(HexColor.format(team.isPvpEnabled() ? "&aTeam-PVP has been enabled!" : "&cTeam-PVP has been disabled!"));
                    TeamGUI.openGUI(player, teamService, 1);
                }
            }
        } else if (title.equals(HexColor.format("&8ᴍᴇᴍʙᴇʀ ᴍᴀɴᴀɢᴇʀ"))) {
            event.setCancelled(true);
        }
    }
}
