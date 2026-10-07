package com.nyretha.sell.command;

import com.nyretha.sell.manager.SellManager;
import com.nyretha.shop.utils.HexColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SellCommand implements CommandExecutor {

    private final SellManager sellManager;

    public SellCommand(SellManager sellManager) {
        this.sellManager = sellManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        if (!player.hasPermission("sell.use")) {
            player.sendMessage(HexColor.format("&#FF0000You do not have permission for this!"));
            return true;
        }

        sellManager.sellInventory(player);
        return true;
    }
}
