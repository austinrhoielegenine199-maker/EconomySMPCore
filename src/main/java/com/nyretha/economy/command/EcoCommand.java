package com.nyretha.economy.command;

import com.nyretha.economy.service.EconomyConfigService;
import com.nyretha.economy.util.NumberFormatter;
import com.nyretha.economy.util.PlayerMatcher;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EcoCommand implements CommandExecutor {
    private final Economy economy;
    private final EconomyConfigService configService;

    public EcoCommand(Economy economy, EconomyConfigService configService) {
        this.economy = economy;
        this.configService = configService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("nyretha.admin.eco")) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§cUsage: /eco <give|take|set|reset> <player> <amount>");
            return true;
        }

        String action = args[0].toLowerCase();
        Player onlineTarget = PlayerMatcher.matchPlayer(args[1]);
        OfflinePlayer target = onlineTarget != null ? onlineTarget : org.bukkit.Bukkit.getOfflinePlayer(args[1]);

        if (target == null || (target.getName() == null && !target.hasPlayedBefore())) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        double amount = 0.0;
        if (!action.equals("reset")) {
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount specified.");
                return true;
            }
        }

        String symbol = configService.getCurrencySymbol();
        boolean useFormatting = configService.isNumberFormattingEnabled();
        String formattedAmount = NumberFormatter.format(amount, symbol, useFormatting);

        switch (action) {
            case "give":
                economy.depositPlayer(target, amount);
                sender.sendMessage("§aGave §e" + formattedAmount + " §apä to §e" + target.getName());
                break;
            case "take":
                economy.withdrawPlayer(target, amount);
                sender.sendMessage("§cTook §e" + formattedAmount + " §afrom §e" + target.getName());
                break;
            case "set":
                double current = economy.getBalance(target);
                if (current < amount) {
                    economy.depositPlayer(target, amount - current);
                } else {
                    economy.withdrawPlayer(target, current - amount);
                }
                sender.sendMessage("§aSet §e" + target.getName() + "'s §abalance to §e" + formattedAmount);
                break;
            case "reset":
                double defaultBal = configService.getStartingBalance();
                double cur = economy.getBalance(target);
                if (cur < defaultBal) {
                    economy.depositPlayer(target, defaultBal - cur);
                } else {
                    economy.withdrawPlayer(target, cur - defaultBal);
                }
                String formattedDefault = NumberFormatter.format(defaultBal, symbol, useFormatting);
                sender.sendMessage("§aReset §e" + target.getName() + "'s §abalance to default (" + formattedDefault + ")");
                break;
            default:
                sender.sendMessage("§cUnknown action. Use: give, take, set, reset");
                break;
        }

        return true;
    }
}
