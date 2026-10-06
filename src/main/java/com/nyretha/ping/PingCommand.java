package com.nyretha.ping;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PingCommand implements CommandExecutor {

    private final PingConfig pingConfig;

    private static final Pattern HEX_PATTERN =
            Pattern.compile("#([A-Fa-f0-9]{6})");

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
                            "ping.messages.player-only",
                            "&cOnly players can use this command."
                    )
            ));
            return true;
        }

        if (!pingConfig.getConfig().getBoolean(
                "ping.enabled",
                true
        )) {
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

        if (pingConfig.getConfig().getBoolean(
                "ping.display.chat",
                true
        )) {
            String message = pingConfig.getConfig().getString(
                    "ping.messages.chat",
                    "&7You have #009bff%ping%&7ms"
            );

            message = message
                    .replace("%ping%", String.valueOf(ping))
                    .replace("%player%", player.getName());

            player.sendMessage(color(message));
        }

        if (pingConfig.getConfig().getBoolean(
                "ping.display.actionbar",
                true
        )) {
            String message = pingConfig.getConfig().getString(
                    "ping.messages.actionbar",
                    "&7You have #009bff%ping%&7ms"
            );

            message = message
                    .replace("%ping%", String.valueOf(ping))
                    .replace("%player%", player.getName());

            player.sendActionBar(parse(message));
        }

        if (pingConfig.getConfig().getBoolean(
                "ping.sound.enabled",
                false
        )) {
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

    private Component parse(String message) {
        Component component = Component.empty();

        Matcher matcher = HEX_PATTERN.matcher(message);

        int lastEnd = 0;

        while (matcher.find()) {
            String before = message.substring(
                    lastEnd,
                    matcher.start()
            );

            if (!before.isEmpty()) {
                component = component.append(
                        legacyComponent(before)
                );
            }

            String hex = matcher.group(1);

            component = component.append(
                    Component.text("")
                            .color(TextColor.fromHexString("#" + hex))
            );

            lastEnd = matcher.end();
        }

        if (lastEnd < message.length()) {
            component = component.append(
                    legacyComponent(
                            message.substring(lastEnd)
                    )
            );
        }

        return component;
    }

    private Component legacyComponent(String message) {
        String converted = ChatColor.translateAlternateColorCodes(
                '&',
                message
        );

        Component result = Component.empty();

        StringBuilder current = new StringBuilder();
        ChatColor currentColor = ChatColor.WHITE;

        for (int i = 0; i < converted.length(); i++) {
            char character = converted.charAt(i);

            if (character == ChatColor.COLOR_CHAR &&
                    i + 1 < converted.length()) {

                if (current.length() > 0) {
                    result = result.append(
                            Component.text(current.toString())
                                    .color(
                                            TextColor.fromHexString(
                                                    currentColor == ChatColor.WHITE
                                                            ? "#FFFFFF"
                                                            : currentColorToHex(currentColor)
                                            )
                                    )
                    );

                    current.setLength(0);
                }

                char code = Character.toLowerCase(
                        converted.charAt(++i)
                );

                currentColor = getChatColor(code);
                continue;
            }

            current.append(character);
        }

        if (current.length() > 0) {
            result = result.append(
                    Component.text(current.toString())
                            .color(
                                    TextColor.fromHexString(
                                            currentColor == ChatColor.WHITE
                                                    ? "#FFFFFF"
                                                    : currentColorToHex(currentColor)
                                    )
                            )
            );
        }

        return result;
    }

    private ChatColor getChatColor(char code) {
        return switch (code) {
            case '0' -> ChatColor.BLACK;
            case '1' -> ChatColor.DARK_BLUE;
            case '2' -> ChatColor.DARK_GREEN;
            case '3' -> ChatColor.DARK_AQUA;
            case '4' -> ChatColor.DARK_RED;
            case '5' -> ChatColor.DARK_PURPLE;
            case '6' -> ChatColor.GOLD;
            case '7' -> ChatColor.GRAY;
            case '8' -> ChatColor.DARK_GRAY;
            case '9' -> ChatColor.BLUE;
            case 'a' -> ChatColor.GREEN;
            case 'b' -> ChatColor.AQUA;
            case 'c' -> ChatColor.RED;
            case 'd' -> ChatColor.LIGHT_PURPLE;
            case 'e' -> ChatColor.YELLOW;
            case 'f' -> ChatColor.WHITE;
            default -> ChatColor.WHITE;
        };
    }

    private String currentColorToHex(ChatColor color) {
        return switch (color) {
            case BLACK -> "#000000";
            case DARK_BLUE -> "#0000AA";
            case DARK_GREEN -> "#00AA00";
            case DARK_AQUA -> "#00AAAA";
            case DARK_RED -> "#AA0000";
            case DARK_PURPLE -> "#AA00AA";
            case GOLD -> "#FFAA00";
            case GRAY -> "#AAAAAA";
            case DARK_GRAY -> "#555555";
            case BLUE -> "#5555FF";
            case GREEN -> "#55FF55";
            case AQUA -> "#55FFFF";
            case RED -> "#FF5555";
            case LIGHT_PURPLE -> "#FF55FF";
            case YELLOW -> "#FFFF55";
            case WHITE -> "#FFFFFF";
            default -> "#FFFFFF";
        };
    }

    private String color(String message) {
        if (message == null) {
            return "";
        }

        Matcher matcher = HEX_PATTERN.matcher(message);

        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group(1);

            StringBuilder replacement = new StringBuilder(
                    ChatColor.COLOR_CHAR + "x"
            );

            for (char character : hex.toCharArray()) {
                replacement
                        .append(ChatColor.COLOR_CHAR)
                        .append(character);
            }

            matcher.appendReplacement(
                    result,
                    Matcher.quoteReplacement(
                            replacement.toString()
                    )
            );
        }

        matcher.appendTail(result);

        return ChatColor.translateAlternateColorCodes(
                '&',
                result.toString()
        );
    }
}
