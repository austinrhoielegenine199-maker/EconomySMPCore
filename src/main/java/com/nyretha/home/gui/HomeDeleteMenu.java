package com.nyretha.home.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HomeDeleteMenu {

    public static void open(Player player, String homeName) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "Confirm Delete"));

        // Cancel Button (Slot 11)
        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cMeta = cancel.getItemMeta();
        if (cMeta != null) {
            cMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "#FFFFFFCancel"));
            cMeta.setLore(java.util.List.of(ChatColor.translateAlternateColorCodes('&', "#AAAAAAClick to cancel")));
            cancel.setItemMeta(cMeta);
        }
        inv.setItem(11, cancel);

        // Home Display Item (Slot 13)
        ItemStack display = new ItemStack(Material.WHITE_BED);
        ItemMeta dMeta = display.getItemMeta();
        if (dMeta != null) {
            dMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "#FFFFFF" + homeName));
            display.setItemMeta(dMeta);
        }
        inv.setItem(13, display);

        // Confirm Button (Slot 15)
        ItemStack confirm = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta confMeta = confirm.getItemMeta();
        if (confMeta != null) {
            confMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "#FFFFFFConfirm"));
            confMeta.setLore(java.util.List.of(ChatColor.translateAlternateColorCodes('&', "#AAAAAAClick to delete")));
            confirm.setItemMeta(confMeta);
        }
        inv.setItem(15, confirm);

        player.openInventory(inv);
    }
}
