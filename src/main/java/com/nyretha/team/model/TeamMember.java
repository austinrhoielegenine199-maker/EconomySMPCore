package com.nyretha.team.model;

import java.util.UUID;

public class TeamMember {
    private final UUID uuid;
    private final String name;
    private String rank;
    private boolean canSetHome;
    private boolean canAccessHome;
    private boolean canManageMembers;

    public TeamMember(UUID uuid, String name, String rank) {
        this.uuid = uuid;
        this.name = name;
        this.rank = rank;
        this.canSetHome = rank.equalsIgnoreCase("OWNER") || rank.equalsIgnoreCase("CO_OWNER");
        this.canAccessHome = true;
        this.canManageMembers = rank.equalsIgnoreCase("OWNER");
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }

    public boolean canSetHome() { return canSetHome; }
    public void setCanSetHome(boolean canSetHome) { this.canSetHome = canSetHome; }

    public boolean canAccessHome() { return canAccessHome; }
    public void setCanAccessHome(boolean canAccessHome) { this.canAccessHome = canAccessHome; }

    public boolean canManageMembers() { return canManageMembers; }
    public void setCanManageMembers(boolean canManageMembers) { this.canManageMembers = canManageMembers; }
}
