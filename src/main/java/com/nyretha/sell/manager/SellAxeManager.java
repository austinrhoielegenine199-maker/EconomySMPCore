package com.nyretha.sell.manager;

import com.nyretha.shop.utils.HexColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SellAxeManager {

    private final JavaPlugin plugin;
    private final NamespacedKey axeKey;
    private FileConfiguration axeConfig;

    public SellAxeManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.axeKey = new NamespacedKey(plugin, "flake_sell_axe");
        loadConfig();
    }

    public void loadConfig() {
        File file = new File(plugin.getDataFolder(), "core/sell/sellaxe.yml");
        if (!file.exists()) {
            plugin.saveResource("core/sell/sellaxe.yml", false);
        }
        this.axeConfig = YamlConfiguration.loadConfiguration(file);
    }

    public ItemStack createSellAxe() {
        String matName = axeConfig.getString("material", "NETHERITE_AXE");
        Material material = Material.matchMaterial(matName);
        if (material == null) material = Material.NETHERITE_AXE;

        ItemStack axe = new ItemStack(material);
        ItemMeta meta = axe.getItemMeta();

        if (meta != null) {
            String displayName = axeConfig.getString("displayname", "&#A20AD6ꜰʟᴀᴋᴇ ꜱᴇʟʟ ᴀxᴇ");
            meta.setDisplayName(HexColor.format(displayName));

            List<String> rawLore = axeConfig.getStringList("lore");
            List<String> formattedLore = new ArrayList<>();
            String timeStr = axeConfig.getString("time", "3d");

            for (String line : rawLore) {
                formattedLore.add(HexColor.format(line.replace("%time%", timeStr)));
            }
            meta.setLore(formattedLore);

            if (axeConfig.getBoolean("enchantments.enabled", true)) {
                List<String> enchants = axeConfig.getStringList("enchantments.list");
                for (String enchantEntry : enchants) {
                    String[] parts = enchantEntry.split(":");
                    if (parts.length == 2) {
                        Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(parts[0].toLowerCase()));
                        if (ench != null) {
                            try {
                                int level = Integer.parseInt(parts[1]);
                                meta.addEnchant(ench, level, true);
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }

            if (axeConfig.getBoolean("glow", true)) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            if (axeConfig.contains("custom-model-data")) {
                meta.setCustomModelData(axeConfig.getInt("custom-model-data"));
            }

            meta.getPersistentDataContainer().set(axeKey, PersistentDataType.BYTE, (byte) 1);
            axe.setItemMeta(meta);
        }

        return axe;
    }

    public boolean isSellAxe(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(axeKey, PersistentDataType.BYTE);
    }
}
