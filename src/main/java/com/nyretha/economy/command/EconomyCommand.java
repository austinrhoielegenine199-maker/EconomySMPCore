package com.nyretha.eco;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class EconomyCommand implements CommandExecutor {
    private final Plugin plugin;
    private final EconomyConfigService configService;

    public EconomyCommand(Plugin plugin, EconomyConfigService configService) {
        this.plugin = plugin;
        this.configService = configService;
    }

    private double getBalance(Player player) {
        return 1000.0; 
    }

    private void setBalance(Player player, double amount) {}

    private void playConfigSound(Player player, String soundKey) {
        String soundName = configService.getSound(soundKey);
        if (soundName != null && !soundName.isEmpty()) {
            try {
                Sound sound = Sound.valueOf(soundName.toUpperCase());
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmdName = command.getName().toLowerCase();
        String symbol = configService.getCurrencySymbol();

        if (cmdName.equals("bal") || cmdName.equals("balance")) {
            if (args.length == 0) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Console must specify a player: /bal <player>");
                    return true;
                }
                Player player = (Player) sender;
                double bal = getBalance(player);
                String msg = configService.getMessage("balance_self", "&eYour balance is &a%symbol%%amount%")
                        .replace("%symbol%", symbol)
                        .replace("%amount%", String.valueOf(bal));
                player.sendMessage(msg);
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                sender.sendMessage(configService.getMessage("player_not_found", "&cPlayer not found or offline!"));
                return true;
            }

            double bal = getBalance(target);
            String msg = configService.getMessage("balance_other", "&#009bff%target% &ehas &a%symbol%%amount%")
                    .replace("%target%", target.getName())
                    .replace("%symbol%", symbol)
                    .replace("%amount%", String.valueOf(bal));
            sender.sendMessage(msg);
            return true;
        }

        if (cmdName.equals("pay")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use /pay.");
                return true;
            }

            Player player = (Player) sender;

            if (args.length < 2) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /pay <player> <amount>"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(configService.getMessage("player_not_found", "&cPlayer not found or offline!"));
                return true;
            }

            if (target.equals(player)) {
                player.sendMessage(configService.getMessage("self_pay", "&cYou cannot pay yourself!"));
                return true;
            }

            double amount;
            try {
                amount = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(configService.getMessage("invalid_amount", "&cInvalid amount specified!"));
                return true;
            }

            if (amount <= 0) {
                player.sendMessage(configService.getMessage("negative_amount", "&cAmount must be greater than zero!"));
                return true;
            }

            double senderBal = getBalance(player);
            if (senderBal < amount) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have enough money!"));
                return true;
            }

            setBalance(player, senderBal - amount);
            setBalance(target, getBalance(target) + amount);

            String payerMsg = configService.getMessage("paid_target", "&#009bffYou paid &b%target% &a%symbol%%amount%")
                    .replace("%target%", target.getName())
                    .replace("%symbol%", symbol)
                    .replace("%amount%", String.valueOf(amount));
            player.sendMessage(payerMsg);
            playConfigSound(player, "pay_sender");

            String targetMsg = configService.getMessage("received_payment", "&#009bff%player% &ehas paid you &a%symbol%%amount%")
                    .replace("%player%", player.getName())
                    .replace("%symbol%", symbol)
                    .replace("%amount%", String.valueOf(amount));
            target.sendMessage(targetMsg);
            playConfigSound(player, "pay_receiver");

            return true;
        }

        if (cmdName.equals("eco")) {
            if (!sender.hasPermission("nyretha.eco.admin")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have permission to use /eco!"));
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eco <give/take/set> <player> <amount>"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null || !target.isOnline()) {
                sender.sendMessage(configService.getMessage("player_not_found", "&cPlayer not found or offline!"));
                return true;
            }

            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(configService.getMessage("invalid_amount", "&cInvalid amount specified!"));
                return true;
            }

            String action = args[0].toLowerCase();
            double currentBal = getBalance(target);

            switch (action) {
                case "give":
                    setBalance(target, currentBal + amount);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&#009bff" + target.getName() + " &ehas been given &a" + symbol + amount));
                    break;
                case "take":
                    setBalance(target, Math.max(0, currentBal - amount));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&#009bff" + target.getName() + " &ehas had &a" + symbol + amount + " &etaken."));
                    break;
                case "set":
                    setBalance(target, amount);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&#009bff" + target.getName() + "'s &ebalance set to &a" + symbol + amount));
                    break;
                default:
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUnknown action. Use give, take, or set."));
                    break;
            }
            return true;
        }

        return false;
    }
}
