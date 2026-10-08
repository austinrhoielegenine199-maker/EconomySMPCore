package com.nyretha.tpa.command;

import com.nyretha.shop.utils.HexColor;
import com.nyretha.tpa.service.TPAService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPACommand implements CommandExecutor {

    private final TPAService tpaService;

    public TPACommand(TPAService tpaService) {
        this.tpaService = tpaService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(HexColor.format("&cUsage: /tpa <player>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage(HexColor.format("&cThat player is not online!"));
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(HexColor.format("&cYou cannot send a TPA request to yourself!"));
            return true;
        }

        if (tpaService.isTpaDisabled(target)) {
            player.sendMessage(HexColor.format("&cThis player has disabled TPA requests!"));
            return true;
        }

        tpaService.sendRequest(player, target, false);
        player.sendMessage(HexColor.format("&fTPA request sent to &#00f986" + target.getName() + "&f!"));
        target.sendMessage(HexColor.format("&fPlayer &#00f986" + player.getName() + " &fwants to teleport to you!"));

        return true;
    }
}
