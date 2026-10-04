package com.nyretha.tp.command;

import com.nyretha.tp.gui.TpConfirmGui;
import com.nyretha.tp.service.TpConfigService;
import com.nyretha.tp.service.TpTaskService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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

    // Helper method to resolve partial/short player names efficiently
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

        // 1. Handling /tpauto
        if (cmdName.equals("tpauto")) {
            UUID uuid = player.getUniqueId();
            if (tpAutoEnabled.contains(uuid)) {
                tpAutoEnabled.remove(uuid);
                String msg = configService.getMessage("tpauto_disabled", "&cYou have disabled Tpauto!");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            } else {
                tpAutoEnabled.add(uuid);
                String msg = configService.getMessage("tpauto_enabled", "&aYou have enabled Tpauto!");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            return true;
        }

        // 2. Handling /tp (Admin Instant Teleport)
        if (cmdName.equals("tp")) {
            if (!player.hasPermission("nyretha.tp.admin")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have permission to use /tp!"));
                return true;
            }

            if (args.length == 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /tp <player> [target]"));
                return true;
            }

            if (args.length == 1) {
                Player target = findPlayer(args[0]);
                if (target == null || !target.isOnline()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer not found or offline!"));
                    return true;
                }
                player.teleport(target.getLocation());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTeleported to &b" + target.getName() + "&a!"));
                return true;
            }

            if (args.length >= 2) {
                Player target1 = findPlayer(args[0]);
                Player target2 = findPlayer(args[1]);
                if (target1 == null || target2 == null || !target1.isOnline() || !target2.isOnline()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cOne or both players not found or offline!"));
                    return true;
                }
                target1.teleport(target2.getLocation());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTeleported &b" + target1.getName() + " &ato &b" + target2.getName() + "&a!"));
                return true;
            }
            return true;
        }

        // 3. Handling /tpa or /tpahere (with shortcut/short name support)
        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /" + label + " <player>"));
            return true;
        }

        Player target = findPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlayer not found or offline!"));
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot teleport to yourself!"));
            return true;
        }

        String type = cmdName.startsWith("tpah") ? "tpahere" : "tpa";

        // If target has tpauto active, skip GUI and trigger countdown instantly
        if (tpAutoEnabled.contains(target.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + target.getName() + " has Tpauto enabled. Teleporting..."));
            
            Player traveler = type.equals("tpa") ? player : target;
            Player destinationHolder = type.equals("tpa") ? target : player;

            TpTaskService.startTeleportCountdown(traveler, destinationHolder.getLocation(), plugin, configService);
            return true;
        }

        // Open 3-row confirmation GUI
        activeSessions.put(player.getUniqueId(), new PendingTpSession(target.getUniqueId(), type));
        TpConfirmGui.open(player, target, type);
        return true;
    }

    public static class PendingTpSession {
        public final UUID targetUuid;
        public final String type;
        public PendingTpSession(UUID targetUuid, String type) {
            this.targetUuid = targetUuid;
            this.type = type;
        }
    }
}
