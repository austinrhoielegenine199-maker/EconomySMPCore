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
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlakesCommand implements CommandExecutor, TabCompleter {

    private final EconomyConfigService eco;

    public FlakesCommand(EconomyConfigService eco) {
        this.eco = eco;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ColorUtils.color("&cConsole usage: /flakes <give|take|set|reset> <player> <amount>"));
                return true;
            }
            double flakes = eco.getFlakes(player.getUniqueId());
            player.sendMessage(ColorUtils.color("&bFlakes: &a" + String.format("%.0f", flakes) + " ❄"));
            return true;
        }

        String action = args[0].toLowerCase();

        // Player /flakes pay <player> <amount> or alias /shards pay
        if (action.equals("pay")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ColorUtils.color("&cConsole cannot pay flakes. Use /flakes give <player> <amount>"));
                return true;
            }
            if (args.length < 3) {
                player.sendMessage(ColorUtils.color("&cUsage: /flakes pay <player> <amount>"));
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if (!target.hasPlayedBefore() && !target.isOnline()) {
                player.sendMessage(ColorUtils.color("&cPlayer not found."));
                return true;
            }

            double amount;
            try {
                amount = NumberParser.parseAmount(args[2]);
                if (amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                player.sendMessage(ColorUtils.color("&cInvalid amount."));
                return true;
            }

            if (eco.getFlakes(player.getUniqueId()) < amount) {
                player.sendMessage(ColorUtils.color("&cYou don't have enough flakes!"));
                return true;
            }

            eco.removeFlakes(player.getUniqueId(), amount);
            eco.addFlakes(target.getUniqueId(), amount);

            String targetName = target.getName() != null ? target.getName() : args[1];
            player.sendMessage(ColorUtils.color("&aPaid &b" + (long) amount + " ❄ flakes &ato &b" + targetName));
            if (target.isOnline() && target.getPlayer() != null) {
                target.getPlayer().sendMessage(ColorUtils.color("&aReceived &b" + (long) amount + " ❄ flakes &afrom &b" + player.getName()));
            }
            return true;
        }

        // Admin Management Actions: give, take, set, reset
        if (!sender.hasPermission("nyrethacore.admin.flakes")) {
            sender.sendMessage(ColorUtils.color("&cNo permission."));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ColorUtils.color("&cUsage: /flakes <give|take|set|reset> <player> [amount]"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        String targetName = target.getName() != null ? target.getName() : args[1];

        if (action.equals("reset")) {
            eco.setFlakes(target.getUniqueId(), 0.0);
            sender.sendMessage(ColorUtils.color("&aReset flakes balance for &b" + targetName));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ColorUtils.color("&cSpecify an amount: /flakes " + action + " <player> <amount>"));
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
                eco.addFlakes(target.getUniqueId(), amount);
                sender.sendMessage(ColorUtils.color("&aGave &b" + (long) amount + " ❄ flakes &ato &b" + targetName));
            }
            case "take" -> {
                eco.removeFlakes(target.getUniqueId(), amount);
                sender.sendMessage(ColorUtils.color("&aTook &b" + (long) amount + " ❄ flakes &afrom &b" + targetName));
            }
            case "set" -> {
                eco.setFlakes(target.getUniqueId(), amount);
                sender.sendMessage(ColorUtils.color("&aSet &b" + targetName + "'s &aflakes balance to &b" + (long) amount + " ❄"));
            }
            default -> sender.sendMessage(ColorUtils.color("&cUnknown action: " + action));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>(Arrays.asList("pay"));
            if (sender.hasPermission("nyrethacore.admin.flakes")) {
                list.addAll(Arrays.asList("give", "take", "set", "reset"));
            }
            return list;
        } else if (args.length == 2) {
            return null; // Suggests online players
        } else if (args.length == 3 && !args[0].equalsIgnoreCase("reset")) {
            return Arrays.asList("1k", "1m", "1b", "1t");
        }
        return new ArrayList<>();
    }
}
