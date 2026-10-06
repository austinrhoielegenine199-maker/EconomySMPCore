package com.nyretha.shop.listener;

import com.nyretha.NyrethaCore;
import com.nyretha.economy.service.EconomyConfigService;
import com.nyretha.shop.service.ShopConfigService;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShopMenuListener implements Listener {

    private final NyrethaCore plugin;
    private final ShopConfigService shopConfigService;
    private final EconomyConfigService ecoConfig;

    public ShopMenuListener(NyrethaCore plugin, ShopConfigService shopConfigService, EconomyConfigService ecoConfig) {
        this.plugin = plugin;
        this.shopConfigService = shopConfigService;
        this.ecoConfig = ecoConfig;
    }

    public static class ShopHolder implements InventoryHolder {
        private final String categoryFile; // null if Main Shop GUI

        public ShopHolder(String categoryFile) {
            this.categoryFile = categoryFile;
        }

        public String getCategoryFile() {
            return categoryFile;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public static void openMainShop(Player player, ShopConfigService configService) {
        FileConfiguration config = configService.getShopGuiConfig();
        if (config == null) return;

        String title = color(config.getString("title", "Shop"));
        int size = config.getInt("size", 27);

        Inventory inv = Bukkit.createInventory(new ShopHolder(null), size, title);

        ConfigurationSection categories = config.getConfigurationSection("categories");
        if (categories != null) {
            for (String key : categories.getKeys(false)) {
                int slot = categories.getInt(key + ".slot");
                String matStr = categories.getString(key + ".material", "STONE");
                String name = color(categories.getString(key + ".name", ""));
                List<String> lore = colorList(categories.getStringList(key + ".lore"));

                ItemStack item = createGuiItem(matStr, name, lore, 1);
                inv.setItem(slot, item);
            }
        }
        player.openInventory(inv);
    }

    public static void openCategoryShop(Player player, ShopConfigService configService, String fileName) {
        FileConfiguration config = configService.getCategoryConfig(fileName);
        if (config == null) {
            player.sendMessage(ChatColor.RED + "Category file not found: " + fileName);
            return;
        }

        String title = color(config.getString("title", "Shop"));
        int size = config.getInt("size", 27);

        Inventory inv = Bukkit.createInventory(new ShopHolder(fileName), size, title);

        // Load Items
        ConfigurationSection items = config.getConfigurationSection("items");
        if (items != null) {
            for (String key : items.getKeys(false)) {
                int slot = items.getInt(key + ".slot");
                String matStr = items.getString(key + ".material", "STONE");
                int amount = items.getInt(key + ".amount", 1);
                double price = items.getDouble(key + ".price", 0.0);

                String name = items.contains(key + ".name") ? color(items.getString(key + ".name")) : null;
                List<String> rawLore = items.getStringList(key + ".lore");
                List<String> lore = new ArrayList<>();
                for (String l : rawLore) {
                    lore.add(color(l.replace("%price%", String.valueOf((long) price))));
                }

                ItemStack item = createGuiItem(matStr, name, lore, amount);
                inv.setItem(slot, item);
            }
        }

        // Load Back Button
        if (config.contains("back")) {
            int backSlot = config.getInt("back.slot", 18);
            String backMat = config.getString("back.material", "RED_STAINED_GLASS_PANE");
            String backName = color(config.getString("back.name", "&cBack"));
            List<String> backLore = colorList(config.getStringList("back.lore"));

            inv.setItem(backSlot, createGuiItem(backMat, backName, backLore, 1));
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof ShopHolder holder)) return;

        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        int slot = event.getRawSlot();

        // If clicking inside Main Shop GUI
        if (holder.getCategoryFile() == null) {
            FileConfiguration mainConfig = shopConfigService.getShopGuiConfig();
            ConfigurationSection categories = mainConfig.getConfigurationSection("categories");
            if (categories == null) return;

            for (String key : categories.getKeys(false)) {
                if (categories.getInt(key + ".slot") == slot) {
                    String categoryFile = categories.getString(key + ".file");
                    if (categoryFile != null) {
                        openCategoryShop(player, shopConfigService, categoryFile);
                    }
                    return;
                }
            }
        } 
        // If clicking inside a Category Shop GUI
        else {
            String fileName = holder.getCategoryFile();
            FileConfiguration categoryConfig = shopConfigService.getCategoryConfig(fileName);
            if (categoryConfig == null) return;

            // Handle Back Button
            if (categoryConfig.contains("back") && slot == categoryConfig.getInt("back.slot")) {
                openMainShop(player, shopConfigService);
                return;
            }

            // Handle Purchase
            ConfigurationSection items = categoryConfig.getConfigurationSection("items");
            if (items == null) return;

            for (String key : items.getKeys(false)) {
                if (items.getInt(key + ".slot") == slot) {
                    double price = items.getDouble(key + ".price");
                    boolean isFlakes = categoryConfig.getBoolean("enable-flakes", false);

                    if (isFlakes) {
                        double currentFlakes = ecoConfig.getFlakes(player.getUniqueId());
                        if (currentFlakes < price) {
                            player.sendMessage(color("#FF5555You do not have enough Flakes!"));
                            return;
                        }
                        ecoConfig.removeFlakes(player.getUniqueId(), price);
                        player.sendMessage(color("#55FF55Purchased for " + (long) price + " Flakes!"));
                    } else {
                        double currentBalance = ecoConfig.getBalance(player.getUniqueId());
                        if (currentBalance < price) {
                            player.sendMessage(color("#FF5555You do not have enough Money!"));
                            return;
                        }
                        ecoConfig.removeBalance(player.getUniqueId(), price);
                        player.sendMessage(color("#55FF55Purchased for $" + (long) price + "!"));
                    }

                    // Execute Give Command or Give Item
                    if (items.contains(key + ".give-command")) {
                        String cmd = items.getString(key + ".give-command").replace("%player%", player.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    } else {
                        String matStr = items.getString(key + ".material", "DIRT");
                        int amount = items.getInt(key + ".amount", 1);
                        ItemStack item = new ItemStack(Material.matchMaterial(matStr.toUpperCase()), amount);
                        player.getInventory().addItem(item);
                    }
                    return;
                }
            }
        }
    }

    private static ItemStack createGuiItem(String matStr, String name, List<String> lore, int amount) {
        Material mat = Material.matchMaterial(matStr.toUpperCase());
        if (mat == null) mat = Material.STONE;

        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (name != null) meta.setDisplayName(name);
            if (lore != null) meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static String color(String text) {
        if (text == null) return "";
        Matcher matcher = Pattern.compile("#[a-fA-F0-9]{6}").matcher(text);
        while (matcher.find()) {
            String hexCode = text.substring(matcher.start(), matcher.end());
            text = text.replace(hexCode, ChatColor.of(hexCode).toString());
            matcher = Pattern.compile("#[a-fA-F0-9]{6}").matcher(text);
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> colorList(List<String> list) {
        List<String> colored = new ArrayList<>();
        for (String line : list) {
            colored.add(color(line));
        }
        return colored;
    }
}
