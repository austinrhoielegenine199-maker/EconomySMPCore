package com.nyretha.home.command;

import com.nyretha.NyrethaCore;
import com.nyretha.home.model.HomeManager;
import com.nyretha.home.service.HomeConfig;
import com.nyretha.utils.ColorUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeCommand implements CommandExecutor, TabCompleter {

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
            sender.sendMessage(ColorUtils.color("&cOnly players can execute home commands."));
            return true;
        }

        if (label.equalsIgnoreCase("sethome")) {
            String homeName;
            
            if (args.length > 0) {
                homeName = args[0].toLowerCase();
            } else {
                // Auto-detect the next available home slot from 1 to 5
                homeName = "1";
                for (int i = 1; i <= 5; i++) {
                    if (homeManager.getHome(player.getUniqueId(), String.valueOf(i)) == null) {
                        homeName = String.valueOf(i);
                        break;
                    }
                }
            }

            homeManager.setHome(player.getUniqueId(), homeName, player.getLocation());
            player.sendMessage(ColorUtils.color("&aHome &b" + homeName + " &aset at your current location!"));
            return true;
        }

        if (label.equalsIgnoreCase("home")) {
            String homeName = (args.length > 0) ? args[0].toLowerCase() : "1";
            Location loc = homeManager.getHome(player.getUniqueId(), homeName);
            if (loc == null) {
                player.sendMessage(ColorUtils.color("&cHome &b" + homeName + " &cdoes not exist. Use /sethome " + homeName));
                return true;
            }
            player.teleport(loc);
            player.sendMessage(ColorUtils.color("&aTeleported to home &b" + homeName + "&a."));
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("1", "2", "3", "4", "5");
        }
        return new ArrayList<>();
    }
}
