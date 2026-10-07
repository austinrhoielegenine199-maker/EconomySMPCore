package com.nyretha.flakes.gui;

import com.nyretha.shop.utils.HexColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class FlakePayGUI {

    public static void openGUI(Player player, Player target) {
        Inventory inv = Bukkit.createInventory(null, 27, HexColor.format("&8ᴘᴀʏ ꜰʟᴀᴋᴇꜱ: " + target.getName()));

        inv.setItem(11, createItem(Material.RED_STAINED_GLASS_PANE, "&#FF0000ᴄᴀɴᴄᴇʟ", "&fClick to cancel"));
        inv.setItem(13, createItem(Material.AMETHYST_SHARD, "&#A303F9" + target.getName(), "&fType amount in chat"));
        inv.setItem(15, createItem(Material.LIME_STAINED_GLASS_PANE, "&#04fc04ᴄᴏɴꜰɪʀᴍ", "&fClick to confirm"));

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
