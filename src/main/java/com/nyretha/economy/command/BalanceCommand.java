package com.nyretha.economy.command;

import com.nyretha.economy.service.EconomyConfig;
import com.nyretha.economy.service.EconomyManager;
import com.nyretha.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

    private final EconomyManager ecoManager;
    private final EconomyConfig ecoConfig;

    public BalanceCommand(EconomyManager ecoManager, EconomyConfig ecoConfig) {
        this.ecoManager = ecoManager;
        this.ecoConfig = ecoConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Console must specify a player: /balance <player>");
                return true;
            }

            double bal = ecoManager.getBalance(player.getUniqueId());
            String msg = ecoConfig.getMessage("balance_self")
                    .replace("%symbol%", ecoConfig.getCurrencySymbol())
                    .replace("%amount%", ecoConfig.formatAmount(bal));

            player.sendMessage(ColorUtils.color(msg));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ColorUtils.color(ecoConfig.getMessage("player_not_found")));
            return true;
        }

        double bal = ecoManager.getBalance(target.getUniqueId());
        String msg = ecoConfig.getMessage("balance_other")
                .replace("%target%", target.getName() != null ? target.getName() : args[0])
                .replace("%symbol%", ecoConfig.getCurrencySymbol())
                .replace("%amount%", ecoConfig.formatAmount(bal));

        sender.sendMessage(ColorUtils.color(msg));
        return true;
    }
}
