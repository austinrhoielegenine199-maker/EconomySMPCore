package com.nyretha.moderation.command;

import com.nyretha.moderation.service.WebhookService;

public class BanCommand {
    private final WebhookService webhookService;

    public BanCommand(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    public void execute(String moderatorIGN, String targetIGN, String reason) {
        if (targetIGN == null || targetIGN.isBlank()) {
            System.out.println("[Command Error] Invalid target IGN.");
            return;
        }

        String duration = "Permanent";
        
        // Execute server-side ban action here...
        System.out.println("[Command] " + moderatorIGN + " permanently banned " + targetIGN + ". Reason: " + reason);

        // Send Webhook Log
        webhookService.sendBanLog(moderatorIGN, targetIGN, duration, reason, "BAN");
    }
}
