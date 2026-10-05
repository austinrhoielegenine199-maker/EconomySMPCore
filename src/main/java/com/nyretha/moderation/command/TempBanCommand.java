package com.nyretha.moderation.command;

import com.nyretha.moderation.service.ConfigService;
import com.nyretha.moderation.service.WebhookService;
import com.nyretha.moderation.util.TimeParser;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Map;

public class TempBanCommand implements CommandExecutor {

    private final ConfigService configService;
    private final WebhookService webhookService;

    public TempBanCommand(
            ConfigService configService,
            WebhookService webhookService
    ) {
        this.configService = configService;
        this.webhookService = webhookService;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (args.length < 2) {
            sender.sendMessage(
                    ChatColor.RED +
                    "Usage: /tempban <player> <time> [reason]"
            );
            return true;
        }

        String target = args[0];
        String duration = args[1];

        long millis =
                TimeParser.parseToMillis(duration);

        if (millis <= 0) {
            sender.sendMessage(
                    ChatColor.RED +
                    "Invalid duration. Examples: 10m, 2h, 7d."
            );
            return true;
        }

        String reason =
                args.length > 2
                        ? String.join(
                                " ",
                                java.util.Arrays.copyOfRange(
                                        args,
                                        2,
                                        args.length
                                )
                        )
                        : "Temporarily banned by staff";

        Date expiry =
                new Date(
                        System.currentTimeMillis() + millis
                );

        Bukkit.getBanList(
                BanList.Type.NAME
        ).addBan(
                target,
                reason,
                expiry,
                sender.getName()
        );

        Map<String, String> placeholders =
                Map.of(
                        "{reason}", reason,
                        "{duration}", duration
                );

        String kickMessage =
                configService.getFormattedMessage(
                        "tempban",
                        placeholders
                );

        Player player =
                Bukkit.getPlayerExact(target);

        if (player != null) {
            player.kickPlayer(
                    ChatColor.translateAlternateColorCodes(
                            '&',
                            kickMessage
                    )
            );
        }

        sender.sendMessage(
                ChatColor.GREEN +
                "Temporarily banned " +
                target +
                " for " +
                duration +
                "."
        );

        webhookService.sendBanLog(
                sender.getName(),
                target,
                duration,
                reason,
                "TEMPBAN"
        );

        return true;
    }
}
