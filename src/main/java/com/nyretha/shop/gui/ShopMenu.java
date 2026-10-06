package com.nyretha.shop.gui;

import com.nyretha.shop.service.ShopConfigService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopMenu {

    public static void openMainShop(Player player, ShopConfigService configService) {
        FileConfiguration config = configService.getMainShopConfig();
        if (config == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cShop configuration not loaded!"));
            return;
        }

        String title = ChatColor.translateAlternateColorCodes('&', config.getString("title", "ѕʜᴏᴘ"));
        int size = config.getInt("size", 27);

        Inventory inv = Bukkit.createInventory(null, size, title);

        ConfigurationSection categoriesSection = config.getConfigurationSection("categories");
        if (categoriesSection != null) {
            for (String key : categoriesSection.getKeys(false)) {
                ConfigurationSection catData = categoriesSection.getConfigurationSection(key);
                if (catData == null) continue;

                Material mat = Material.valueOf(catData.getString("material", "STONE"));
                int slot = catData.getInt("slot", 0);
                String name = ChatColor.translateAlternateColorCodes('&', catData.getString("name", "&fCategory"));

                List<String> rawLore = catData.getStringList("lore");
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

    public static void openCategoryShop(Player player, String categoryKey, ShopConfigService configService) {
        FileConfiguration catConfig = configService.getCategoryConfig(categoryKey);
        if (catConfig == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCategory configuration not found!"));
            return;
        }

        String title = ChatColor.translateAlternateColorCodes('&', catConfig.getString("title", "ѕʜᴏᴘ"));
        int size = catConfig.getInt("size", 27);

        Inventory inv = Bukkit.createInventory(null, size, title);

        ConfigurationSection itemsSection = catConfig.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemData = itemsSection.getConfigurationSection(key);
                if (itemData == null) continue;

                Material mat = Material.valueOf(itemData.getString("material", "STONE"));
                int slot = itemData.getInt("slot", 0);
                int price = itemData.getInt("price", 0);
                int amount = itemData.getInt("amount", 1);

                String name = itemData.contains("name") 
                    ? ChatColor.translateAlternateColorCodes('&', itemData.getString("name")) 
                    : null;

                List<String> rawLore = itemData.getStringList("lore");
                List<String> lore = new ArrayList<>();
                for (String line : rawLore) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line.replace("%price%", String.valueOf(price))));
                }

                ItemStack item = new ItemStack(mat, amount);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    if (name != null) meta.setDisplayName(name);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
                inv.setItem(slot, item);
            }
        }

        ConfigurationSection backData = catConfig.getConfigurationSection("back");
        if (backData != null) {
            Material mat = Material.valueOf(backData.getString("material", "RED_STAINED_GLASS_PANE"));
            int slot = backData.getInt("slot", size - 1);
            String name = ChatColor.translateAlternateColorCodes('&', backData.getString("name", "&cBack"));

            List<String> rawLore = backData.getStringList("lore");
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
