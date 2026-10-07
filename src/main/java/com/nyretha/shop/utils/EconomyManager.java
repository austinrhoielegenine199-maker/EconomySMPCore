package com.nyretha.shop.utils;

import com.nyretha.shop.Shop;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {

    private final Shop plugin;
    private Economy economy;

    public EconomyManager(Shop plugin) {
        this.plugin = plugin;
        setupEconomy();
    }

    private void setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) return;
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            this.economy = rsp.getProvider();
        }
    }

    public Economy getEconomy() { return economy; }
    public boolean hasEconomy() { return economy != null; }
}
