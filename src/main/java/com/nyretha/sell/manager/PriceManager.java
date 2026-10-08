package com.nyretha.sell.manager;

import com.nyretha.sell.model.PriceModel;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface PriceManager {
    void loadPrices();
    PriceModel getPrice(ItemStack item);
    Map<String, PriceModel> getAllPrices();
}
