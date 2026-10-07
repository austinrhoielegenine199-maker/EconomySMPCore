package com.nyretha.moderation.service;

import com.nyretha.moderation.model.Infraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ModerationService {

    private final Map<UUID, Long> mutedPlayers = new HashMap<>();
    private final Map<String, List<Infraction>> infractionDatabase = new HashMap<>();

    public void mutePlayer(UUID uuid, long durationMillis, String reason) {
        long expireTime = durationMillis > 0 ? System.currentTimeMillis() + durationMillis : -1;
        mutedPlayers.put(uuid, expireTime);
    }

    public boolean isMuted(UUID uuid) {
        if (!mutedPlayers.containsKey(uuid)) return false;
        long expireTime = mutedPlayers.get(uuid);
        if (expireTime != -1 && System.currentTimeMillis() > expireTime) {
            mutedPlayers.remove(uuid);
            return false;
        }
        return true;
    }

    public void warnPlayer(UUID targetUuid, String issuer, String reason) {
        addInfraction(targetUuid.toString(), issuer, reason, "WARN");
    }

    public void addInfraction(String userId, String moderatorId, String reason, String type) {
        Infraction infraction = new Infraction(userId, moderatorId, reason, type);
        infractionDatabase.computeIfAbsent(userId, k -> new ArrayList<>()).add(infraction);
    }

    public List<Infraction> getUserInfractions(String userId) {
        return infractionDatabase.getOrDefault(userId, Collections.emptyList());
    }
}
