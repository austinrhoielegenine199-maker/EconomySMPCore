package com.nyretha.home.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HomeManageMenu {

    public static void open(Player player, String homeName) {
        Inventory inv = Bukkit.createInventory(null, 27, "Manage: " + homeName);

        // Teleport Button (Slot 0)
        ItemStack teleport = new ItemStack(Material.LODESTONE);
        ItemMeta tMeta = teleport.getItemMeta();
        if (tMeta != null) {
            tMeta.setDisplayName("§fTeleport");
            teleport.setItemMeta(tMeta);
        }
        inv.setItem(0, teleport);

        // Rename Button (Slot 1)
        ItemStack rename = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta rMeta = rename.getItemMeta();
        if (rMeta != null) {
            rMeta.setDisplayName("§fRename");
            rename.setItemMeta(rMeta);
        }
        inv.setItem(1, rename);

        // Delete Button (Slot 4)
        ItemStack delete = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta dMeta = delete.getItemMeta();
        if (dMeta != null) {
            dMeta.setDisplayName("§fDelete");
            delete.setItemMeta(dMeta);
        }
        inv.setItem(4, delete);

        player.openInventory(inv);
    }
}
