package com.nyretha.economy.command;

import com.nyretha.economy.service.FlakesConfig;
import com.nyretha.economy.service.FlakesManager;
import com.nyretha.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlakesCommand implements CommandExecutor {

    private final FlakesManager flakesManager;
    private final FlakesConfig flakesConfig;

    public FlakesCommand(FlakesManager flakesManager, FlakesConfig flakesConfig) {
        this.flakesManager = flakesManager;
        this.flakesConfig = flakesConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("pay")) {
            return handlePay(sender, args);
        }

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Console must specify a player: /flakes <player>");
                return true;
            }

            long bal = flakesManager.getBalance(player.getUniqueId());
            String msg = flakesConfig.getMessage("balance_self")
                    .replace("%symbol%", flakesConfig.getCurrencySymbol())
                    .replace("%amount%", flakesConfig.formatAmount(bal));

            player.sendMessage(ColorUtils.color(msg));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ColorUtils.color(flakesConfig.getMessage("player_not_found")));
            return true;
        }

        long bal = flakesManager.getBalance(target.getUniqueId());
        String msg = flakesConfig.getMessage("balance_other")
                .replace("%target%", target.getName() != null ? target.getName() : args[0])
                .replace("%symbol%", flakesConfig.getCurrencySymbol())
                .replace("%amount%", flakesConfig.formatAmount(bal));

        sender.sendMessage(ColorUtils.color(msg));
        return true;
    }

    private boolean handlePay(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can pay flakes.");
            return true;
        }

        if (args.length < 3) {
            player.sendMessage(ColorUtils.color("&cUsage: /flakes pay <player> <amount>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(ColorUtils.color(flakesConfig.getMessage("player_not_found")));
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ColorUtils.color(flakesConfig.getMessage("self_pay")));
            return true;
        }

        long amount;
        try {
            amount = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(ColorUtils.color(flakesConfig.getMessage("invalid_amount")));
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(ColorUtils.color(flakesConfig.getMessage("negative_amount")));
            return true;
        }

        if (!flakesManager.has(player.getUniqueId(), amount)) {
            player.sendMessage(ColorUtils.color(flakesConfig.getMessage("insufficient_funds")));
            return true;
        }

        flakesManager.withdraw(player.getUniqueId(), amount);
        flakesManager.deposit(target.getUniqueId(), amount);

        String formattedAmount = flakesConfig.formatAmount(amount);
        String symbol = flakesConfig.getCurrencySymbol();

        String paidMsg = flakesConfig.getMessage("paid_target")
                .replace("%target%", target.getName())
                .replace("%symbol%", symbol)
                .replace("%amount%", formattedAmount);
        player.sendMessage(ColorUtils.color(paidMsg));

        Sound senderSound = flakesConfig.getSound("pay_sender");
        if (senderSound != null) {
            player.playSound(player.getLocation(), senderSound, 1.0f, 1.0f);
        }

        String receivedMsg = flakesConfig.getMessage("received_payment")
                .replace("%player%", player.getName())
                .replace("%symbol%", symbol)
                .replace("%amount%", formattedAmount);
        target.sendMessage(ColorUtils.color(receivedMsg));

        Sound receiverSound = flakesConfig.getSound("pay_receiver");
        if (receiverSound != null) {
            target.playSound(target.getLocation(), receiverSound, 1.0f, 1.0f);
        }

        return true;
    }
}
