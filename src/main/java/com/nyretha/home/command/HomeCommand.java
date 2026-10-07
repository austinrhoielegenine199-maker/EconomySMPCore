package com.nyretha.home.command;

import com.nyretha.home.gui.HomeGUI;
import com.nyretha.home.service.HomeService;
import com.nyretha.team.service.TeamService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {

    private final HomeService homeService;
    private final TeamService teamService;

    public HomeCommand(HomeService homeService, TeamService teamService) {
        this.homeService = homeService;
        this.teamService = teamService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        HomeGUI.openGUI(player, homeService, teamService);
        return true;
    }
}
