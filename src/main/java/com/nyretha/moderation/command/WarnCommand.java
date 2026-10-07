package com.nyretha.moderation.command;

import com.nyretha.moderation.service.ModerationService;
import com.nyretha.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class WarnCommand implements CommandExecutor {

    private final ModerationService moderationService;

    public WarnCommand(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nyretha.staff.warn")) {
            sender.sendMessage(ColorUtils.color("&cNo permission."));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ColorUtils.color("&cUsage: /warn <player> [reason]"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        String targetName = target.getName() != null ? target.getName() : args[0];
        String reason = (args.length > 1) ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "Warned by staff.";

        moderationService.warnPlayer(target.getUniqueId(), sender.getName(), reason);

        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().sendMessage(ColorUtils.color("&cYou have received a warning: &f" + reason));
        }

        sender.sendMessage(ColorUtils.color("&aWarned &b" + targetName + " &afor: &f" + reason));
        return true;
    }
}
