package com.nyretha.shop.models;

import com.nyretha.shop.Core;
import com.nyretha.shop.utils.HexColor;
import com.nyretha.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConfirmMenu {

    private static final Map<UUID, Long> CLICK_COOLDOWNS = new ConcurrentHashMap<>();
    private static final long COOLDOWN_MS = 250;

    public static void handleBuyClick(Shop plugin, Player player, ShopItem shopItem, int amount, InventoryClickEvent event) {
        event.setCancelled(true);

        long currentTime = System.currentTimeMillis();
        Long lastClick = CLICK_COOLDOWNS.get(player.getUniqueId());
        if (lastClick != null && (currentTime - lastClick) < COOLDOWN_MS) {
            return;
        }
        CLICK_COOLDOWNS.put(player.getUniqueId(), currentTime);

        double totalCost = shopItem.getPrice() * amount;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (plugin.getEconomy() == null) {
                Bukkit.getScheduler().runTask(plugin, () -> 
                    player.sendMessage(HexColor.format("&cEconomy system is currently unavailable.")));
                return;
            }

            if (!plugin.getEconomy().has(player, totalCost)) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.sendMessage(HexColor.format(plugin.getLangManager().get("messages.not-enough-money")));
                    player.closeInventory();
                });
                return;
            }

            plugin.getEconomy().withdrawPlayer(player, totalCost);

            Bukkit.getScheduler().runTask(plugin, () -> {
                String shortPlayerName = PlayerUtil.getShortName(player, 12);

                if (shopItem.getCommands() != null && !shopItem.getCommands().isEmpty()) {
                    for (String cmd : shopItem.getCommands()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
                            cmd.replace("%player%", player.getName())
                               .replace("%short_player%", shortPlayerName)
                               .replace("%amount%", String.valueOf(amount)));
                    }
                } else {
                    ItemStack itemToGive = shopItem.getItemStack().clone();
                    itemToGive.setAmount(amount);
                    Map<Integer, ItemStack> leftover = player.getInventory().addItem(itemToGive);
                    for (ItemStack drop : leftover.values()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), drop);
                    }
                }

                player.sendMessage(HexColor.format(
                    plugin.getLangManager().get("purchase-success.module.chat.message")
                        .replace("{item}", shopItem.getDisplayName())
                        .replace("{amount}", String.valueOf(amount))
                        .replace("{price}", String.valueOf(totalCost))
                ));
            });
        });
    }
}
