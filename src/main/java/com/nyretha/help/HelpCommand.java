package com.nyretha.help;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class HelpCommand implements CommandExecutor, Listener {

    private final HelpConfig helpConfig;

    public HelpCommand(HelpConfig helpConfig) {
        this.helpConfig = helpConfig;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        openHelp(player);
        return true;
    }

    private void openHelp(Player player) {
        String title = color(
                helpConfig.getConfig().getString(
                        "title",
                        "&8ѕᴇʀᴠᴇʀ ɪɴꜰᴏ"
                )
        );

        int size = helpConfig.getConfig().getInt("size", 27);

        Inventory inventory = Bukkit.createInventory(null, size, title);

        if (helpConfig.getConfig().isConfigurationSection("items")) {
            for (String key : helpConfig.getConfig()
                    .getConfigurationSection("items")
                    .getKeys(false)) {

                String path = "items." + key;

                String materialName = helpConfig.getConfig()
                        .getString(path + ".material");

                if (materialName == null) {
                    continue;
                }

                Material material;

                try {
                    material = Material.valueOf(materialName.toUpperCase());
                } catch (IllegalArgumentException exception) {
                    continue;
                }

                int slot = helpConfig.getConfig()
                        .getInt(path + ".slot", -1);

                if (slot < 0 || slot >= size) {
                    continue;
                }

                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();

                if (meta == null) {
                    continue;
                }

                String name = helpConfig.getConfig()
                        .getString(path + ".name");

                if (name != null) {
                    meta.setDisplayName(color(name));
                }

                List<String> lore = helpConfig.getConfig()
                        .getStringList(path + ".lore");

                if (!lore.isEmpty()) {
                    meta.setLore(
                            lore.stream()
                                    .map(this::color)
                                    .toList()
                    );
                }

                item.setItemMeta(meta);
                inventory.setItem(slot, item);
            }
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = color(
                helpConfig.getConfig().getString(
                        "title",
                        "&8ѕᴇʀᴠᴇʀ ɪɴꜰᴏ"
                )
        );

        if (!event.getView().getTitle().equals(title)) {
            return;
        }

        event.setCancelled(true);
    }

    private String color(String message) {
        if (message == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {
            char character = message.charAt(i);

            if (character == '#' && i + 6 < message.length()) {
                String hex = message.substring(i + 1, i + 7);

                if (hex.matches("[A-Fa-f0-9]{6}")) {
                    result.append(ChatColor.COLOR_CHAR).append('x');

                    for (char hexCharacter : hex.toCharArray()) {
                        result.append(ChatColor.COLOR_CHAR)
                                .append(hexCharacter);
                    }

                    i += 6;
                    continue;
                }
            }

            result.append(character);
        }

        return ChatColor.translateAlternateColorCodes(
                '&',
                result.toString()
        );
    }
}
