package com.nyretha.moderation.model;

import java.time.Instant;

public class Infraction {
    private final String userId;
    private final String moderatorId;
    private final String reason;
    private final String type; // e.g., WARN, MUTE, BAN, TEMPBAN
    private final Instant timestamp;

    public Infraction(String userId, String moderatorId, String reason, String type) {
        this.userId = userId;
        this.moderatorId = moderatorId;
        this.reason = reason;
        this.type = type;
        this.timestamp = Instant.now();
    }

    public String getUserId() { return userId; }
    public String getModeratorId() { return moderatorId; }
    public String getReason() { return reason; }
    public String getType() { return type; }
    public Instant getTimestamp() { return timestamp; }
}
