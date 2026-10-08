package com.nyretha.shop.models;

import com.nyretha.Core;
import com.nyretha.shop.utils.HexColor;
import com.nyretha.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FlakeConfirmMenu {

    private static final Map<UUID, Long> CLICK_COOLDOWNS = new ConcurrentHashMap<>();
    private static final long COOLDOWN_MS = 250;

    public static void handleFlakeBuyClick(Core plugin, Player player, ShopItem shopItem, int amount, InventoryClickEvent event) {
        event.setCancelled(true);

        long currentTime = System.currentTimeMillis();
        Long lastClick = CLICK_COOLDOWNS.get(player.getUniqueId());
        if (lastClick != null && (currentTime - lastClick) < COOLDOWN_MS) {
            return;
        }
        CLICK_COOLDOWNS.put(player.getUniqueId(), currentTime);

        int totalFlakeCost = shopItem.getFlakePrice() * amount;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int currentFlakes = plugin.getFlakesManager().getFlakes(player.getUniqueId());

            if (currentFlakes < totalFlakeCost) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.sendMessage(HexColor.format(plugin.getLangManager().get("messages.not-enough-flakes")));
                    player.closeInventory();
                });
                return;
            }

            plugin.getFlakesManager().removeFlakes(player.getUniqueId(), totalFlakeCost);

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
                    plugin.getLangManager().get("purchase-flakes.module.chat.message")
                        .replace("{item}", shopItem.getDisplayName())
                        .replace("{amount}", String.valueOf(amount))
                        .replace("{price}", String.valueOf(totalFlakeCost))
                ));
            });
        });
    }
}
