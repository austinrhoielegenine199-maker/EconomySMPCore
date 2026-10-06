package com.nyretha.shop.command;

import com.nyretha.shop.listener.ShopMenuListener;
import com.nyretha.shop.service.ShopConfigService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class ShopCommand implements CommandExecutor {

    private final ShopConfigService shopConfigService;
    private final File dataFolder;

    public ShopCommand(ShopConfigService shopConfigService, File dataFolder) {
        this.shopConfigService = shopConfigService;
        this.dataFolder = dataFolder;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        ShopMenuListener.openMainShop(player, shopConfigService);
        return true;
    }
}
