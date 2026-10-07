package com.nyretha.team.model;

import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Team {
    private final String name;
    private UUID owner;
    private Location home;
    private boolean pvpEnabled;
    private final Map<UUID, TeamMember> members = new ConcurrentHashMap<>();

    public Team(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        this.pvpEnabled = false;
    }

    public String getName() { return name; }
    public UUID getOwner() { return owner; }
    public void setOwner(UUID owner) { this.owner = owner; }

    public Location getHome() { return home; }
    public void setHome(Location home) { this.home = home; }

    public boolean isPvpEnabled() { return pvpEnabled; }
    public void setPvpEnabled(boolean pvpEnabled) { this.pvpEnabled = pvpEnabled; }

    public Map<UUID, TeamMember> getMembers() { return members; }
    public void addMember(TeamMember member) { members.put(member.getUuid(), member); }
    public void removeMember(UUID uuid) { members.remove(uuid); }
    public boolean isMember(UUID uuid) { return members.containsKey(uuid); }
}
