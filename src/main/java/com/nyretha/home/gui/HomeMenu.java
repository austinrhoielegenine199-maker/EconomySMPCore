package com.nyretha.home.gui;

import com.nyretha.home.model.HomeManager;
import com.nyretha.home.service.HomeGuiConfigService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeMenu {

    public static void open(Player player, HomeGuiConfigService configService, HomeManager homeManager /*, TeamManager teamManager */) {
        String title = ChatColor.translateAlternateColorCodes('&', configService.getGuiTitle());
        int rows = configService.getGuiRows();
        Inventory inv = Bukkit.createInventory(null, rows * 9, title);

        Set<String> playerHomes = homeManager.getHomeNames(player.getUniqueId());
        List<Integer> bedSlots = configService.getSlots("beds");
        List<Integer> dyeSlots = configService.getSlots("dyes");

        // 1. Render Personal Homes (Beds & Dyes 1-5)
        for (int i = 0; i < 5; i++) {
            int slotNum = i + 1;
            String homeNameStr = String.valueOf(slotNum);
            boolean hasHome = playerHomes.contains(homeNameStr);
            boolean hasPermission = player.hasPermission("nyretha.home." + slotNum) || player.hasPermission("nyretha.home.all") || slotNum <= 1;

            if (i < bedSlots.size()) {
                inv.setItem(bedSlots.get(i), createBedItem(slotNum, hasHome, hasPermission, configService));
            }

            if (i < dyeSlots.size()) {
                inv.setItem(dyeSlots.get(i), createDyeItem(slotNum, hasHome, hasPermission, configService));
            }
        }

        // 2. Render Team Banner & Dye
        boolean isInTeam = false; // Replace with: teamManager.isInTeam(player.getUniqueId());
        boolean hasTeamHome = false; // Replace with: teamManager.hasTeamHome(player.getUniqueId());
        boolean canManageTeamHome = false; // Replace with: teamManager.canManageTeamHome(player.getUniqueId());

        int teamBannerSlot = configService.getSlot("team-banner");
        int teamDyeSlot = configService.getSlot("team-dye");

        inv.setItem(teamBannerSlot, createTeamBannerItem(isInTeam, hasTeamHome, configService));
        inv.setItem(teamDyeSlot, createTeamDyeItem(isInTeam, hasTeamHome, canManageTeamHome, configService));

        player.openInventory(inv);
    }

    @SuppressWarnings("unchecked")
    private static ItemStack createBedItem(int number, boolean hasHome, boolean hasPermission, HomeGuiConfigService configService) {
        String itemKey = !hasPermission ? "no-permission-bed" : (hasHome ? "existing-bed" : "empty-bed");
        Map<String, Object> itemData = configService.getItemConfig(itemKey);
        
        Material material = Material.LIGHT_GRAY_BED;
        String name = "&7ɴᴏ ʜᴏᴍᴇ ѕᴇᴛ";
        List<String> loreList = new ArrayList<>();

        if (itemData != null) {
            if (itemData.containsKey("material")) {
                try { material = Material.valueOf((String) itemData.get("material")); } catch (Exception ignored) {}
            }
            if (itemData.containsKey("name")) name = (String) itemData.get("name");
            if (itemData.containsKey("lore")) loreList = (List<String>) itemData.get("lore");
        }

        name = name.replace("{number}", String.valueOf(number)).replace("{name}", "");
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> coloredLore = new ArrayList<>();
            for (String line : loreList) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', line.replace("{number}", String.valueOf(number))));
            }
            meta.setLore(coloredLore);
            item.setItemMeta(meta);
        }
        return item;
    }

    @SuppressWarnings("unchecked")
    private static ItemStack createDyeItem(int number, boolean hasHome, boolean hasPermission, HomeGuiConfigService configService) {
        String itemKey = !hasPermission ? "no-permission-dye" : (hasHome ? "existing-dye" : "empty-dye");
        Map<String, Object> itemData = configService.getItemConfig(itemKey);
        
        Material material = Material.GRAY_DYE;
        String name = "&7ɴᴏ ʜᴏᴍᴇ ѕᴇᴛ";
        List<String> loreList = new ArrayList<>();

        if (itemData != null) {
            if (itemData.containsKey("material")) {
                try { material = Material.valueOf((String) itemData.get("material")); } catch (Exception ignored) {}
            }
            if (itemData.containsKey("name")) name = (String) itemData.get("name");
            if (itemData.containsKey("lore")) loreList = (List<String>) itemData.get("lore");
        }

        name = name.replace("{number}", String.valueOf(number)).replace("{name}", "");
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> coloredLore = new ArrayList<>();
            for (String line : loreList) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', line.replace("{number}", String.valueOf(number))));
            }
            meta.setLore(coloredLore);
            item.setItemMeta(meta);
        }
        return item;
    }

    @SuppressWarnings("unchecked")
    private static ItemStack createTeamBannerItem(boolean isInTeam, boolean hasTeamHome, HomeGuiConfigService configService) {
        String itemKey;
        if (!isInTeam) {
            itemKey = "team-banner-no-team";
        } else if (hasTeamHome) {
            itemKey = "team-banner-has-home";
        } else {
            itemKey = "team-banner-no-home";
        }

        Map<String, Object> itemData = configService.getItemConfig(itemKey);
        Material material = Material.WHITE_BANNER;
        String name = "&fᴛᴇᴀᴍ ʜᴏᴍᴇ";
        List<String> loreList = new ArrayList<>();

        if (itemData != null) {
            if (itemData.containsKey("material")) {
                try { material = Material.valueOf((String) itemData.get("material")); } catch (Exception ignored) {}
            }
            if (itemData.containsKey("name")) name = (String) itemData.get("name");
            if (itemData.containsKey("lore")) loreList = (List<String>) itemData.get("lore");
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> coloredLore = new ArrayList<>();
            for (String line : loreList) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(coloredLore);
            item.setItemMeta(meta);
        }
        return item;
    }

    @SuppressWarnings("unchecked")
    private static ItemStack createTeamDyeItem(boolean isInTeam, boolean hasTeamHome, boolean canManage, HomeGuiConfigService configService) {
        String itemKey;
        if (!isInTeam) {
            itemKey = "team-dye-no-team";
        } else if (hasTeamHome) {
            itemKey = canManage ? "team-dye-has-home-manageable" : "team-dye-has-home-no-permission";
        } else {
            itemKey = "team-dye-no-home";
        }

        Map<String, Object> itemData = configService.getItemConfig(itemKey);
        Material material = Material.BLUE_DYE;
        String name = "&fᴛᴇᴀᴍ ʜᴏᴍᴇ";
        List<String> loreList = new ArrayList<>();

        if (itemData != null) {
            if (itemData.containsKey("material")) {
                try { material = Material.valueOf((String) itemData.get("material")); } catch (Exception ignored) {}
            }
            if (itemData.containsKey("name")) name = (String) itemData.get("name");
            if (itemData.containsKey("lore")) loreList = (List<String>) itemData.get("lore");
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> coloredLore = new ArrayList<>();
            for (String line : loreList) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(coloredLore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
