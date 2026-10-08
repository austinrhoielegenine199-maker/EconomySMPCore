package com.nyretha.flakes.listener;

import com.nyretha.flakes.gui.FlakeViewGUI;
import com.nyretha.flakes.service.FlakesService;
import com.nyretha.shop.utils.HexColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class FlakesListener implements Listener {

    private final FlakesService flakesService;

    public FlakesListener(FlakesService flakesService) {
        this.flakesService = flakesService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();

        if (title.startsWith(HexColor.format("&8ꜱʜᴀʀᴅ"))) {
            event.setCancelled(true);
            int slot = event.getSlot();

            if (slot == 49) { // Refresh
                FlakeViewGUI.openGUI(player, flakesService, 1);
            }
        }
    }
}
