package com.nyretha.moderation.command;

import com.nyretha.moderation.service.ConfigService;
import com.nyretha.moderation.service.WebhookService;
import com.nyretha.moderation.util.TimeParser;
import com.nyretha.utils.ColorUtils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Date;

public class TempBanCommand implements CommandExecutor {

    private final ConfigService configService;
    private final WebhookService webhookService;

    public TempBanCommand(ConfigService configService, WebhookService webhookService) {
        this.configService = configService;
        this.webhookService = webhookService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nyretha.staff.tempban")) {
            sender.sendMessage(ColorUtils.color("&cNo permission."));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ColorUtils.color("&cUsage: /tempban <player> <time> [reason]"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        String targetName = target.getName() != null ? target.getName() : args[0];

        long durationMillis = TimeParser.parseTimeToMillis(args[1]);
        if (durationMillis <= 0) {
            sender.sendMessage(ColorUtils.color("&cInvalid time format! Use 1d, 12h, 30m, etc."));
            return true;
        }

        Date expiration = new Date(System.currentTimeMillis() + durationMillis);
        String reason = (args.length > 2) ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "Temporarily banned by administrator.";

        Bukkit.getBanList(BanList.Type.NAME).addBan(
                targetName,
                reason,
                expiration,
                sender.getName()
        );

        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().kickPlayer(ColorUtils.color("&cYou have been temporarily banned.\nReason: " + reason + "\nExpires: " + expiration));
        }

        sender.sendMessage(ColorUtils.color("&aSuccessfully temporarily banned &b" + targetName + " &auntil &e" + expiration));
        return true;
    }
}
