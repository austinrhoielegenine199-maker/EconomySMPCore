package com.nyretha.rtp.listener;

import com.nyretha.shop.utils.HexColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class RTPListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();

        if (title.equals(HexColor.format("&8ʀᴀɴᴅᴏᴍ ᴛᴇʟᴇᴘᴏʀᴛ")) || title.equals(HexColor.format("&8ʀᴛᴘqᴜᴇᴜᴇ"))) {
            event.setCancelled(true);
        }
    }
}
