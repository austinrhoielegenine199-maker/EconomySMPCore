package com.nyretha.team.gui;

import com.nyretha.shop.utils.HexColor;
import com.nyretha.team.model.Team;
import com.nyretha.team.model.TeamMember;
import com.nyretha.team.service.TeamService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class TeamGUI {

    public static void openGUI(Player player, TeamService teamService, int page) {
        Team team = teamService.getTeam(player);
        if (team == null) {
            player.sendMessage(HexColor.format("&cYou are not in a team!"));
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 54, HexColor.format("&8ᴛᴇᴀᴍ (Page " + page + ")"));

        // Bottom Bar Control Buttons
        inv.setItem(45, createItem(Material.OAK_SIGN, "&#34eb9bꜱᴇᴀʀᴄʜ", "&fClick to search"));
        inv.setItem(46, createItem(Material.HOPPER, "&#34eb9bѕᴏʀᴛ", "&fClick to sort members"));
        inv.setItem(48, createItem(Material.ARROW, "&#34eb9bʙᴀᴄᴋ", "&fClick to go to previous page"));
        inv.setItem(49, createItem(Material.IRON_HELMET, "&#34eb9bᴛᴇᴀᴍ " + team.getName(), "&fClick to refresh\n&fAdd up to 27 team members"));
        inv.setItem(50, createItem(Material.ARROW, "&#34eb9bɴᴇxᴛ", "&fClick to go to next page"));
        inv.setItem(52, createItem(Material.WHITE_BANNER, "&#34eb9bᴛᴇᴀᴍ ʜᴏᴍᴇ", "&fSet the team home with /team sethome"));

        String pvpStatus = team.isPvpEnabled() ? "&#34eb9b&lON" : "&FF0000&lOFF";
        inv.setItem(53, createItem(Material.DIAMOND_SWORD, "&#34eb9bᴘᴠᴘ", "&fCurrently: " + pvpStatus));

        // Member Heads
        int slot = 0;
        for (TeamMember member : team.getMembers().values()) {
            if (slot >= 45) break;

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(member.getUuid()));
                meta.setDisplayName(HexColor.format("&#34eb9b" + member.getName()));
                meta.setLore(List.of(
                        HexColor.format("&fRank: &e" + member.getRank()),
                        HexColor.format("&fClick to manage")
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
