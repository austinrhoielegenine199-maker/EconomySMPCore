package com.nyretha.ping;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand implements CommandExecutor {

    private final PingConfig pingConfig;

    public PingCommand(PingConfig pingConfig) {
        this.pingConfig = pingConfig;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(color(
                    pingConfig.getConfig().getString(
                            "messages.player-only",
                            "&cOnly players can use this command."
                    )
            ));
            return true;
        }

        if (!pingConfig.getConfig().getBoolean("ping.enabled", true)) {
            player.sendMessage(color(
                    pingConfig.getConfig().getString(
                            "ping.messages.disabled",
                            "&cThe ping command is currently disabled."
                    )
            ));
            return true;
        }

        if (!player.hasPermission("donutsmpcore.ping")) {
            player.sendMessage(color(
                    pingConfig.getConfig().getString(
                            "ping.messages.no-permission",
                            "&cYou don't have permission to use this command."
                    )
            ));
            return true;
        }

        int ping = player.getPing();

        if (pingConfig.getConfig().getBoolean("ping.display.chat", true)) {
            String message = pingConfig.getConfig().getString(
                    "ping.messages.chat",
                    "&7You have #009bff%ping%&7ms"
            );

            message = message
                    .replace("%ping%", String.valueOf(ping))
                    .replace("%player%", player.getName());

            player.sendMessage(color(message));
        }

        if (pingConfig.getConfig().getBoolean("ping.display.actionbar", true)) {
            String message = pingConfig.getConfig().getString(
                    "ping.messages.actionbar",
                    "&7You have #009bff%ping%&7ms"
            );

            message = message
                    .replace("%ping%", String.valueOf(ping))
                    .replace("%player%", player.getName());

            player.sendActionBar(Component.text(color(message)));
        }

        if (pingConfig.getConfig().getBoolean("ping.sound.enabled", false)) {
            String soundName = pingConfig.getConfig().getString(
                    "ping.sound.name",
                    "ENTITY_EXPERIENCE_ORB_PICKUP"
            );

            try {
                float volume = (float) pingConfig.getConfig()
                        .getDouble("ping.sound.volume", 1.0);

                float pitch = (float) pingConfig.getConfig()
                        .getDouble("ping.sound.pitch", 1.0);

                player.playSound(
                        player.getLocation(),
                        org.bukkit.Sound.valueOf(soundName.toUpperCase()),
                        volume,
                        pitch
                );
            } catch (IllegalArgumentException ignored) {
            }
        }

        return true;
    }

    private String color(String message) {
        if (message == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {
            char character = message.charAt(i);

            if (character == '#' && i + 6 < message.length()) {
                String hex = message.substring(i + 1, i + 7);

                if (hex.matches("[A-Fa-f0-9]{6}")) {
                    result.append(ChatColor.COLOR_CHAR).append('x');

                    for (char hexCharacter : hex.toCharArray()) {
                        result.append(ChatColor.COLOR_CHAR)
                                .append(hexCharacter);
                    }

                    i += 6;
                    continue;
                }
            }

            result.append(character);
        }

        return ChatColor.translateAlternateColorCodes('&', result.toString());
    }
}
