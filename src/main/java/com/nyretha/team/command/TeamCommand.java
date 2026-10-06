package com.nyretha.team.command;

import com.nyretha.team.listener.TeamGuiListener;
import com.nyretha.team.service.TeamManager;
import com.nyretha.team.service.TeamManager.Team;
import com.nyretha.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand implements CommandExecutor {

    private final TeamManager teamManager;

    public TeamCommand(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use team commands.");
            return true;
        }

        if (args.length == 0) {
            TeamGuiListener.openTeamGui(player, teamManager, 1);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("create") && args.length >= 2) {
            if (teamManager.createTeam(args[1], player.getUniqueId())) {
                player.sendMessage(ColorUtils.color("&aTeam '" + args[1] + "' created!"));
            } else {
                player.sendMessage(ColorUtils.color("&cCould not create team."));
            }
            return true;
        }

        if (sub.equals("sethome")) {
            Team team = teamManager.getTeamByPlayer(player.getUniqueId());
            if (team == null || !team.getOwner().equals(player.getUniqueId())) {
                player.sendMessage(ColorUtils.color("&cOnly the team owner can set team home!"));
                return true;
            }
            teamManager.setTeamHome(team.getName(), player.getLocation());
            player.sendMessage(ColorUtils.color("&aTeam home set successfully!"));
            return true;
        }

        return false;
    }
}
