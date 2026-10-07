package com.nyretha.pay.command;

import com.nyretha.pay.service.PayService;
import com.nyretha.shop.utils.EconomyManager;
import com.nyretha.shop.utils.HexColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final PayService payService;
    private final EconomyManager economyManager;

    public PayCommand(PayService payService, EconomyManager economyManager) {
        this.payService = payService;
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(HexColor.format("&cUsage: /pay <player> <amount>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage(HexColor.format("&cPlayer not found!"));
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(HexColor.format("&cYou can't pay yourself!"));
            return true;
        }

        if (payService.isPayDisabled(target)) {
            player.sendMessage(HexColor.format("&c" + target.getName() + " has disabled receiving payments!"));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(HexColor.format("&cInvalid number!"));
            return true;
        }

        if (!economyManager.getEconomy().has(player, amount)) {
            player.sendMessage(HexColor.format("&cYou don't have enough money!"));
            return true;
        }

        // Direct Execution
        economyManager.getEconomy().withdrawPlayer(player, amount);
        economyManager.getEconomy().depositPlayer(target, amount);

        payService.executeDirectPayment(player, target, amount);

        player.sendMessage(HexColor.format("&7You paid &#0bf52b" + amount + " &7to &#00A0FC" + target.getName()));
        target.sendMessage(HexColor.format("&7You received &#0bf52b" + amount + " &7from &#00A0FC" + player.getName()));

        return true;
    }
}
