package com.nyretha.team.command;

import com.nyretha.team.service.TeamManager;
import com.nyretha.team.service.TeamManager.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamCommand implements CommandExecutor {

    private final TeamManager teamManager;

    public TeamCommand(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use team commands.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.GOLD + "=== Team Commands ===");
            player.sendMessage(ChatColor.YELLOW + "/team create <name>");
            player.sendMessage(ChatColor.YELLOW + "/team invite <player>");
            player.sendMessage(ChatColor.YELLOW + "/team join");
            player.sendMessage(ChatColor.YELLOW + "/team leave");
            player.sendMessage(ChatColor.YELLOW + "/team info");
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "create" -> {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /team create <name>");
                    return true;
                }
                String teamName = args[1];
                if (teamManager.createTeam(teamName, player.getUniqueId())) {
                    player.sendMessage(ChatColor.GREEN + "Team '" + teamName + "' created successfully!");
                } else {
                    player.sendMessage(ChatColor.RED + "Failed to create team. Name might be taken or you are already in a team.");
                }
            }
            case "invite" -> {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /team invite <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Player not found online.");
                    return true;
                }
                Team team = teamManager.getTeamByPlayer(player.getUniqueId());
                if (team == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a team.");
                    return true;
                }
                if (teamManager.invitePlayer(player.getUniqueId(), target.getUniqueId(), team.getName())) {
                    player.sendMessage(ChatColor.GREEN + "Invited " + target.getName() + " to your team!");
                    target.sendMessage(ChatColor.GREEN + "You were invited to team '" + team.getName() + "'. Type /team join to accept!");
                } else {
                    player.sendMessage(ChatColor.RED + "Only the team owner can invite players.");
                }
            }
            case "join" -> {
                if (teamManager.joinTeam(player.getUniqueId())) {
                    player.sendMessage(ChatColor.GREEN + "Successfully joined team!");
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have any pending team invites.");
                }
            }
            case "leave" -> {
                if (teamManager.leaveTeam(player.getUniqueId())) {
                    player.sendMessage(ChatColor.GREEN + "You left the team.");
                } else {
                    player.sendMessage(ChatColor.RED + "You are not in a team.");
                }
            }
            case "info" -> {
                Team team = teamManager.getTeamByPlayer(player.getUniqueId());
                if (team == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a team.");
                    return true;
                }
                player.sendMessage(ChatColor.GOLD + "=== Team: " + team.getName() + " ===");
                OfflinePlayer owner = Bukkit.getOfflinePlayer(team.getOwner());
                player.sendMessage(ChatColor.YELLOW + "Owner: " + owner.getName());

                List<String> memberNames = new ArrayList<>();
                for (var uuid : team.getMembers()) {
                    memberNames.add(Bukkit.getOfflinePlayer(uuid).getName());
                }
                player.sendMessage(ChatColor.YELLOW + "Members: " + String.join(", ", memberNames));
            }
            default -> player.sendMessage(ChatColor.RED + "Unknown team subcommand. Type /team for help.");
        }

        return true;
    }
}
