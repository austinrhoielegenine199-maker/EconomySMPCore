package com.nyretha.sell.manager;

import com.nyretha.sell.model.PriceModel;
import org.bukkit.inventory.ItemStack;

public class WorthManager {

    private final PriceManager priceManager;

    public WorthManager(PriceManager priceManager) {
        this.priceManager = priceManager;
    }

    public double getItemWorth(ItemStack item) {
        if (item == null) return 0.0;
        PriceModel model = priceManager.getPrice(item);
        if (model == null) return 0.0;

        return model.getPrice() * item.getAmount();
    }
}
