package com.nyretha.shop.listener;

import com.nyretha.shop.gui.ShopConfirmMenu;
import com.nyretha.shop.gui.ShopMenu;
import com.nyretha.shop.service.ShopConfigService;
import com.nyretha.shop.service.ShopTransactionService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ShopMenuListener implements Listener {
    private final ShopConfigService configService;
    private final ShopTransactionService transactionService;
    private final Map<UUIDSessionKey, PendingPurchase> activeSessions = new ConcurrentHashMap<>();

    public ShopMenuListener(ShopConfigService configService, Plugin plugin) {
        this.configService = configService;
        this.transactionService = new ShopTransactionService(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (title.equals(ChatColor.translateAlternateColorCodes('&', "ѕʜᴏᴘ"))) {
            event.setCancelled(true);
            int slot = event.getRawSlot();

            if (slot == 11) ShopMenu.openCategoryShop(player, "end", configService);
            else if (slot == 12) ShopMenu.openCategoryShop(player, "nether", configService);
            else if (slot == 13) ShopMenu.openCategoryShop(player, "gear", configService);
            else if (slot == 14) ShopMenu.openCategoryShop(player, "food", configService);
            else if (slot == 22) ShopMenu.openCategoryShop(player, "flake", configService);
            return;
        }

        if (title.contains("ѕʜᴏᴘ -")) {
            event.setCancelled(true);
            int slot = event.getRawSlot();

            if (slot == 18) {
                ShopMenu.openMainShop(player, configService);
                return;
            }

            String categoryKey = getCategoryFromTitle(title);
            Map<String, Object> catConfig = configService.getCategoryConfig(categoryKey);
            if (catConfig != null) {
                @SuppressWarnings("unchecked")
                Map<String, Map<String, Object>> items = (Map<String, Map<String, Object>>) catConfig.get("items");
                if (items != null) {
                    for (Map.Entry<String, Map<String, Object>> entry : items.entrySet()) {
                        Map<String, Object> itemData = entry.getValue();
                        if ((int) itemData.get("slot") == slot) {
                            int basePrice = (int) itemData.getOrDefault("price", 0);
                            int defaultAmount = (int) itemData.getOrDefault("amount", 1);
                            
                            activeSessions.put(new UUIDSessionKey(player.getUniqueId()), new PendingPurchase(itemData, defaultAmount, basePrice, categoryKey));
                            ShopConfirmMenu.open(player, itemData, defaultAmount, basePrice);
                            break;
                        }
                    }
                }
            }
            return;
        }

        if (title.equals(ChatColor.translateAlternateColorCodes('&', "&8Confirm Purchase"))) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            UUIDSessionKey sessionKey = new UUIDSessionKey(player.getUniqueId());
            PendingPurchase pending = activeSessions.get(sessionKey);

            if (pending == null) return;

            int currentAmount = pending.amount;

            if (slot == 9) currentAmount -= 64;
            else if (slot == 10) currentAmount -= 10;
            else if (slot == 11) currentAmount -= 1;
            else if (slot == 15) currentAmount += 1;
            else if (slot == 16) currentAmount += 10;
            else if (slot == 17) currentAmount += 64;

            if (slot >= 9 && slot <= 17 && slot != 13) {
                currentAmount = Math.max(1, Math.min(2304, currentAmount));
                pending.amount = currentAmount;
                ShopConfirmMenu.open(player, pending.itemData, currentAmount, pending.unitPrice);
                return;
            }

            if (slot == 21) {
                activeSessions.remove(sessionKey);
                ShopMenu.openCategoryShop(player, pending.categoryKey, configService);
                return;
            }

            if (slot == 23) {
                activeSessions.remove(sessionKey);
                transactionService.processPurchase(player, pending.itemData, pending.amount, pending.unitPrice, configService, pending.categoryKey);
            }
        }
    }

    private String getCategoryFromTitle(String title) {
        if (title.contains("ꜰʟᴀᴋᴇ")) return "flake";
        if (title.contains("ᴇɴᴅ")) return "end";
        if (title.contains("ɴᴇᴛʜᴇʀ")) return "nether";
        if (title.contains("ꜰᴏᴏᴅ")) return "food";
        return "gear";
    }

    private static class UUIDSessionKey {
        private final UUID uuid;
        public UUIDSessionKey(UUID uuid) { this.uuid = uuid; }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UUIDSessionKey)) return false;
            return uuid.equals(((UUIDSessionKey) o).uuid);
        }
        @Override public int hashCode() { return uuid.hashCode(); }
    }

    private static class PendingPurchase {
        final Map<String, Object> itemData;
        int amount;
        final int unitPrice;
        final String categoryKey;

        public PendingPurchase(Map<String, Object> itemData, int amount, int unitPrice, String categoryKey) {
            this.itemData = itemData;
            this.amount = amount;
            this.unitPrice = unitPrice;
            this.categoryKey = categoryKey;
        }
    }
}
