package com.nyretha.tools.command;

import com.nyretha.shop.utils.HexColor;
import com.nyretha.tools.gui.ToolsGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToolsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        ToolsGUI.openGUI(player);
        return true;
    }
}
