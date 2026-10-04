package com.nyretha.shop.gui;

import com.nyretha.shop.service.ShopConfigService;
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

public class ShopMenu {

    @SuppressWarnings("unchecked")
    public static void openMainShop(Player player, ShopConfigService configService) {
        Map<String, Object> config = configService.getMainShopConfig();
        String title = ChatColor.translateAlternateColorCodes('&', (String) config.getOrDefault("title", "ѕʜᴏᴘ"));
        int size = (int) config.getOrDefault("size", 27);

        Inventory inv = Bukkit.createInventory(null, size, title);

        Map<String, Map<String, Object>> categories = (Map<String, Map<String, Object>>) config.get("categories");
        if (categories != null) {
            for (Map.Entry<String, Map<String, Object>> entry : categories.entrySet()) {
                Map<String, Object> catData = entry.getValue();
                Material mat = Material.valueOf((String) catData.getOrDefault("material", "STONE"));
                int slot = (int) catData.get("slot");
                String name = ChatColor.translateAlternateColorCodes('&', (String) catData.getOrDefault("name", "&fCategory"));
                
                List<String> rawLore = (List<String>) catData.getOrDefault("lore", new ArrayList<>());
                List<String> lore = new ArrayList<>();
                for (String line : rawLore) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line));
                }

                ItemStack item = new ItemStack(mat);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(name);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
                inv.setItem(slot, item);
            }
        }

        player.openInventory(inv);
    }

    @SuppressWarnings("unchecked")
    public static void openCategoryShop(Player player, String categoryKey, ShopConfigService configService) {
        Map<String, Object> catConfig = configService.getCategoryConfig(categoryKey);
        if (catConfig == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCategory configuration not found!"));
            return;
        }

        String title = ChatColor.translateAlternateColorCodes('&', (String) catConfig.getOrDefault("title", "ѕʜᴏᴘ"));
        int size = (int) catConfig.getOrDefault("size", 27);

        Inventory inv = Bukkit.createInventory(null, size, title);

        Map<String, Map<String, Object>> items = (Map<String, Map<String, Object>>) catConfig.get("items");
        if (items != null) {
            for (Map.Entry<String, Map<String, Object>> entry : items.entrySet()) {
                Map<String, Object> itemData = entry.getValue();
                Material mat = Material.valueOf((String) itemData.getOrDefault("material", "STONE"));
                int slot = (int) itemData.get("slot");
                int price = (int) itemData.getOrDefault("price", 0);
                
                String name = itemData.containsKey("name") 
                    ? ChatColor.translateAlternateColorCodes('&', (String) itemData.get("name")) 
                    : null;

                List<String> rawLore = (List<String>) itemData.getOrDefault("lore", new ArrayList<>());
                List<String> lore = new ArrayList<>();
                for (String line : rawLore) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line.replace("%price%", String.valueOf(price))));
                }

                ItemStack item = new ItemStack(mat, (int) itemData.getOrDefault("amount", 1));
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    if (name != null) meta.setDisplayName(name);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
                inv.setItem(slot, item);
            }
        }

        Map<String, Object> backData = (Map<String, Object>) catConfig.get("back");
        if (backData != null) {
            Material mat = Material.valueOf((String) backData.getOrDefault("material", "RED_STAINED_GLASS_PANE"));
            int slot = (int) backData.get("slot");
            String name = ChatColor.translateAlternateColorCodes('&', (String) backData.getOrDefault("name", "&cBack"));
            
            List<String> rawLore = (List<String>) backData.getOrDefault("lore", new ArrayList<>());
            List<String> lore = new ArrayList<>();
            for (String line : rawLore) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }

            ItemStack backItem = new ItemStack(mat);
            ItemMeta meta = backItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(name);
                meta.setLore(lore);
                backItem.setItemMeta(meta);
            }
            inv.setItem(slot, backItem);
        }

        player.openInventory(inv);
    }
}
