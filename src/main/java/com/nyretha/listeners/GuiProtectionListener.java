package com.nyretha.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class GuiProtectionListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.contains("Select Amount") || title.contains("Homes") || title.contains("Team") || title.contains("Shop")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (title.contains("Select Amount") || title.contains("Homes") || title.contains("Team") || title.contains("Shop")) {
            event.setCancelled(true);
        }
    }
}
