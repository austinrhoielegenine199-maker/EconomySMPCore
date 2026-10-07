package com.nyretha.sell.command;

import com.nyretha.sell.manager.SellAxeManager;
import com.nyretha.shop.utils.HexColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellAxeCommand implements CommandExecutor {

    private final SellAxeManager axeManager;

    public SellAxeCommand(SellAxeManager axeManager) {
        this.axeManager = axeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sell.sellaxe")) {
            sender.sendMessage(HexColor.format("&#FF0000You do not have permission for this!"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(HexColor.format("&cUsage: /sellaxe <player>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(HexColor.format("&#FF0000Player not found!"));
            return true;
        }

        ItemStack axe = axeManager.createSellAxe();
        target.getInventory().addItem(axe);

        sender.sendMessage(HexColor.format("&#0bf52bSuccess! Gave Flake Sell Axe to " + target.getName()));
        target.sendMessage(HexColor.format("&#0bf52bYou have received a Flake Sell Axe!"));

        return true;
    }
}
