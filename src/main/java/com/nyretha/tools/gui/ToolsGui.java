package com.nyretha.tools.gui;

import com.nyretha.shop.utils.HexColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ToolsGUI {

    public static void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, HexColor.format("&8ᴛᴏᴏʟꜱ"));

        // Hex color updated to #009bff for Flake Tools
        inv.setItem(10, createItem(Material.DIAMOND_PICKAXE, "&#009bffꜰʟᴀᴋᴇ ᴅʀɪʟʟ", "&fInstantly mines blocks in a 3x3 radius"));
        inv.setItem(12, createItem(Material.DIAMOND_AXE, "&#009bffꜰʟᴀᴋᴇ ᴛʀᴇᴇ ᴄʜᴏᴘᴘᴇʀ", "&fChops entire tree with one break"));
        inv.setItem(14, createItem(Material.DIAMOND_HOE, "&#009bffꜰʟᴀᴋᴇ ʜᴏᴇ", "&fHarvests crops in a 3x3 area"));
        inv.setItem(16, createItem(Material.DIAMOND_SHOVEL, "&#009bffꜰʟᴀᴋᴇ ꜱʜᴏᴠᴇʟ", "&fDigs dirt and sand in a 3x3 area"));

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
