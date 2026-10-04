package com.nyretha.team.command;

import com.nyretha.team.service.TeamManager;
import org.bukkit.ChatColor;
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
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use team commands.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /team <create|leave|disband|sethome>"));
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("create")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /team create <name>"));
                return true;
            }
            String teamName = args[1];
            boolean success = teamManager.createTeam(player.getUniqueId(), teamName);
            if (success) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully created team &e" + teamName + "&a!"));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCould not create team. Either you are already in one or the name is taken."));
            }
        } else if (sub.equals("leave")) {
            boolean success = teamManager.leaveTeam(player.getUniqueId());
            if (success) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have left your team."));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are not in a team."));
            }
        } else if (sub.equals("disband")) {
            String teamName = teamManager.getTeamName(player.getUniqueId());
            if (teamName == null || !teamManager.isLeader(player.getUniqueId(), teamName)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must be the team leader to disband the team."));
                return true;
            }
            teamManager.disbandTeam(teamName);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cTeam disbanded successfully."));
        } else if (sub.equals("sethome")) {
            String teamName = teamManager.getTeamName(player.getUniqueId());
            if (teamName == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must be in a team to set a team home."));
                return true;
            }
            teamManager.setTeamHome(teamName, player.getLocation());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTeam home set successfully!"));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUnknown team subcommand."));
        }

        return true;
    }
}
