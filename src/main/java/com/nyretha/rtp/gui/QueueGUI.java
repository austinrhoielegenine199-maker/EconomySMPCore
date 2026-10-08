package com.nyretha.rtp.gui;

import com.nyretha.shop.utils.HexColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class QueueGUI {

    public static void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, HexColor.format("&8ʀᴛᴘqᴜᴇᴜᴇ"));

        inv.setItem(11, createQueueItem(Material.GRASS_BLOCK, "&#00FF89ᴏᴠᴇʀᴡᴏʀʟᴅ", List.of("", "&bInformation:", "&fPlay 1v1s against other players", "&fIn different &#00FF89worlds.", "", "&#00FF89&l⏺ &fPlayers in queue: &#00FF890/2", "", "&#E0E319&l➟ &#E0E319&l&nCLICK&#E0E319 to queue")));
        inv.setItem(13, createQueueItem(Material.NETHERRACK, "&#00FF89ɴᴇᴛʜᴇʀ", List.of("", "&bInformation:", "&fPlay 1v1s against other players", "&fIn different &#00FF89worlds.", "", "&#00FF89&l⏺ &fPlayers in queue: &#00FF890/2", "", "&#E0E319&l➟ &#E0E319&l&nCLICK&#E0E319 to queue")));
        inv.setItem(15, createQueueItem(Material.END_STONE, "&#00FF89ᴇɴᴅ", List.of("", "&bInformation:", "&fPlay 1v1s against other players", "&fIn different &#00FF89worlds.", "", "&#00FF89&l⏺ &fPlayers in queue: &#00FF890/2", "", "&#E0E319&l➟ &#E0E319&l&nCLICK&#E0E319 to queue")));

        player.openInventory(inv);
    }

    private static ItemStack createQueueItem(Material material, String name, List<String> lore) {
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
