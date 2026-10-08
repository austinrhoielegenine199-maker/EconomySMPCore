package com.nyretha.command;

import com.nyretha.Core;
import com.nyretha.shop.utils.HexColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CoreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("core.admin")) {
                sender.sendMessage(HexColor.format("&cYou do not have permission to execute this command!"));
                return true;
            }

            Core.getInstance().reloadConfig();
            sender.sendMessage(HexColor.format("&a[NyrethaCore] Configuration reloaded successfully!"));
            return true;
        }

        sender.sendMessage(HexColor.format("&aNyrethaCore v1.0.0 - Running on Paper 1.21.1"));
        return true;
    }
}
