package com.nyretha.sell.listener;

import com.nyretha.sell.manager.SellManager;
import com.nyretha.shop.utils.HexColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SellListener implements Listener {

    private final SellManager sellManager;

    public SellListener(SellManager sellManager) {
        this.sellManager = sellManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();

        if (title.startsWith(HexColor.format("&8ꜱᴇʟʟ ᴍᴇɴᴜ"))) {
            event.setCancelled(true);
            int slot = event.getSlot();

            if (slot == 13) {
                player.closeInventory();
                sellManager.sellInventory(player);
            }
        }
    }
}
