package com.nyretha.moderation.command;

import com.nyretha.moderation.service.ModerationService;
import com.nyretha.moderation.util.TimeParser;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MuteCommand implements CommandExecutor {

    private final ModerationService moderationService;

    public MuteCommand() {
        this.moderationService = new ModerationService();
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (args.length < 1) {
            sender.sendMessage(
                    ChatColor.RED +
                    "Usage: /mute <player> [time]"
            );
            return true;
        }

        String duration =
                args.length > 1
                        ? args[1]
                        : "permanent";

        if (
                !duration.equalsIgnoreCase("permanent")
                && TimeParser.parseToMillis(duration) <= 0
        ) {
            sender.sendMessage(
                    ChatColor.RED +
                    "Invalid mute duration. Examples: 10m, 2h, 7d."
            );
            return true;
        }

        moderationService.addInfraction(
                args[0],
                sender.getName(),
                duration,
                "MUTE"
        );

        sender.sendMessage(
                ChatColor.GREEN +
                "Muted " +
                args[0] +
                " for " +
                duration +
                "."
        );

        return true;
    }
}
