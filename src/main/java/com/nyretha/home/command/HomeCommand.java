package com.nyretha.home.command;

import com.nyretha.NyrethaCore;
import com.nyretha.home.model.HomeManager;
import com.nyretha.home.service.HomeConfig;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class HomeCommand implements CommandExecutor {

    private final HomeManager homeManager;
    private final HomeConfig homeConfig;
    private final NyrethaCore plugin;

    public HomeCommand(HomeManager homeManager, HomeConfig homeConfig, NyrethaCore plugin) {
        this.homeManager = homeManager;
        this.homeConfig = homeConfig;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use home commands.");
            return true;
        }

        String cmd = label.toLowerCase();

        // Load homes lazily if missing
        Map<String, Location> homes = homeManager.getHomes(player.getUniqueId());
        if (homes.isEmpty()) {
            homes = homeConfig.getHomes(player.getUniqueId());
            homeManager.loadHomes(player.getUniqueId(), homes);
        }

        if (cmd.equals("sethome")) {
            String name = args.length > 0 ? args[0] : "home";
            Location loc = player.getLocation();

            homeManager.setHome(player.getUniqueId(), name, loc);
            homeConfig.saveHome(player.getUniqueId(), name, loc);
            player.sendMessage(ChatColor.GREEN + "Home '" + name + "' set successfully!");
            return true;
        }

        if (cmd.equals("delhome")) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /delhome <name>");
                return true;
            }
            String name = args[0];
            if (homeManager.deleteHome(player.getUniqueId(), name)) {
                homeConfig.removeHome(player.getUniqueId(), name);
                player.sendMessage(ChatColor.GREEN + "Home '" + name + "' deleted!");
            } else {
                player.sendMessage(ChatColor.RED + "Home '" + name + "' does not exist.");
            }
            return true;
        }

        if (cmd.equals("home")) {
            String name = args.length > 0 ? args[0] : "home";
            Location loc = homeManager.getHome(player.getUniqueId(), name);

            if (loc == null) {
                player.sendMessage(ChatColor.RED + "Home '" + name + "' not found! Your homes: " + String.join(", ", homes.keySet()));
                return true;
            }

            player.teleport(loc);
            player.sendMessage(ChatColor.GREEN + "Teleported to home '" + name + "'!");
            return true;
        }

        return false;
    }
}
