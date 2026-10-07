package com.nyretha.home.gui;

import com.nyretha.home.service.HomeService;
import com.nyretha.shop.utils.HexColor;
import com.nyretha.team.service.TeamService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class HomeGUI {

    public static void openGUI(Player player, HomeService homeService, TeamService teamService) {
        Inventory inv = Bukkit.createInventory(null, 36, HexColor.format("&8ʜᴏᴍᴇꜱ"));

        // 1. Set Team Home (Slot 1)
        ItemStack setTeamHomeItem = createItem(Material.OAK_SIGN, "&#0044FC&lꜱᴇᴛ ᴛᴇᴀᴍ ʜᴏᴍᴇ", "&fClick to set team home location");
        inv.setItem(1, setTeamHomeItem);

        // 2. Team Home Logic (Slot 10)
        boolean hasTeam = teamService != null && teamService.isInTeam(player);
        boolean hasTeamHome = teamService != null && teamService.hasTeamHome(player);

        Material bannerMat;
        String bannerTitle;
        String bannerLore;

        if (!hasTeam) {
            bannerMat = Material.RED_BANNER;
            bannerTitle = "&#FF0000ɴᴏ ᴛᴇᴀᴍ";
            bannerLore = "&fJoin or create a team to unlock";
        } else if (!hasTeamHome) {
            bannerMat = Material.GRAY_BANNER;
            bannerTitle = "&7ɴᴏ ᴛᴇᴀᴍ ʜᴏᴍᴇ ꜱᴇᴛ";
            bannerLore = "&fClick set team home above";
        } else {
            bannerMat = Material.BLUE_BANNER;
            bannerTitle = "&#0044FCᴛᴇᴀᴍ ʜᴏᴍᴇ";
            bannerLore = "&fClick to teleport to team home";
        }

        inv.setItem(10, createItem(bannerMat, bannerTitle, bannerLore));

        // 3. Personal Homes shifted right (Bed Slots: 12, 13, 14, 15, 16 | Dye Slots: 21, 22, 23, 24, 25)
        int[] bedSlots = {12, 13, 14, 15, 16};
        int[] dyeSlots = {21, 22, 23, 24, 25};
        int maxHomes = homeService.getMaxHomes(player);

        for (int i = 0; i < bedSlots.length; i++) {
            int homeNumber = i + 1;
            boolean isUnlocked = homeNumber <= maxHomes;
            boolean isSet = homeService.hasHome(player, homeNumber);

            // Bed Item
            ItemStack bedItem;
            if (!isUnlocked) {
                bedItem = createItem(Material.RED_BED, "&#FF0000ɴᴏ ᴘᴇʀᴍɪꜱꜱɪᴏɴ", "&fBuy a higher &bRank &fin /store for more homes");
            } else if (isSet) {
                bedItem = createItem(Material.LIGHT_BLUE_BED, "&#0044FCʜᴏᴍᴇ " + homeNumber, "&fClick to teleport");
            } else {
                bedItem = createItem(Material.LIGHT_GRAY_BED, "&7ɴᴏ ʜᴏᴍᴇ ꜱᴇᴛ", "&fClick to save your location");
            }
            inv.setItem(bedSlots[i], bedItem);

            // Dye Item
            ItemStack dyeItem;
            if (!isUnlocked) {
                dyeItem = createItem(Material.RED_DYE, "&#FF0000ɴᴏ ᴘᴇʀᴍɪꜱꜱɪᴏɴ", "&fBuy a higher &bRank &fin /store for more homes");
            } else if (isSet) {
                dyeItem = createItem(Material.BLUE_DYE, "&#0044FCʜᴏᴍᴇ " + homeNumber, "&fClick to delete");
            } else {
                dyeItem = createItem(Material.GRAY_DYE, "&7ɴᴏ ʜᴏᴍᴇ ꜱᴇᴛ", "&fClick to save your location");
            }
            inv.setItem(dyeSlots[i], dyeItem);
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
