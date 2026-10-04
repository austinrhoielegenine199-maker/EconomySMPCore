package com.nyretha.moderation.command;

import com.nyretha.moderation.service.ModerationService;

public class WarnCommand {
    private final ModerationService moderationService;

    public WarnCommand(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    public void execute(String moderatorId, String targetUserId, String reason) {
        if (targetUserId == null || targetUserId.isBlank()) {
            System.out.println("[Command Error] Invalid target user for warn command.");
            return;
        }

        moderationService.addInfraction(targetUserId, moderatorId, reason, "WARN");
        System.out.println("[Command] Moderator " + moderatorId + " successfully warned " + targetUserId + " for: " + reason);
    }
}
