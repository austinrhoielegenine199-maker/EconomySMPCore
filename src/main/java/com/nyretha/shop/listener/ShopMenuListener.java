package com.nyretha.shop.listener;

import com.nyretha.shop.gui.ShopMenu;
import com.nyretha.shop.service.ShopConfigService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ShopMenuListener implements Listener {
    private final ShopConfigService configService;

    public ShopMenuListener(ShopConfigService configService) {
        this.configService = configService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (title.equals(ChatColor.translateAlternateColorCodes('&', "ѕʜᴏᴘ"))) {
            event.setCancelled(true);
            int slot = event.getRawSlot();

            if (slot == 11) {
                ShopMenu.openCategoryShop(player, "end", configService);
            } else if (slot == 12) {
                ShopMenu.openCategoryShop(player, "nether", configService);
            } else if (slot == 13) {
                ShopMenu.openCategoryShop(player, "gear", configService);
            } else if (slot == 14) {
                ShopMenu.openCategoryShop(player, "food", configService);
            } else if (slot == 22) {
                ShopMenu.openCategoryShop(player, "flake", configService);
            }
            return;
        }

        if (title.contains("ѕʜᴏᴘ -")) {
            event.setCancelled(true);
            int slot = event.getRawSlot();

            if (slot == 18) {
                ShopMenu.openMainShop(player, configService);
                return;
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aItem selected from shop category!"));
        }
    }
}
