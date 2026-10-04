package com.nyretha.moderation.service;

import com.nyretha.moderation.model.Infraction;
import java.util.*;

public class ModerationService {
    
    private final Map<String, List<Infraction>> infractionDatabase = new HashMap<>();

    public void addInfraction(String userId, String moderatorId, String reason, String type) {
        Infraction infraction = new Infraction(userId, moderatorId, reason, type);
        infractionDatabase.computeIfAbsent(userId, k -> new ArrayList<>()).add(infraction);
    }

    public List<Infraction> getUserInfractions(String userId) {
        return infractionDatabase.getOrDefault(userId, Collections.emptyList());
    }
}
