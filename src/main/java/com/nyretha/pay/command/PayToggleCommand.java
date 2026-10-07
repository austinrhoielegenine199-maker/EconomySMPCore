package com.nyretha.pay.command;

import com.nyretha.pay.service.PayService;
import com.nyretha.shop.utils.HexColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayToggleCommand implements CommandExecutor {

    private final PayService payService;

    public PayToggleCommand(PayService payService) {
        this.payService = payService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        payService.togglePay(player);
        if (payService.isPayDisabled(player)) {
            player.sendMessage(HexColor.format("&cNow you can NOT receive payments!"));
        } else {
            player.sendMessage(HexColor.format("&aNow you can receive payments!"));
        }

        return true;
    }
}
