package com.nyretha.economy.command;

import com.nyretha.NyrethaCore;
import com.nyretha.economy.service.EconomyConfigService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EconomyCommand implements CommandExecutor, TabCompleter {

    private final NyrethaCore plugin;
    private final EconomyConfigService eco;

    public EconomyCommand(NyrethaCore plugin, EconomyConfigService eco) {
        this.plugin = plugin;
        this.eco = eco;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = label.toLowerCase();

        // /bal or /balance
        if (cmd.equals("bal") || cmd.equals("balance")) {
            if (args.length == 0) {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can check their own balance.");
                    return true;
                }
                double money = eco.getBalance(player.getUniqueId());
                double flakes = eco.getFlakes(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Balance: $" + String.format("%.2f", money));
                player.sendMessage(ChatColor.AQUA + "Flakes: " + String.format("%.0f", flakes));
                return true;
            } else {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                if (!target.hasPlayedBefore() && !target.isOnline()) {
                    sender.sendMessage(ChatColor.RED + "Player never joined the server.");
                    return true;
                }
                double money = eco.getBalance(target.getUniqueId());
                double flakes = eco.getFlakes(target.getUniqueId());
                sender.sendMessage(ChatColor.GREEN + target.getName() + "'s Balance: $" + String.format("%.2f", money));
                sender.sendMessage(ChatColor.AQUA + target.getName() + "'s Flakes: " + String.format("%.0f", flakes));
                return true;
            }
        }

        // /pay <player> <amount> [flakes]
        if (cmd.equals("pay")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Only players can send money.");
                return true;
            }
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /pay <player> <amount> [flakes]");
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore() && !target.isOnline()) {
                player.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            if (target.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You cannot pay yourself!");
                return true;
            }

            double amount;
            try {
                amount = Double.parseDouble(args[1]);
                if (amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid amount.");
                return true;
            }

            boolean isFlakes = args.length >= 3 && args[2].equalsIgnoreCase("flakes");

            if (isFlakes) {
                if (eco.getFlakes(player.getUniqueId()) < amount) {
                    player.sendMessage(ChatColor.RED + "You don't have enough flakes!");
                    return true;
                }
                eco.removeFlakes(player.getUniqueId(), amount);
                eco.addFlakes(target.getUniqueId(), amount);
                player.sendMessage(ChatColor.GREEN + "Paid " + (long) amount + " flakes to " + target.getName());
                if (target.isOnline()) {
                    ((Player) target).sendMessage(ChatColor.GREEN + "Received " + (long) amount + " flakes from " + player.getName());
                }
            } else {
                if (eco.getBalance(player.getUniqueId()) < amount) {
                    player.sendMessage(ChatColor.RED + "You don't have enough money!");
                    return true;
                }
                eco.removeBalance(player.getUniqueId(), amount);
                eco.addBalance(target.getUniqueId(), amount);
                player.sendMessage(ChatColor.GREEN + "Paid $" + String.format("%.2f", amount) + " to " + target.getName());
                if (target.isOnline()) {
                    ((Player) target).sendMessage(ChatColor.GREEN + "Received $" + String.format("%.2f", amount) + " from " + player.getName());
                }
            }
            return true;
        }

        // Admin command: /eco <give/take/set> <player> <amount> [money/flakes]
        if (cmd.equals("eco")) {
            if (!sender.hasPermission("nyrethacore.admin.eco")) {
                sender.sendMessage(ChatColor.RED + "No permission.");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /eco <give|take|set> <player> <amount> [money|flakes]");
                return true;
            }

            String action = args[0].toLowerCase();
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            
            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid amount.");
                return true;
            }

            boolean isFlakes = args.length >= 4 && args[3].equalsIgnoreCase("flakes");

            switch (action) {
                case "give" -> {
                    if (isFlakes) eco.addFlakes(target.getUniqueId(), amount);
                    else eco.addBalance(target.getUniqueId(), amount);
                    sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " " + (isFlakes ? "flakes" : "money") + " to " + target.getName());
                }
                case "take" -> {
                    if (isFlakes) eco.removeFlakes(target.getUniqueId(), amount);
                    else eco.removeBalance(target.getUniqueId(), amount);
                    sender.sendMessage(ChatColor.GREEN + "Took " + amount + " " + (isFlakes ? "flakes" : "money") + " from " + target.getName());
                }
                case "set" -> {
                    if (isFlakes) eco.setFlakes(target.getUniqueId(), amount);
                    else eco.setBalance(target.getUniqueId(), amount);
                    sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s " + (isFlakes ? "flakes" : "money") + " to " + amount);
                }
                default -> sender.sendMessage(ChatColor.RED + "Unknown action: " + action);
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1 && alias.equalsIgnoreCase("eco")) {
            completions.add("give");
            completions.add("take");
            completions.add("set");
        } else if (args.length == 4) {
            completions.add("money");
            completions.add("flakes");
        }
        return completions;
    }
}
