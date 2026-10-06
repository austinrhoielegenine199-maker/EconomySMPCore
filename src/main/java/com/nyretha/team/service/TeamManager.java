package com.nyretha.team.service;

import java.util.*;

public class TeamManager {

    public static class Team {
        private final String name;
        private final UUID owner;
        private final Set<UUID> members = new HashSet<>();

        public Team(String name, UUID owner) {
            this.name = name;
            this.owner = owner;
            this.members.add(owner);
        }

        public String getName() { return name; }
        public UUID getOwner() { return owner; }
        public Set<UUID> getMembers() { return members; }
    }

    private final Map<String, Team> teamsByName = new HashMap<>();
    private final Map<UUID, Team> playerTeamMap = new HashMap<>();
    private final Map<UUID, String> pendingInvites = new HashMap<>(); // Target UUID -> Team Name

    public boolean createTeam(String name, UUID owner) {
        if (teamsByName.containsKey(name.toLowerCase()) || playerTeamMap.containsKey(owner)) {
            return false;
        }
        Team team = new Team(name, owner);
        teamsByName.put(name.toLowerCase(), team);
        playerTeamMap.put(owner, team);
        return true;
    }

    public boolean invitePlayer(UUID inviter, UUID target, String teamName) {
        Team team = playerTeamMap.get(inviter);
        if (team == null || !team.getOwner().equals(inviter)) return false;

        pendingInvites.put(target, team.getName());
        return true;
    }

    public boolean joinTeam(UUID target) {
        String teamName = pendingInvites.get(target);
        if (teamName == null) return false;

        Team team = teamsByName.get(teamName.toLowerCase());
        if (team == null) return false;

        team.getMembers().add(target);
        playerTeamMap.put(target, team);
        pendingInvites.remove(target);
        return true;
    }

    public boolean leaveTeam(UUID player) {
        Team team = playerTeamMap.get(player);
        if (team == null) return false;

        if (team.getOwner().equals(player)) {
            // Disband team if owner leaves
            for (UUID member : team.getMembers()) {
                playerTeamMap.remove(member);
            }
            teamsByName.remove(team.getName().toLowerCase());
        } else {
            team.getMembers().remove(player);
            playerTeamMap.remove(player);
        }
        return true;
    }

    public Team getTeamByPlayer(UUID uuid) {
        return playerTeamMap.get(uuid);
    }
}
