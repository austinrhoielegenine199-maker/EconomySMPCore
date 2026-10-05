package com.nyretha.shop.command;

import com.nyretha.shop.gui.ShopMenu;
import com.nyretha.shop.service.ShopConfigService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShopCommand implements CommandExecutor {
    private final ShopConfigService configService;
    private final File dataFolder;

    public ShopCommand(ShopConfigService configService, File dataFolder) {
        this.configService = configService;
        this.dataFolder = dataFolder;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use shop commands.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            ShopMenu.openMainShop(player, configService);
            return true;
        }

        if (args[0].equalsIgnoreCase("addhanditem")) {
            if (!player.hasPermission("nyretha.shop.admin")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have permission to use this command."));
                return true;
            }

            if (args.length < 4) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /shop addhanditem <category> <slot> <price>"));
                return true;
            }

            String categoryName = args[1].toLowerCase();
            int slot;
            int price;

            try {
                slot = Integer.parseInt(args[2]);
                price = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSlot and price must be valid numbers!"));
                return true;
            }

            ItemStack itemInHand = player.getItemInHand();
            if (itemInHand == null || itemInHand.getType() == Material.AIR) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must be holding an item in your hand!"));
                return true;
            }

            boolean success = saveItemToCategoryFile(categoryName, itemInHand, slot, price);
            if (success) {
                configService.loadConfigs();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully added &e" + itemInHand.getType().name() + " &ato category &e" + categoryName + " &aat slot &e" + slot + " &afor &e$" + price));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFailed to save item. Make sure the category exists (e.g. gear, flake, end, nether, food)."));
            }
            return true;
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUnknown shop subcommand. Use /shop"));
        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean saveItemToCategoryFile(String categoryName, ItemStack item, int slot, int price) {
        File categoryFile = new File(dataFolder, "shop/categories/" + categoryName + ".yml");
        if (!categoryFile.exists()) {
            if (categoryName.equals("flake") || categoryName.equals("flakeshop")) {
                categoryFile = new File(dataFolder, "shop/categories/flakeshop.yml");
            } else {
                return false;
            }
        }

        Yaml yaml = new Yaml();
        Map<String, Object> rootData;

        try (InputStream in = new FileInputStream(categoryFile)) {
            rootData = yaml.load(in);
        } catch (Exception e) {
            rootData = new LinkedHashMap<>();
        }

        if (rootData == null) {
            rootData = new LinkedHashMap<>();
        }

        Map<String, Object> itemsMap = (Map<String, Object>) rootData.get("items");
        if (itemsMap == null) {
            itemsMap = new LinkedHashMap<>();
            rootData.put("items", itemsMap);
        }

        String itemKey = item.getType().name().toLowerCase();

        Map<String, Object> itemDetails = new LinkedHashMap<>();
        itemDetails.put("material", item.getType().name());
        itemDetails.put("slot", slot);
        itemDetails.put("amount", item.getAmount());
        itemDetails.put("price", price);
        itemDetails.put("lore", Arrays.asList("#FFFFFFBuy price: #55FF55%price%"));

        itemsMap.put(itemKey, itemDetails);

        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml dumpYaml = new Yaml(options);

        try (FileWriter writer = new FileWriter(categoryFile)) {
            dumpYaml.dump(rootData, writer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
