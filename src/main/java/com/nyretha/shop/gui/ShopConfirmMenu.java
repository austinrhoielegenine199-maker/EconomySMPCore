package com.nyretha.shop.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Map;

public class ShopConfirmMenu {

    public static void open(Player player, Map<String, Object> itemData, int currentAmount, int unitPrice) {
        String title = ChatColor.translateAlternateColorCodes('&', "&8Confirm Purchase");
        Inventory inv = Bukkit.createInventory(null, 27, title);

        // Quantity Decrement Panels (Red)
        inv.setItem(9, createItem(Material.RED_STAINED_GLASS_PANE, "&c-64"));
        inv.setItem(10, createItem(Material.RED_STAINED_GLASS_PANE, "&c-10"));
        inv.setItem(11, createItem(Material.RED_STAINED_GLASS_PANE, "&c-1"));

        // Central Preview Item (Slot 13)
        String materialName = (String) itemData.getOrDefault("material", "STONE");
        Material mat = Material.valueOf(materialName);
        ItemStack previewItem = new ItemStack(mat, Math.max(1, Math.min(64, currentAmount)));
        ItemMeta meta = previewItem.getItemMeta();
        if (meta != null) {
            if (itemData.containsKey("name")) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', (String) itemData.get("name")));
            }
            int totalPrice = unitPrice * currentAmount;
            meta.setLore(Arrays.asList(
                ChatColor.translateAlternateColorCodes('&', "&7Amount: &e" + currentAmount),
                ChatColor.translateAlternateColorCodes('&', "&7Total Price: &a$" + totalPrice)
            ));
            previewItem.setItemMeta(meta);
        }
        inv.setItem(13, previewItem);

        // Quantity Increment Panels (Light Green)
        inv.setItem(15, createItem(Material.LIME_STAINED_GLASS_PANE, "&a+1"));
        inv.setItem(16, createItem(Material.LIME_STAINED_GLASS_PANE, "&a+10"));
        inv.setItem(17, createItem(Material.LIME_STAINED_GLASS_PANE, "&a+64"));

        // Action Buttons: Cancel (Slot 21) & Confirm (Slot 23)
        inv.setItem(21, createItem(Material.RED_CONCRETE, "&c&lᴄᴀɴᴄᴇʟ"));
        inv.setItem(23, createItem(Material.LIME_CONCRETE, "&a&lᴄᴏɴғɪʀᴍ"));

        player.openInventory(inv);
    }

    private static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            item.setItemMeta(meta);
        }
        return item;
    }
}
