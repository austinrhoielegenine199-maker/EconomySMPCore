package com.nyretha.moderation.command;

import com.nyretha.moderation.service.ModerationService;
import com.nyretha.moderation.util.TimeParser;
import com.nyretha.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class MuteCommand implements CommandExecutor {

    private final ModerationService moderationService;

    public MuteCommand(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nyretha.staff.mute")) {
            sender.sendMessage(ColorUtils.color("&cNo permission."));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ColorUtils.color("&cUsage: /mute <player> [time] [reason]"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        String targetName = target.getName() != null ? target.getName() : args[0];

        long durationMillis = -1;
        String reason = "Muted by staff.";

        if (args.length >= 2) {
            long parsedTime = TimeParser.parseTimeToMillis(args[1]);
            if (parsedTime > 0) {
                durationMillis = parsedTime;
                if (args.length > 2) {
                    reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                }
            } else {
                reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            }
        }

        moderationService.mutePlayer(target.getUniqueId(), durationMillis, reason);

        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().sendMessage(ColorUtils.color("&cYou have been muted. Reason: " + reason));
        }

        sender.sendMessage(ColorUtils.color("&aSuccessfully muted &b" + targetName));
        return true;
    }
}
