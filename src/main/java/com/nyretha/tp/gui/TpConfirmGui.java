package com.nyretha.tp.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class TpConfirmGui {

    public static void open(Player viewer, Player target, String requestType) {
        String title = ChatColor.translateAlternateColorCodes('&', "&8" + requestType.toUpperCase() + " Request");
        Inventory inv = Bukkit.createInventory(null, 27, title);

        // 1. Red Cancel Button (Slot 10)
        inv.setItem(10, createButton(Material.RED_CONCRETE, "&c&lᴄᴀɴᴄᴇʟ"));

        // 2. Environment World Indicator (Slot 12)
        inv.setItem(12, getWorldIndicatorItem(target));

        // 3. Target Player Head (Slot 13) with IGN lore
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(target);
            skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b" + target.getName()));
            skullMeta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&8(" + target.getName() + ")")));
            head.setItemMeta(skullMeta);
        }
        inv.setItem(13, head);

        // 4. Green Confirm Button (Slot 16)
        inv.setItem(16, createButton(Material.LIME_CONCRETE, "&a&lᴄᴏɴғɪʀᴍ"));

        viewer.openInventory(inv);
    }

    private static ItemStack createButton(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack getWorldIndicatorItem(Player target) {
        String worldName = target.getWorld().getEnvironment().name();
        ItemStack item;
        String name;

        if (worldName.equals("NETHER")) {
            item = new ItemStack(Material.NETHERRACK);
            name = "&4ɴᴇᴛʜᴇʀ";
        } else if (worldName.equals("THE_END")) {
            item = new ItemStack(Material.END_STONE);
            name = "&eᴇɴᴅ";
        } else {
            item = new ItemStack(Material.GRASS_BLOCK);
            name = "&aᴏᴠᴇʀᴡᴏʀʟᴅ";
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            item.setItemMeta(meta);
        }
        return item;
    }
}
