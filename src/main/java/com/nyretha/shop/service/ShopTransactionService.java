package com.nyretha.shop.service;

import com.nyretha.shop.gui.ShopMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class ShopTransactionService {
    private final ShopClickThrottle throttle = new ShopClickThrottle();
    private final Plugin plugin;

    public ShopTransactionService(Plugin plugin) {
        this.plugin = plugin;
    }

    public void processPurchase(Player player, Map<String, Object> itemData, int amount, int unitPrice, ShopConfigService configService, String categoryKey) {
        if (!throttle.canClick(player.getUniqueId())) {
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour inventory is full!"));
            return;
        }

        int totalPrice = unitPrice * amount;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean hasEnoughMoney = true; // Placeholder for economy check

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (!hasEnoughMoney) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have enough money!"));
                    return;
                }

                String materialName = (String) itemData.getOrDefault("material", "STONE");
                ItemStack itemToGive = new ItemStack(org.bukkit.Material.valueOf(materialName), amount);

                player.getInventory().addItem(itemToGive);
                
                // Formatted in light green (&a) as requested
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have bought " + amount + "x " + materialName + "!"));
                
                ShopMenu.openCategoryShop(player, categoryKey, configService);
            });
        });
    }
}
