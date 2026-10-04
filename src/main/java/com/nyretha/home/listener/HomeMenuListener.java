package com.nyretha.home.listener;

import com.nyretha.home.gui.HomeDeleteMenu;
import com.nyretha.home.gui.HomeManageMenu;
import com.nyretha.home.model.HomeManager;
import com.nyretha.home.service.HomeGuiConfigService;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Set;

public class HomeMenuListener implements Listener {
    private final HomeManager homeManager;
    private final HomeGuiConfigService configService;
    private final Plugin plugin;

    public HomeMenuListener(HomeManager homeManager, HomeGuiConfigService configService, Plugin plugin) {
        this.homeManager = homeManager;
        this.configService = configService;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        String expectedTitle = ChatColor.translateAlternateColorCodes('&', configService.getGuiTitle());

        if (title.equals(expectedTitle)) {
            event.setCancelled(true);
            int slot = event.getRawSlot();

            List<Integer> bedSlots = configService.getSlots("beds");
            List<Integer> dyeSlots = configService.getSlots("dyes");

            int homeIndex = -1;
            boolean isDyeClick = false;

            if (bedSlots.contains(slot)) {
                homeIndex = bedSlots.indexOf(slot);
            } else if (dyeSlots.contains(slot)) {
                homeIndex = dyeSlots.indexOf(slot);
                isDyeClick = true;
            }

            if (homeIndex != -1) {
                int slotNum = homeIndex + 1;
                String homeName = String.valueOf(slotNum);
                Set<String> playerHomes = homeManager.getHomeNames(player.getUniqueId());
                boolean hasHome = playerHomes.contains(homeName);
                boolean hasPermission = player.hasPermission("nyretha.home." + slotNum) || player.hasPermission("nyretha.home.all") || slotNum <= 1;

                if (!hasPermission) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "#FF1D1DYou do not have permission to use this home slot."));
                    return;
                }

                if (isDyeClick) {
                    if (hasHome) {
                        player.closeInventory();
                        HomeDeleteMenu.open(player, homeName);
                    } else {
                        player.closeInventory();
                        homeManager.setHome(player.getUniqueId(), homeName, player.getLocation());
                        player.sendMessage("§aHome §e" + homeName + " §aset successfully!");
                    }
                } else {
                    if (hasHome) {
                        player.closeInventory();
                        HomeManageMenu.open(player, homeName);
                    } else {
                        player.closeInventory();
                        homeManager.setHome(player.getUniqueId(), homeName, player.getLocation());
                        player.sendMessage("§aHome §e" + homeName + " §aset successfully!");
                    }
                }
            }
        } else if (title.startsWith("Manage: ")) {
            event.setCancelled(true);
            String homeName = title.replace("Manage: ", "");
            int slot = event.getRawSlot();

            if (slot == 0) {
                player.closeInventory();
                Location loc = homeManager.getHome(player.getUniqueId(), homeName);
                if (loc != null) {
                    player.teleport(loc);
                    player.sendMessage("§7You teleported to your home §e" + homeName);
                } else {
                    player.sendMessage("§cHome not found.");
                }
            } else if (slot == 1) {
                player.closeInventory();
                HomeRenameListener.startRenaming(player.getUniqueId(), homeName);
                player.sendMessage("§aType the new name for your home in chat, or type §ccancel §ato abort.");
            } else if (slot == 4) {
                player.closeInventory();
                HomeDeleteMenu.open(player, homeName);
            }
        } else if (title.equals("Confirm Delete")) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            ItemStack displayItem = event.getInventory().getItem(13);
            if (displayItem == null || !displayItem.hasItemMeta()) return;
            String homeName = ChatColor.stripColor(displayItem.getItemMeta().getDisplayName());

            if (slot == 11) {
                player.closeInventory();
                player.sendMessage("§cDeletion cancelled.");
            } else if (slot == 15) {
                player.closeInventory();
                homeManager.removeHome(player.getUniqueId(), homeName);
                player.sendMessage("§aSuccessfully deleted home §e" + homeName + "§a!");
            }
        }
    }
}
