package com.nyretha.pay.gui;

import com.nyretha.pay.model.PayTransaction;
import com.nyretha.pay.service.PayService;
import com.nyretha.shop.utils.HexColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PayHistoryGUI {

    public static void openGUI(Player player, Player target, PayService payService, int page) {
        Inventory inv = Bukkit.createInventory(null, 54, HexColor.format("&8ᴘᴀʏᴍᴇɴᴛ ʟᴏɢꜱ " + target.getName() + " (Page " + page + ")"));

        // Navigation Buttons
        inv.setItem(45, createItem(Material.ARROW, "&#00fc88ᴘʀᴇᴠɪᴏᴜꜱ", "&fClick to go to the previous page"));
        inv.setItem(48, createItem(Material.HOPPER, "&#00fc88ꜱᴏʀᴛ", "&fSort transaction list"));
        inv.setItem(49, createItem(Material.ANVIL, "&#00fc88ʀᴇꜰʀᴇꜱʜ", "&fClick to refresh"));
        inv.setItem(50, createItem(Material.CAULDRON, "&#00fc88ꜰɪʟᴛᴇʀ", "&fFilter transaction list"));
        inv.setItem(53, createItem(Material.ARROW, "&#00fc88ɴᴇxᴛ", "&fClick to go to the next page"));

        // Populate history items in slots 0-44
        List<PayTransaction> history = payService.getHistory(target.getUniqueId());
        int slot = 0;

        for (PayTransaction tx : history) {
            if (slot > 44) break;

            boolean isReceive = tx.getReceiver().equalsIgnoreCase(target.getName());
            Material mat = isReceive ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
            String prefix = isReceive ? "&#04fc04+" : "&#fc0404-";
            String title = isReceive ? "&8Received" : "&8Gift";

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(HexColor.format(prefix + tx.getAmount()));
                meta.setLore(List.of(
                        HexColor.format(title),
                        "",
                        HexColor.format("&fID: " + prefix + tx.getId()),
                        HexColor.format("&fAmount: " + prefix + tx.getAmount()),
                        HexColor.format("&fFrom: &#00A0FC" + tx.getSender()),
                        HexColor.format("&fTo: &#00A0FC" + tx.getReceiver()),
                        HexColor.format("&fDate &7" + tx.getDate())
                ));
                item.setItemMeta(meta);
            }
            inv.setItem(slot++, item);
        }

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
