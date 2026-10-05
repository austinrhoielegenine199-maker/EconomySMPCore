package com.nyretha.tp.command;

import com.nyretha.tp.service.TpConfigService;
import com.nyretha.tp.service.TpTaskService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TpCommand implements CommandExecutor {
    private final TpConfigService configService;
    private final Plugin plugin;
    private final Set<UUID> tpAutoEnabled = new HashSet<>();

    public static final Map<UUID, PendingTpSession> activeSessions = new HashMap<>();

    public TpCommand(TpConfigService configService, Plugin plugin) {
        this.configService = configService;
        this.plugin = plugin;
    }

    private Player findPlayer(String nameQuery) {
        String lowerQuery = nameQuery.toLowerCase();
        Player exactMatch = Bukkit.getPlayerExact(nameQuery);
        if (exactMatch != null) return exactMatch;

        Player bestMatch = null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().toLowerCase().startsWith(lowerQuery)) {
                bestMatch = p;
                break;
            }
        }
        return bestMatch;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use teleport commands.");
            return true;
        }

        Player player = (Player) sender;
        String cmdName = command.getName().toLowerCase();

        if (cmdName.equals("tpauto")) {
            UUID uuid = player.getUniqueId();
            if (tpAutoEnabled.contains(uuid)) {
                tpAutoEnabled.remove(uuid);
                player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', configService.getMessage("tpauto_disabled", "&cYou have disabled Tpauto!")));
            } else {
                tpAutoEnabled.add(uuid);
                player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', configService.getMessage("tpauto_enabled", "&aYou have enabled Tpauto!")));
            }
            return true;
        }

        if (cmdName.equals("tp")) {
            if (!player.hasPermission("nyretha.tp.admin")) {
                player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&cYou do not have permission to use /tp!"));
                return true;
            }

            if (args.length == 0) {
                player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&cUsage: /tp <player> [target]"));
                return true;
            }

            if (args.length == 1) {
                Player target = findPlayer(args[0]);
                if (target == null || !target.isOnline()) {
                    player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&cPlayer not found or offline!"));
                    return true;
                }
                player.teleport(target.getLocation());
                player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&aTeleported to &b" + target.getName() + "&a!"));
                return true;
            }

            if (args.length >= 2) {
                Player target1 = findPlayer(args[0]);
                Player target2 = findPlayer(args[1]);
                if (target1 == null || target2 == null || !target1.isOnline() || !target2.isOnline()) {
                    player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&cOne or both players not found or offline!"));
                    return true;
                }
                target1.teleport(target2.getLocation());
                player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&aTeleported &b" + target1.getName() + " &ato &b" + target2.getName() + "&a!"));
                return true;
            }
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&cUsage: /" + label + " <player>"));
            return true;
        }

        Player target = findPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&cPlayer not found or offline!"));
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&cYou cannot teleport to yourself!"));
            return true;
        }

        String type = cmdName.startsWith("tpah") ? "tpahere" : "tpa";

        if (tpAutoEnabled.contains(target.getUniqueId())) {
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&a" + target.getName() + " has Tpauto enabled. Teleporting..."));

            Player traveler = type.equals("tpa") ? player : target;
            Player destinationHolder = type.equals("tpa") ? target : player;

            TpTaskService.startTeleportCountdown(traveler, destinationHolder.getLocation(), plugin, configService);
            return true;
        }

        activeSessions.put(target.getUniqueId(), new PendingTpSession(player.getUniqueId(), type));

        String rawMsg = configService.getMessage("tpa_received", "&b%target% &esent you a &b%type% &erequest! &#009bff&l[Click Me]");
        String parsedMsg = rawMsg.replace("%target%", player.getName()).replace("%type%", type.toUpperCase());
        String[] parts = parsedMsg.split("&#009bff&l\\[Click Me\\]");

        TextComponent finalMessage = new TextComponent();

        if (parts.length > 0) {
            for (BaseComponent comp : TextComponent.fromLegacyText(org.bukkit.ChatColor.translateAlternateColorCodes('&', parts[0]))) {
                finalMessage.addExtra(comp);
            }
        }

        TextComponent clickButton = new TextComponent("[Click Me]");
        clickButton.setColor(ChatColor.of(new Color(0, 155, 255)));
        clickButton.setBold(true);
        clickButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + player.getName()));
        clickButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to open confirmation GUI").create()));

        finalMessage.addExtra(clickButton);

        if (parts.length > 1) {
            for (BaseComponent comp : TextComponent.fromLegacyText(org.bukkit.ChatColor.translateAlternateColorCodes('&', parts[1]))) {
                finalMessage.addExtra(comp);
            }
        }

        target.spigot().sendMessage(finalMessage);
        player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', configService.getMessage("tpa_sent", "&eSent a teleport request to &b%target%&e.").replace("%target%", target.getName())));

        return true;
    }

    public static class PendingTpSession {
        public final UUID requesterUuid;
        public final String type;
        public PendingTpSession(UUID requesterUuid, String type) {
            this.requesterUuid = requesterUuid;
            this.type = type;
        }
    }
}
