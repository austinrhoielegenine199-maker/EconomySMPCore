package com.nyretha.tpa.command;

import com.nyretha.shop.utils.HexColor;
import com.nyretha.tpa.gui.TPAAcceptGUI;
import com.nyretha.tpa.gui.TPAHereAcceptGUI;
import com.nyretha.tpa.model.TPARequest;
import com.nyretha.tpa.service.TPAService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPAcceptCommand implements CommandExecutor {

    private final TPAService tpaService;

    public TPAcceptCommand(TPAService tpaService) {
        this.tpaService = tpaService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        TPARequest request = tpaService.getRequest(player.getUniqueId());
        if (request == null) {
            player.sendMessage(HexColor.format("&cYou have no pending TPA requests!"));
            return true;
        }

        if (request.isHere()) {
            TPAHereAcceptGUI.openGUI(player, request.getSender(), player.getWorld().getName(), request.getSender().isFlying());
        } else {
            TPAAcceptGUI.openGUI(player, request.getSender(), player.getWorld().getName(), request.getSender().isFlying());
        }

        return true;
    }
}
