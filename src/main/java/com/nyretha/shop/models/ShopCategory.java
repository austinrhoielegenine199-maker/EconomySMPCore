package com.nyretha.shop.models;

import org.bukkit.inventory.ItemStack;
import java.util.Map;

public class ShopCategory {
    private final String id;
    private final String title;
    private final int rows;
    private final ItemStack icon;
    private final Map<Integer, ShopItem> items;

    public ShopCategory(String id, String title, int rows, ItemStack icon, Map<Integer, ShopItem> items) {
        this.id = id;
        this.title = title;
        this.rows = rows;
        this.icon = icon;
        this.items = items;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public int getRows() { return rows; }
    public ItemStack getIcon() { return icon; }
    public Map<Integer, ShopItem> getItems() { return items; }
}
