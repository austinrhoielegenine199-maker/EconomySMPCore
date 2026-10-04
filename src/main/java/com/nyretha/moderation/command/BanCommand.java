package com.nyretha.moderation.command;

import com.nyretha.moderation.service.ConfigService;
import com.nyretha.moderation.service.WebhookService;
import java.util.Map;

public class BanCommand {
    private final ConfigService configService;
    private final WebhookService webhookService;

    public BanCommand(ConfigService configService, WebhookService webhookService) {
        this.configService = configService;
        this.webhookService = webhookService;
    }

    public void execute(String moderatorIGN, String targetIGN, String reason) {
        if (targetIGN == null || targetIGN.isBlank()) {
            System.out.println("[Command Error] Invalid target IGN.");
            return;
        }

        String duration = "Permanent";
        
        Map<String, String> placeholders = Map.of(
            "{reason}", reason,
            "{duration}", duration
        );
        String kickMessage = configService.getFormattedMessage("ban", placeholders);

        System.out.println("[Command] " + moderatorIGN + " banned " + targetIGN + ". Kick Message:\n" + kickMessage);

        webhookService.sendBanLog(moderatorIGN, targetIGN, duration, reason, "BAN");
    }
}
