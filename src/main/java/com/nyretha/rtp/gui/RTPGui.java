package com.nyretha.rtp.gui;

import com.nyretha.shop.utils.HexColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RTPGui {

    public static void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, HexColor.format("&8ʀᴀɴᴅᴏᴍ ᴛᴇʟᴇᴘᴏʀᴛ"));

        inv.setItem(11, createWorldItem(Material.GRASS_BLOCK, "&#00FF89ᴏᴠᴇʀᴡᴏʀʟᴅ", List.of("&fClick to random teleport", "", "&7Player (&00A0FC" + Bukkit.getWorld("world").getPlayers().size() + "&7)")));
        inv.setItem(13, createWorldItem(Material.NETHERRACK, "&#00FF89ɴᴇᴛʜᴇʀ", List.of("&fClick to random teleport", "", "&7Player (&00A0FC" + Bukkit.getWorld("world_nether").getPlayers().size() + "&7)")));
        inv.setItem(15, createWorldItem(Material.END_STONE, "&#00FF89ᴇɴᴅ", List.of("&7Click to random teleport", "", "&7Player (&00A0FC" + Bukkit.getWorld("world_the_end").getPlayers().size() + "&7)")));

        player.openInventory(inv);
    }

    private static ItemStack createWorldItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(HexColor.format(name));
            meta.setLore(lore.stream().map(HexColor::format).toList());
            item.setItemMeta(meta);
        }
        return item;
    }
}
