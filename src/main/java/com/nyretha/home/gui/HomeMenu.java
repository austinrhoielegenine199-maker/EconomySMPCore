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

    public static void open(Player player, HomeGuiConfigService configService, HomeManager homeManager) {
        String title = ChatColor.translateAlternateColorCodes('&', configService.getGuiTitle());
        int rows = configService.getGuiRows();
        Inventory inv = Bukkit.createInventory(null, rows * 9, title);

        Set<String> playerHomes = homeManager.getHomeNames(player.getUniqueId());
        List<Integer> bedSlots = configService.getSlots("beds");
        List<Integer> dyeSlots = configService.getSlots("dyes");

        for (int i = 0; i < 5; i++) {
            int slotNum = i + 1;
            String homeNameStr = String.valueOf(slotNum);
            boolean hasHome = playerHomes.contains(homeNameStr);
            boolean hasPermission = player.hasPermission("nyretha.home." + slotNum) || player.hasPermission("nyretha.home.all") || slotNum <= 1;

            if (i < bedSlots.size()) {
                int bedSlot = bedSlots.get(i);
                inv.setItem(bedSlot, createBedItem(slotNum, hasHome, hasPermission, configService));
            }

            if (i < dyeSlots.size()) {
                int dyeSlot = dyeSlots.get(i);
                inv.setItem(dyeSlot, createDyeItem(slotNum, hasHome, hasPermission, configService));
            }
        }

        player.openInventory(inv);
    }

    @SuppressWarnings("unchecked")
    private static ItemStack createBedItem(int number, boolean hasHome, boolean hasPermission, HomeGuiConfigService configService) {
        String itemKey;
        if (!hasPermission) {
            itemKey = "no-permission-bed";
        } else if (hasHome) {
            itemKey = "existing-bed";
        } else {
            itemKey = "empty-bed";
        }

        Map<String, Object> itemData = configService.getItemConfig(itemKey);
        Material material = Material.LIGHT_GRAY_BED;
        String name = "&7ɴᴏ ʜᴏᴍᴇ ѕᴇᴛ";
        List<String> loreList = new ArrayList<>();

        if (itemData != null) {
            if (itemData.containsKey("material")) {
                try {
                    material = Material.valueOf((String) itemData.get("material"));
                } catch (Exception ignored) {}
            }
            if (itemData.containsKey("name")) {
                name = (String) itemData.get("name");
            }
            if (itemData.containsKey("lore")) {
                Object loreObj = itemData.get("lore");
                if (loreObj instanceof List) {
                    loreList = (List<String>) loreObj;
                }
            }
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
        String itemKey;
        if (!hasPermission) {
            itemKey = "no-permission-dye";
        } else if (hasHome) {
            itemKey = "existing-dye";
        } else {
            itemKey = "empty-dye";
        }

        Map<String, Object> itemData = configService.getItemConfig(itemKey);
        Material material = Material.GRAY_DYE;
        String name = "&7ɴᴏ ʜᴏᴍᴇ ѕᴇᴛ";
        List<String> loreList = new ArrayList<>();

        if (itemData != null) {
            if (itemData.containsKey("material")) {
                try {
                    material = Material.valueOf((String) itemData.get("material"));
                } catch (Exception ignored) {}
            }
            if (itemData.containsKey("name")) {
                name = (String) itemData.get("name");
            }
            if (itemData.containsKey("lore")) {
                Object loreObj = itemData.get("lore");
                if (loreObj instanceof List) {
                    loreList = (List<String>) loreObj;
                }
            }
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
}
