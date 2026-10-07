package com.nyretha.home.gui;

import com.nyretha.shop.utils.HexColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ConfirmGUI {

    public static void openConfirm(Player player, int homeId) {
        Inventory inv = Bukkit.createInventory(null, 27, HexColor.format("&8ᴄᴏɴꜰʀɪᴍ"));

        // Cancel Button (Slot 11)
        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cMeta = cancel.getItemMeta();
        if (cMeta != null) {
            cMeta.setDisplayName(HexColor.format("&#FF0000ᴄᴀɴᴄᴇʟ"));
            cMeta.setLore(List.of(HexColor.format("&fClick to cancel")));
            cancel.setItemMeta(cMeta);
        }
        inv.setItem(11, cancel);

        // Info Button (Slot 13)
        ItemStack info = new ItemStack(Material.BLUE_DYE);
        ItemMeta iMeta = info.getItemMeta();
        if (iMeta != null) {
            iMeta.setDisplayName(HexColor.format("&#0044FCʜᴏᴍᴇ " + homeId));
            info.setItemMeta(iMeta);
        }
        inv.setItem(13, info);

        // Confirm Button (Slot 15)
        ItemStack confirm = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta confMeta = confirm.getItemMeta();
        if (confMeta != null) {
            confMeta.setDisplayName(HexColor.format("&#0bf52bᴄᴏɴꜰʀɪᴍ"));
            confMeta.setLore(List.of(HexColor.format("&fClick to delete")));
            confirm.setItemMeta(confMeta);
        }
        inv.setItem(15, confirm);

        player.openInventory(inv);
    }
}
