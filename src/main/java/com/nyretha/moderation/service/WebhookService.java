package com.nyretha.moderation.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WebhookService {
    
    private final ConfigService configService;
    private final HttpClient httpClient;

    public WebhookService(ConfigService configService) {
        this.configService = configService;
        this.httpClient = HttpClient.newHttpClient();
    }

    public void sendBanLog(String moderatorIGN, String targetIGN, String duration, String reason, String actionType) {
        String webhookUrl = configService.getWebhookUrl();
        if (webhookUrl == null || webhookUrl.isEmpty() || webhookUrl.equals("YOUR_DISCORD_WEBHOOK_URL_HERE")) {
            return;
        }

        String jsonPayload = String.format(
            "{\"content\": null, \"embeds\": [{" +
            "\"title\": \"🛡️ Moderation Log: %s\"," +
            "\"color\": 15158332," +
            "\"fields\": [" +
            "{\"name\": \"Target (IGN)\", \"value\": \"%s\", \"inline\": true}," +
            "{\"name\": \"Moderator\", \"value\": \"%s\", \"inline\": true}," +
            "{\"name\": \"Duration\", \"value\": \"%s\", \"inline\": true}," +
            "{\"name\": \"Reason\", \"value\": \"%s\", \"inline\": false}" +
            "]," +
            "\"timestamp\": \"%s\"" +
            "}]}",
            actionType, targetIGN, moderatorIGN, duration, reason, java.time.Instant.now().toString()
        );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            System.err.println("[Webhook Error] Failed to send moderation log: " + e.getMessage());
        }
    }
}
