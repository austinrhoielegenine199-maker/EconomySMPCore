package com.nyretha.flakes.gui;

import com.nyretha.flakes.service.FlakesService;
import com.nyretha.shop.utils.HexColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class FlakeViewGUI {

    public static void openGUI(Player player, FlakesService flakesService, int page) {
        Inventory inv = Bukkit.createInventory(null, 54, HexColor.format("&8ꜱʜᴀʀᴅ (Page " + page + ")"));

        inv.setItem(45, createItem(Material.ARROW, "&#00fc88ᴘʀᴇᴠɪᴏᴜꜱ", "&fClick to go to the previous page"));
        inv.setItem(49, createItem(Material.ANVIL, "&#A303F9ꜰʟᴀᴋᴇꜱ", "&fClick to refresh"));
        inv.setItem(53, createItem(Material.ARROW, "&#00fc88ɴᴇxᴛ", "&fClick to go to the next page"));

        int slot = 0;
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (slot >= 45) break;

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(target);
                meta.setDisplayName(HexColor.format("&#04fc04" + target.getName()));
                meta.setLore(List.of(
                        HexColor.format("&fFlakes: &#A303F9" + flakesService.getFlakes(target.getUniqueId())),
                        HexColor.format("&fClick to pay flakes")
                ));
                head.setItemMeta(meta);
            }
            inv.setItem(slot++, head);
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
