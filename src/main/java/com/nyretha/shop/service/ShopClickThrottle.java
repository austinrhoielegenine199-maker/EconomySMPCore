package com.nyretha.shop.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopClickThrottle {
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long cooldownMillis = 250; // 250ms debounce time

    public boolean canClick(UUID playerUuid) {
        long current = System.currentTimeMillis();
        if (cooldowns.containsKey(playerUuid)) {
            long lastClick = cooldowns.get(playerUuid);
            if (current - lastClick < cooldownMillis) {
                return false;
            }
        }
        cooldowns.put(playerUuid, current);
        return true;
    }
}
