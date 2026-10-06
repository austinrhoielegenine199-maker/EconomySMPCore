package com.nyretha.economy.command;

import com.nyretha.economy.service.EconomyConfig;
import com.nyretha.economy.service.EconomyManager;
import com.nyretha.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final EconomyManager ecoManager;
    private final EconomyConfig ecoConfig;

    public PayCommand(EconomyManager ecoManager, EconomyConfig ecoConfig) {
        this.ecoManager = ecoManager;
        this.ecoConfig = ecoConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute /pay.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ColorUtils.color("&cUsage: /pay <player> <amount>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ColorUtils.color(ecoConfig.getMessage("player_not_found")));
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ColorUtils.color(ecoConfig.getMessage("self_pay")));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ColorUtils.color(ecoConfig.getMessage("invalid_amount")));
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(ColorUtils.color(ecoConfig.getMessage("negative_amount")));
            return true;
        }

        if (!ecoManager.has(player.getUniqueId(), amount)) {
            player.sendMessage(ColorUtils.color(ecoConfig.getMessage("insufficient_funds")));
            return true;
        }

        ecoManager.withdraw(player.getUniqueId(), amount);
        ecoManager.deposit(target.getUniqueId(), amount);

        String formattedAmount = ecoConfig.formatAmount(amount);
        String symbol = ecoConfig.getCurrencySymbol();

        String paidMsg = ecoConfig.getMessage("paid_target")
                .replace("%target%", target.getName())
                .replace("%symbol%", symbol)
                .replace("%amount%", formattedAmount);
        player.sendMessage(ColorUtils.color(paidMsg));

        Sound senderSound = ecoConfig.getSound("pay_sender");
        if (senderSound != null) {
            player.playSound(player.getLocation(), senderSound, 1.0f, 1.0f);
        }

        String receivedMsg = ecoConfig.getMessage("received_payment")
                .replace("%player%", player.getName())
                .replace("%symbol%", symbol)
                .replace("%amount%", formattedAmount);
        target.sendMessage(ColorUtils.color(receivedMsg));

        Sound receiverSound = ecoConfig.getSound("pay_receiver");
        if (receiverSound != null) {
            target.playSound(target.getLocation(), receiverSound, 1.0f, 1.0f);
        }

        return true;
    }
}
