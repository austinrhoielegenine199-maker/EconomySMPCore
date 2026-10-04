package com.nyretha.team.service;

import org.bukkit.Location;
import java.util.*;

public class TeamManager {
    private final Map<UUID, String> playerTeams = new HashMap<>();
    private final Map<String, Set<UUID>> teamMembers = new HashMap<>();
    private final Map<UUID, String> teamLeaders = new HashMap<>();
    private final Map<String, Location> teamHomes = new HashMap<>();

    public boolean isInTeam(UUID playerUuid) {
        return playerTeams.containsKey(playerUuid);
    }

    public String getTeamName(UUID playerUuid) {
        return playerTeams.get(playerUuid);
    }

    public String getTeamPlaceholder(UUID playerUuid) {
        return playerTeams.getOrDefault(playerUuid, "");
    }

    public boolean isLeader(UUID playerUuid, String teamName) {
        String leadTeam = teamLeaders.get(playerUuid);
        return leadTeam != null && leadTeam.equalsIgnoreCase(teamName);
    }

    public boolean createTeam(UUID creatorUuid, String teamName) {
        if (isInTeam(creatorUuid)) return false;
        if (teamMembers.containsKey(teamName)) return false;

        teamMembers.put(teamName, new HashSet<>());
        teamMembers.get(teamName).add(creatorUuid);
        playerTeams.put(creatorUuid, teamName);
        teamLeaders.put(creatorUuid, teamName);
        return true;
    }

    public boolean leaveTeam(UUID playerUuid) {
        if (!isInTeam(playerUuid)) return false;
        String teamName = playerTeams.get(playerUuid);

        if (isLeader(playerUuid, teamName)) {
            disbandTeam(teamName);
            return true;
        }

        playerTeams.remove(playerUuid);
        Set<UUID> members = teamMembers.get(teamName);
        if (members != null) {
            members.remove(playerUuid);
        }
        return true;
    }

    public boolean disbandTeam(String teamName) {
        Set<UUID> members = teamMembers.remove(teamName);
        if (members == null) return false;

        for (UUID memberUuid : members) {
            playerTeams.remove(memberUuid);
            teamLeaders.remove(memberUuid);
        }
        teamHomes.remove(teamName);
        return true;
    }

    public boolean hasTeamHome(String teamName) {
        return teamHomes.containsKey(teamName);
    }

    public Location getTeamHome(String teamName) {
        return teamHomes.get(teamName);
    }

    public void setTeamHome(String teamName, Location location) {
        teamHomes.put(teamName, location);
    }
}
