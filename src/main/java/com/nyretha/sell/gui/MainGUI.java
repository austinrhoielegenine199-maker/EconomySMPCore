package com.nyretha.sell.gui;

import com.nyretha.shop.utils.HexColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MainGUI {

    public static void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, HexColor.format("&8ꜱᴇʟʟ ᴍᴇɴᴜ"));

        inv.setItem(13, createItem(Material.HOPPER, "&#E0E319ꜱᴇʟʟ ᴀʟʟ", "&fClick to sell all sellable items in inventory"));

        player.openInventory(inv);
    }

    private static ItemStack createItem(Material mat, String name, String lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(HexColor.format(name));
            meta.setLore(List.of(HexColor.format(lore)));
            item.setItemMeta(meta);
        }
        return item;
    }
}
