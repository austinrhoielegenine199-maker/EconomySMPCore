package com.nyretha.pay.command;

import com.nyretha.pay.gui.PayHistoryGUI;
import com.nyretha.pay.service.PayService;
import com.nyretha.shop.utils.HexColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayHistoryCommand implements CommandExecutor {

    private final PayService payService;

    public PayHistoryCommand(PayService payService) {
        this.payService = payService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        Player target = player;
        if (args.length > 0 && player.hasPermission("paycore.payhistory.other")) {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                player.sendMessage(HexColor.format("&cPlayer not found!"));
                return true;
            }
        }

        PayHistoryGUI.openGUI(player, target, payService, 1);
        return true;
    }
}
