package com.nyretha.sell.manager;

import com.nyretha.shop.utils.EconomyManager;
import com.nyretha.shop.utils.HexColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellManager {

    private final WorthManager worthManager;
    private final EconomyManager economyManager;

    public SellManager(WorthManager worthManager, EconomyManager economyManager) {
        this.worthManager = worthManager;
        this.economyManager = economyManager;
    }

    public void sellInventory(Player player) {
        double totalWorth = 0.0;
        int itemsSold = 0;

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;

            double worth = worthManager.getItemWorth(item);
            if (worth > 0) {
                totalWorth += worth;
                itemsSold += item.getAmount();
                player.getInventory().setItem(i, null);
            }
        }

        if (totalWorth > 0) {
            economyManager.getEconomy().depositPlayer(player, totalWorth);
            player.sendMessage(HexColor.format("&7[&#E0E319Sell&7] Sold &a" + itemsSold + " &7items for &#0bf52b$" + totalWorth));
        } else {
            player.sendMessage(HexColor.format("&#FF0000You have no sellable items!"));
        }
    }
}
