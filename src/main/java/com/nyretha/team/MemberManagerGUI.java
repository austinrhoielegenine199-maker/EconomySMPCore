package com.nyretha.team.gui;

import com.nyretha.shop.utils.HexColor;
import com.nyretha.team.model.TeamMember;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MemberManagerGUI {

    public static void openManager(Player player, TeamMember targetMember) {
        Inventory inv = Bukkit.createInventory(null, 27, HexColor.format("&8ᴍᴇᴍʙᴇʀ ᴍᴀɴᴀɢᴇʀ"));

        inv.setItem(10, createToggleItem(Material.DIAMOND_SWORD, "&#34eb9bᴛᴇᴀᴍ ᴘᴠᴘ", true));
        inv.setItem(11, createToggleItem(Material.LIGHT_GRAY_BED, "&#34eb9bᴛᴇᴀᴍ ʜᴏᴍᴇ", targetMember.canAccessHome()));
        inv.setItem(12, createToggleItem(Material.RED_BED, "&#34eb9bꜱᴇᴛ ʜᴏᴍᴇ", targetMember.canSetHome()));
        inv.setItem(13, createToggleItem(Material.BARRIER, "&#34eb9bᴛᴇᴀᴍ ᴀᴅᴍɪɴ", targetMember.getRank().equalsIgnoreCase("OWNER")));
        inv.setItem(14, createToggleItem(Material.PLAYER_HEAD, "&#34eb9bᴍᴀɴᴀɢᴇ ᴍᴇᴍʙᴇʀꜱ", targetMember.canManageMembers()));

        player.openInventory(inv);
    }

    private static ItemStack createToggleItem(Material mat, String title, boolean status) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(HexColor.format(title));
            String statusText = status ? "&#02de4fAllowed" : "&#e00202Denied";
            meta.setLore(List.of(HexColor.format("&fPermission: " + statusText)));
            item.setItemMeta(meta);
        }
        return item;
    }
}
