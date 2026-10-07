package com.nyretha.pay.listener;

import com.nyretha.pay.gui.PayHistoryGUI;
import com.nyretha.pay.service.PayService;
import com.nyretha.shop.utils.HexColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PayListener implements Listener {

    private final PayService payService;

    public PayListener(PayService payService) {
        this.payService = payService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();

        if (title.startsWith(HexColor.format("&8ᴘᴀʏᴍᴇɴᴛ ʟᴏɢꜱ"))) {
            event.setCancelled(true);
            int slot = event.getSlot();

            if (slot == 49) { // Refresh Button
                PayHistoryGUI.openGUI(player, player, payService, 1);
            }
        }
    }
}
