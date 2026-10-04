package com.nyretha.moderation.command;

import com.nyretha.moderation.util.TimeParser;

public class MuteCommand {

    public void execute(String moderatorIGN, String targetIGN, String durationArg, String reason) {
        if (targetIGN == null || targetIGN.isBlank()) {
            System.out.println("[Command Error] Invalid target IGN.");
            return;
        }

        long durationMillis = TimeParser.parseToMillis(durationArg);
        if (durationMillis <= 0) {
            System.out.println("[Command Error] Invalid mute duration format. Use formats like 1s, 10h, 1w.");
            return;
        }

        System.out.println("[Command] " + moderatorIGN + " muted " + targetIGN + " for " + durationArg + ". Reason: " + reason);
    }
}
