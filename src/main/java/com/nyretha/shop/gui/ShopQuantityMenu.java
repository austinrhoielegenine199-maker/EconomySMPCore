package com.nyretha.shop.gui;

import com.nyretha.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class ShopQuantityMenu {

    public static void openQuantityMenu(Player player, ItemStack targetItem, double unitPrice, int currentQuantity, boolean isFlakes) {
        Inventory inv = Bukkit.createInventory(null, 27, ColorUtils.color("&8Select Amount"));

        // Red Glass Panes on Slots 9, 10, 11
        inv.setItem(9, createButton(Material.RED_STAINED_GLASS_PANE, "&c-64", "-64"));
        inv.setItem(10, createButton(Material.RED_STAINED_GLASS_PANE, "&c-10", "-10"));
        inv.setItem(11, createButton(Material.RED_STAINED_GLASS_PANE, "&c-1", "-1"));

        // Item Icon on Slot 13
        ItemStack displayItem = targetItem.clone();
        displayItem.setAmount(Math.max(1, Math.min(64, currentQuantity)));
        ItemMeta meta = displayItem.getItemMeta();
        if (meta != null) {
            double total = unitPrice * currentQuantity;
            String currencySymbol = isFlakes ? "❄ " : "$";
            meta.setLore(Collections.singletonList(ColorUtils.color("&7Total: &a" + currencySymbol + String.format("%.2f", total))));
            displayItem.setItemMeta(meta);
        }
        inv.setItem(13, displayItem);

        // Green Glass Panes on Slots 15, 16, 17
        inv.setItem(15, createButton(Material.GREEN_STAINED_GLASS_PANE, "&a+1", "+1"));
        inv.setItem(16, createButton(Material.GREEN_STAINED_GLASS_PANE, "&a+10", "+10"));
        inv.setItem(17, createButton(Material.GREEN_STAINED_GLASS_PANE, "&a+64", "+64"));

        // Cancel Button on Slot 22
        inv.setItem(22, createButton(Material.RED_WOOL, "&#009bffᴄᴀɴᴄᴇʟ", "&7ᴄʟɪᴄᴋ ᴛᴏ ᴄᴀɴᴄᴇʟ"));

        // Confirm Button on Slot 24
        inv.setItem(24, createButton(Material.LIME_WOOL, "&#009bffᴄᴏɴғɪʀᴍ", "&7ᴄʟɪᴄᴋ ᴛᴏ ᴄᴏɴғɪʀᴍ"));

        player.openInventory(inv);
    }

    private static ItemStack createButton(Material mat, String name, String lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtils.color(name));
            if (lore != null && !lore.isEmpty()) {
                meta.setLore(Collections.singletonList(ColorUtils.color(lore)));
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
