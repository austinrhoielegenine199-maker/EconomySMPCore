package com.nyretha.flakes.model;

public class FlakeBooster {
    private final double multiplier;
    private final long expirationTime;

    public FlakeBooster(double multiplier, long durationHours) {
        this.multiplier = multiplier;
        this.expirationTime = System.currentTimeMillis() + (durationHours * 3600000L);
    }

    public double getMultiplier() { return multiplier; }
    public boolean isExpired() { return System.currentTimeMillis() > expirationTime; }
    public long getRemainingSeconds() { return Math.max(0, (expirationTime - System.currentTimeMillis()) / 1000L); }
}
