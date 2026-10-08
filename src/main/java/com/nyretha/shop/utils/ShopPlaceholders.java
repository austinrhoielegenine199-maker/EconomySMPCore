package com.nyretha.shop.utils;

import com.nyretha.shop.Core;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class ShopPlaceholders extends PlaceholderExpansion {

    private final Shop plugin;

    public ShopPlaceholders(Shop plugin) {
        this.plugin = plugin;
    }

    @Override public @NotNull String getIdentifier() { return "nyrethashop"; }
    @Override public @NotNull String getAuthor() { return "Nyretha"; }
    @Override public @NotNull String getVersion() { return "1.0.0"; }
    @Override public boolean persist() { return true; }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        if (params.equalsIgnoreCase("flakes")) {
            return String.valueOf(plugin.getFlakesManager().getFlakes(player.getUniqueId()));
        }

        return null;
    }
}
