package com.nyretha.economy.command;

import com.nyretha.economy.service.EconomyConfigService;
import com.nyretha.utils.ColorUtils;
import com.nyretha.utils.NumberParser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EconomyAdminCommand implements CommandExecutor, TabCompleter {

    private final EconomyConfigService eco;

    public EconomyAdminCommand(EconomyConfigService eco) {
        this.eco = eco;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nyrethacore.admin.eco")) {
            sender.sendMessage(ColorUtils.color("&cNo permission."));
            return true;
        }

        if (args.length < 3 && !args[0].equalsIgnoreCase("reset")) {
            sender.sendMessage(ColorUtils.color("&cUsage: /eco <give|take|set|reset> <player> [amount]"));
            return true;
        }

        String action = args[0].toLowerCase();
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        String targetName = target.getName() != null ? target.getName() : args[1];

        if (action.equals("reset")) {
            eco.setBalance(target.getUniqueId(), 0.0);
            sender.sendMessage(ColorUtils.color("&aReset money balance for &b" + targetName));
            return true;
        }

        double amount;
        try {
            amount = NumberParser.parseAmount(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ColorUtils.color("&cInvalid amount specified."));
            return true;
        }

        switch (action) {
            case "give" -> {
                eco.addBalance(target.getUniqueId(), amount);
                sender.sendMessage(ColorUtils.color("&aGave &b$" + String.format("%.2f", amount) + " &ato &b" + targetName));
            }
            case "take" -> {
                eco.removeBalance(target.getUniqueId(), amount);
                sender.sendMessage(ColorUtils.color("&aTook &b$" + String.format("%.2f", amount) + " &afrom &b" + targetName));
            }
            case "set" -> {
                eco.setBalance(target.getUniqueId(), amount);
                sender.sendMessage(ColorUtils.color("&aSet &b" + targetName + "'s &amoney balance to &b$" + String.format("%.2f", amount)));
            }
            default -> sender.sendMessage(ColorUtils.color("&cUnknown action: " + action));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("give", "take", "set", "reset");
        } else if (args.length == 2) {
            return null; // Suggests online players
        } else if (args.length == 3 && !args[0].equalsIgnoreCase("reset")) {
            return Arrays.asList("1k", "1m", "1b", "1t");
        }
        return new ArrayList<>();
    }
}
