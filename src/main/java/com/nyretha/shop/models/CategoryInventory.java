package com.nyretha.shop.models;

import com.nyretha.shop.utils.HexColor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CategoryInventory implements InventoryHolder {

    private final ShopCategory category;
    private final Inventory inventory;

    public CategoryInventory(ShopCategory category) {
        this.category = category;
        this.inventory = Bukkit.createInventory(this, category.getRows() * 9, HexColor.format(category.getTitle()));
        populate();
    }

    private void populate() {
        category.getItems().forEach((slot, item) -> {
            if (slot >= 0 && slot < inventory.getSize()) {
                inventory.setItem(slot, item.getItemStack());
            }
        });
    }

    public ShopCategory getCategory() { return category; }

    @Override
    public Inventory getInventory() { return inventory; }
}
