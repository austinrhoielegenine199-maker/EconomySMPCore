package com.nyretha.tpa.gui;

import com.nyretha.shop.utils.HexColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class TPAAcceptGUI {

    public static void openGUI(Player target, Player sender, String world, boolean isFlying) {
        Inventory inv = Bukkit.createInventory(null, 27, HexColor.format("&8ᴛᴘᴀ ᴀᴄᴄᴇᴘᴛ"));

        inv.setItem(10, createItem(Material.RED_STAINED_GLASS_PANE, "&#FF0000ᴄᴀɴᴄᴇʟ", List.of("&fClick to decline the TPA request")));
        inv.setItem(12, createItem(Material.GRASS_BLOCK, "&#00f986ʟᴏᴄᴀᴛɪᴏɴ", List.of("&7" + world)));
        inv.setItem(13, createPlayerHead(sender, "&#00f986ᴘʟᴀʏᴇʀ", List.of("&7" + sender.getName())));
        inv.setItem(14, createItem(Material.FEATHER, "&#00f986ғʟᴀᴋᴇ ғʟʏɪɴɢ", List.of("&7" + (isFlying ? "Yes" : "No"))));
        inv.setItem(16, createItem(Material.LIME_STAINED_GLASS_PANE, "&#00FF00ᴄᴏɴғɪʀᴍ", List.of("&fClick to accept the teleport request from " + sender.getName())));

        target.openInventory(inv);
    }

    private static ItemStack createItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(HexColor.format(name));
            List<String> formatted = new ArrayList<>();
            for (String line : lore) formatted.add(HexColor.format(line));
            meta.setLore(formatted);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createPlayerHead(Player player, String name, List<String> lore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.setDisplayName(HexColor.format(name));
            List<String> formatted = new ArrayList<>();
            for (String line : lore) formatted.add(HexColor.format(line));
            meta.setLore(formatted);
            head.setItemMeta(meta);
        }
        return head;
    }
}
