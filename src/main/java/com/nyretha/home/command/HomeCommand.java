package com.nyretha.home.command;

import com.nyretha.NyrethaCore;
import com.nyretha.home.listener.HomeGuiListener;
import com.nyretha.home.model.HomeManager;
import com.nyretha.home.service.HomeConfig;
import com.nyretha.team.service.TeamManager;
import com.nyretha.utils.ColorUtils;
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
            sender.sendMessage("Only players can use home commands.");
            return true;
        }

        String cmd = label.toLowerCase();

        if (cmd.equals("homes") || (cmd.equals("home") && args.length == 0)) {
            TeamManager teamManager = NyrethaCore.getInstance().getTeamManager();
            HomeGuiListener.openHomeGui(player, homeConfig, homeManager, teamManager);
            return true;
        }

        if (cmd.equals("sethome")) {
            String name = args.length > 0 ? args[0] : "home1";
            homeManager.setHome(player.getUniqueId(), name, player.getLocation());
            homeConfig.saveHome(player.getUniqueId(), name, player.getLocation());
            player.sendMessage(ColorUtils.color("&aHome '" + name + "' set successfully!"));
            return true;
        }

        if (cmd.equals("home")) {
            String name = args[0].toLowerCase();
            Location loc = homeManager.getHome(player.getUniqueId(), name);
            if (loc == null) {
                player.sendMessage(ColorUtils.color("&cHome '" + name + "' does not exist."));
                return true;
            }
            HomeGuiListener.startTeleportCountdown(player, loc, homeConfig.getGuiConfig());
            return true;
        }

        return false;
    }
}
