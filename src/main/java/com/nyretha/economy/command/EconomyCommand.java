package com.nyretha.economy.command;

import com.nyretha.NyrethaCore;
import com.nyretha.economy.service.EconomyConfigService;
import com.nyretha.utils.ColorUtils;
import org.bukkit.Bukkit;
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
                    sender.sendMessage(ColorUtils.color("&cOnly players can check their own balance."));
                    return true;
                }
                double money = eco.getBalance(player.getUniqueId());
                double flakes = eco.getFlakes(player.getUniqueId());
                player.sendMessage(ColorUtils.color("&eBalance: &a$" + String.format("%.2f", money)));
                player.sendMessage(ColorUtils.color("&bFlakes: &a" + String.format("%.0f", flakes) + " ❄"));
                return true;
            } else {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                if (!target.hasPlayedBefore() && !target.isOnline()) {
                    sender.sendMessage(ColorUtils.color("&cPlayer never joined the server."));
                    return true;
                }
                String targetName = target.getName() != null ? target.getName() : args[0];
                double money = eco.getBalance(target.getUniqueId());
                double flakes = eco.getFlakes(target.getUniqueId());
                sender.sendMessage(ColorUtils.color("&e" + targetName + "'s Balance: &a$" + String.format("%.2f", money)));
                sender.sendMessage(ColorUtils.color("&b" + targetName + "'s Flakes: &a" + String.format("%.0f", flakes) + " ❄"));
                return true;
            }
        }

        // /pay <player> <amount> [flakes]
        if (cmd.equals("pay")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ColorUtils.color("&cOnly players can send money or flakes."));
                return true;
            }
            if (args.length < 2) {
                player.sendMessage(ColorUtils.color("&cUsage: /pay <player> <amount> [flakes]"));
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore() && !target.isOnline()) {
                player.sendMessage(ColorUtils.color("&cPlayer not found or offline."));
                return true;
            }

            if (target.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(ColorUtils.color("&cYou cannot pay yourself!"));
                return true;
            }

            double amount;
            try {
                amount = Double.parseDouble(args[1]);
                if (amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                player.sendMessage(ColorUtils.color("&cInvalid amount specified."));
                return true;
            }

            boolean isFlakes = args.length >= 3 && args[2].equalsIgnoreCase("flakes");
            String targetName = target.getName() != null ? target.getName() : args[0];

            if (isFlakes) {
                if (eco.getFlakes(player.getUniqueId()) < amount) {
                    player.sendMessage(ColorUtils.color("&cYou don't have enough flakes!"));
                    return true;
                }
                eco.removeFlakes(player.getUniqueId(), amount);
                eco.addFlakes(target.getUniqueId(), amount);
                player.sendMessage(ColorUtils.color("&aPaid &b" + (long) amount + " ❄ flakes &ato &b" + targetName));
                if (target.isOnline() && target.getPlayer() != null) {
                    target.getPlayer().sendMessage(ColorUtils.color("&aReceived &b" + (long) amount + " ❄ flakes &afrom &b" + player.getName()));
                }
            } else {
                if (eco.getBalance(player.getUniqueId()) < amount) {
                    player.sendMessage(ColorUtils.color("&cYou don't have enough money!"));
                    return true;
                }
                eco.removeBalance(player.getUniqueId(), amount);
                eco.addBalance(target.getUniqueId(), amount);
                player.sendMessage(ColorUtils.color("&aPaid &a$" + String.format("%.2f", amount) + " &ato &b" + targetName));
                if (target.isOnline() && target.getPlayer() != null) {
                    target.getPlayer().sendMessage(ColorUtils.color("&aReceived &a$" + String.format("%.2f", amount) + " &afrom &b" + player.getName()));
                }
            }
            return true;
        }

        // Admin Command: /eco <give|take|set> <player> <amount> [money|flakes]
        if (cmd.equals("eco")) {
            if (!sender.hasPermission("nyrethacore.admin.eco")) {
                sender.sendMessage(ColorUtils.color("&cNo permission."));
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(ColorUtils.color("&cUsage: /eco <give|take|set> <player> <amount> [money|flakes]"));
                return true;
            }

            String action = args[0].toLowerCase();
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            String targetName = target.getName() != null ? target.getName() : args[1];

            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ColorUtils.color("&cInvalid amount specified."));
                return true;
            }

            boolean isFlakes = args.length >= 4 && args[3].equalsIgnoreCase("flakes");
            String currencyName = isFlakes ? "flakes" : "money";

            switch (action) {
                case "give" -> {
                    if (isFlakes) eco.addFlakes(target.getUniqueId(), amount);
                    else eco.addBalance(target.getUniqueId(), amount);
                    sender.sendMessage(ColorUtils.color("&aGave &b" + amount + " " + currencyName + " &ato &b" + targetName));
                }
                case "take" -> {
                    if (isFlakes) eco.removeFlakes(target.getUniqueId(), amount);
                    else eco.removeBalance(target.getUniqueId(), amount);
                    sender.sendMessage(ColorUtils.color("&aTook &b" + amount + " " + currencyName + " &afrom &b" + targetName));
                }
                case "set" -> {
                    if (isFlakes) eco.setFlakes(target.getUniqueId(), amount);
                    else eco.setBalance(target.getUniqueId(), amount);
                    sender.sendMessage(ColorUtils.color("&aSet &b" + targetName + "'s " + currencyName + " &
