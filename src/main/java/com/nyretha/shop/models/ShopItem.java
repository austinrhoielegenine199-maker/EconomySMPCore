package com.nyretha.shop.models;

import org.bukkit.inventory.ItemStack;
import java.util.List;

public class ShopItem {
    private final String id;
    private final String displayName;
    private final ItemStack itemStack;
    private final double price;
    private final int flakePrice;
    private final int slot;
    private final int page;
    private final List<String> commands;

    public ShopItem(String id, String displayName, ItemStack itemStack, double price, int flakePrice, int slot, int page, List<String> commands) {
        this.id = id;
        this.displayName = displayName;
        this.itemStack = itemStack;
        this.price = price;
        this.flakePrice = flakePrice;
        this.slot = slot;
        this.page = page;
        this.commands = commands;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public ItemStack getItemStack() { return itemStack; }
    public double getPrice() { return price; }
    public int getFlakePrice() { return flakePrice; }
    public int getSlot() { return slot; }
    public int getPage() { return page; }
    public List<String> getCommands() { return commands; }
}
