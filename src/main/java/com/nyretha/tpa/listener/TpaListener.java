package com.nyretha.tpa.listener;

import com.nyretha.shop.utils.HexColor;
import com.nyretha.tpa.model.TPARequest;
import com.nyretha.tpa.service.TPAService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TPAListener implements Listener {

    private final TPAService tpaService;

    public TPAListener(TPAService tpaService) {
        this.tpaService = tpaService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();

        if (title.equals(HexColor.format("&8ᴛᴘᴀ ᴀᴄᴄᴇᴘᴛ")) || title.equals(HexColor.format("&8ᴛᴘᴀ ʜᴇʀᴇ ᴀᴄᴄᴇᴘᴛ"))) {
            event.setCancelled(true);

            int slot = event.getSlot();
            TPARequest request = tpaService.getRequest(player.getUniqueId());

            if (request == null) {
                player.closeInventory();
                player.sendMessage(HexColor.format("&cThe TPA request has expired!"));
                return;
            }

            if (slot == 16) { // Confirm Button
                Player sender = request.getSender();
                if (sender != null && sender.isOnline()) {
                    if (request.isHere()) {
                        player.teleport(sender.getLocation());
                    } else {
                        sender.teleport(player.getLocation());
                    }
                    player.sendMessage(HexColor.format("&fYou accepted the TPA request!"));
                    sender.sendMessage(HexColor.format("&fYour TPA request was accepted!"));
                }
                tpaService.removeRequest(player.getUniqueId());
                player.closeInventory();
            } else if (slot == 10) { // Cancel Button
                player.sendMessage(HexColor.format("&cYou denied the TPA request!"));
                tpaService.removeRequest(player.getUniqueId());
                player.closeInventory();
            }
        }
    }
}
