package com.nyretha.economy.command;

import com.nyretha.economy.service.EconomyConfigService;
import com.nyretha.economy.util.NumberFormatter;
import com.nyretha.economy.util.PlayerMatcher;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PayCommand implements CommandExecutor {
    private final Economy economy;
    private final EconomyConfigService configService;

    public PayCommand(Economy economy, EconomyConfigService configService) {
        this.economy = economy;
        this.configService = configService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage("§cUsage: /pay <player> <amount>");
            return true;
        }

        Player target = PlayerMatcher.matchPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage("§cPlayer not found or offline.");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage("§cYou cannot pay yourself!");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid amount specified.");
            return true;
        }

        if (amount <= 0) {
            player.sendMessage("§cAmount must be greater than zero.");
            return true;
        }

        if (economy.getBalance(player) < amount) {
            player.sendMessage("§cYou do not have enough money!");
            return true;
        }

        economy.withdrawPlayer(player, amount);
        economy.depositPlayer(target, amount);

        String symbol = configService.getCurrencySymbol();
        boolean useFormatting = configService.isNumberFormattingEnabled();
        String formattedAmount = NumberFormatter.format(amount, symbol, useFormatting);

        player.sendMessage("§aYou successfully sent §e" + formattedAmount + " §ato §e" + target.getName());
        target.sendMessage("§aYou received §e" + formattedAmount + " §afrom §e" + player.getName());

        try {
            String senderSoundName = configService.getSound("pay_sender");
            String receiverSoundName = configService.getSound("pay_receiver");
            
            player.playSound(player.getLocation(), Sound.valueOf(senderSoundName), 1.0f, 1.0f);
            target.playSound(target.getLocation(), Sound.valueOf(receiverSoundName), 1.0f, 1.0f);
        } catch (Exception e) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        }

        return true;
    }
}
