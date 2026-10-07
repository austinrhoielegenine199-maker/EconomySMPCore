package com.nyretha.moderation.command;

import com.nyretha.moderation.service.ConfigService;
import com.nyretha.moderation.service.WebhookService;
import com.nyretha.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class BanCommand implements CommandExecutor {

    private final ConfigService configService;
    private final WebhookService webhookService;

    public BanCommand(ConfigService configService, WebhookService webhookService) {
        this.configService = configService;
        this.webhookService = webhookService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nyretha.staff.ban")) {
            sender.sendMessage(ColorUtils.color("&cNo permission."));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ColorUtils.color("&cUsage: /ban <player> [reason]"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        String reason = (args.length > 1) ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "Banned by administrator.";

        Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(
                target.getName() != null ? target.getName() : args[0],
                reason,
                null,
                sender.getName()
        );

        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().kickPlayer(ColorUtils.color("&cYou have been banned.\nReason: " + reason));
        }

        sender.sendMessage(ColorUtils.color("&aSuccessfully banned &b" + (target.getName() != null ? target.getName() : args[0])));
        return true;
    }
}
