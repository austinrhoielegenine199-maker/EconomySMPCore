package com.nyretha.tpa.model;

import org.bukkit.entity.Player;

public class TPARequest {
    private final Player sender;
    private final Player target;
    private final boolean isHere;
    private final long timestamp;

    public TPARequest(Player sender, Player target, boolean isHere) {
        this.sender = sender;
        this.target = target;
        this.isHere = isHere;
        this.timestamp = System.currentTimeMillis();
    }

    public Player getSender() { return sender; }
    public Player getTarget() { return target; }
    public boolean isHere() { return isHere; }
    public long getTimestamp() { return timestamp; }
}
