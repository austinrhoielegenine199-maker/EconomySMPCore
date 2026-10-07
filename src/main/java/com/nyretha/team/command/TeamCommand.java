package com.nyretha.team.command;

import com.nyretha.shop.utils.HexColor;
import com.nyretha.team.gui.TeamGUI;
import com.nyretha.team.model.Team;
import com.nyretha.team.service.TeamService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand implements CommandExecutor {

    private final TeamService teamService;

    public TeamCommand(TeamService teamService) {
        this.teamService = teamService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute team commands.");
            return true;
        }

        if (args.length == 0) {
            if (teamService.isInTeam(player)) {
                TeamGUI.openGUI(player, teamService, 1);
            } else {
                player.sendMessage(HexColor.format("&cUsage: /team create <name> or /team join <name>"));
            }
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "create" -> {
                if (args.length < 2) {
                    player.sendMessage(HexColor.format("&cUsage: /team create <name>"));
                    return true;
                }
                if (teamService.isInTeam(player)) {
                    player.sendMessage(HexColor.format("&cYou are already in a team!"));
                    return true;
                }
                teamService.createTeam(player, args[1]);
                player.sendMessage(HexColor.format("&fYour team &#34eb9b" + args[1] + " &fhas been created"));
            }
            case "join" -> {
                if (args.length < 2) {
                    player.sendMessage(HexColor.format("&cUsage: /team join <team>"));
                    return true;
                }
                Team team = teamService.getTeamByName(args[1]);
                if (team == null) {
                    player.sendMessage(HexColor.format("&cTeam not found!"));
                    return true;
                }
                if (!teamService.hasInvite(player, args[1])) {
                    player.sendMessage(HexColor.format("&cYou have no invite from this team!"));
                    return true;
                }
                teamService.joinTeam(player, team);
                player.sendMessage(HexColor.format("&fYou have joined the team &#34eb9b" + team.getName()));
            }
            case "invite" -> {
                if (args.length < 2) {
                    player.sendMessage(HexColor.format("&cUsage: /team invite <player>"));
                    return true;
                }
                Team team = teamService.getTeam(player);
                if (team == null) {
                    player.sendMessage(HexColor.format("&cYou are not in a team!"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(HexColor.format("&cPlayer not found!"));
                    return true;
                }
                teamService.sendInvite(team, target);
                player.sendMessage(HexColor.format("&fInvitation sent to &#34eb9b" + target.getName()));
                target.sendMessage(HexColor.format("&fYou have been invited to the team &#34eb9b" + team.getName() + "! &7/team join " + team.getName()));
            }
            case "leave" -> {
                if (!teamService.isInTeam(player)) {
                    player.sendMessage(HexColor.format("&cYou are not in a team!"));
                    return true;
                }
                teamService.leaveTeam(player);
                player.sendMessage(HexColor.format("&#FC0000You have left the team!"));
            }
            case "sethome" -> {
                if (!teamService.isInTeam(player)) {
                    player.sendMessage(HexColor.format("&cYou are not in a team!"));
                    return true;
                }
                teamService.setTeamHome(player, player.getLocation());
                player.sendMessage(HexColor.format("&aTeam home has been set!"));
            }
            case "home" -> {
                if (!teamService.hasTeamHome(player)) {
                    player.sendMessage(HexColor.format("&cYour team has not set a home!"));
                    return true;
                }
                player.teleport(teamService.getTeamHome(player));
                player.sendMessage(HexColor.format("&aYou have been teleported to Team home!"));
            }
            default -> player.sendMessage(HexColor.format("&cUnknown team sub-command."));
        }

        return true;
    }
}
