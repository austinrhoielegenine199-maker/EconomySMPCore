package com.nyretha.commands;

import com.nyretha.NyrethaCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public final class CoreCommand implements CommandExecutor, TabCompleter {

    private final NyrethaCore plugin;

    public CoreCommand(NyrethaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!sender.hasPermission("nyretha.admin")) {
            sender.sendMessage(
                    ChatColor.RED +
                    "You do not have permission to use /core."
            );
            return true;
        }

        if (args.length == 1 &&
                args[0].equalsIgnoreCase("reload")) {

            plugin.reloadCore();

            sender.sendMessage(
                    ChatColor.GREEN +
                    "NyrethaCore configuration reloaded."
            );

            return true;
        }

        sender.sendMessage(
                ChatColor.YELLOW +
                "Usage: /core reload"
        );

        return true;
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {
        if (args.length == 1) {
            return Collections.singletonList("reload");
        }

        return Collections.emptyList();
    }
}
