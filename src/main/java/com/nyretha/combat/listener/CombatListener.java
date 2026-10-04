package com.nyretha.combat.listener;

import com.nyretha.combat.service.CombatConfigService;
import com.nyretha.combat.service.CombatManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class CombatListener implements Listener {
    private final CombatManager combatManager;
    private final CombatConfigService configService;

    public CombatListener(CombatManager combatManager, CombatConfigService configService) {
        this.combatManager = combatManager;
        this.configService = configService;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player victim = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();

            combatManager.tagPlayer(victim);
            combatManager.tagPlayer(attacker);
        } else if (event.getEntity() instanceof Player && event.getDamager() instanceof org.bukkit.entity.Projectile) {
            org.bukkit.entity.Projectile proj = (org.bukkit.entity.Projectile) event.getDamager();
            if (proj.getShooter() instanceof Player) {
                Player victim = (Player) event.getEntity();
                Player attacker = (Player) proj.getShooter();

                if (!victim.equals(attacker)) {
                    combatManager.tagPlayer(victim);
                    combatManager.tagPlayer(attacker);
                }
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!combatManager.isInCombat(player.getUniqueId())) return;

        // Admin bypass check
        if (player.hasPermission("nyretha.combat.bypass")) {
            return; // Allows admins to use any command while in combat
        }

        String message = event.getMessage().substring(1).toLowerCase(); // remove '/'
        String cmdLabel = message.split(" ")[0];

        for (String blacklisted : configService.getBlacklistedCommands()) {
            if (cmdLabel.equals(blacklisted)) {
                event.setCancelled(true);
                String blockedMsg = configService.getMessage("command_blocked", "&cYou cannot use this command while in combat!");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', blockedMsg));
                return;
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!combatManager.isInCombat(player.getUniqueId())) return;

        // Admin bypass for elytra restriction as well
        if (player.hasPermission("nyretha.combat.bypass")) {
            return;
        }

        // Optional Elytra check
        if (configService.getOtherSetting("disable_elytra_in_combat", true)) {
            if (player.isGliding()) {
                player.setGliding(false);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot use Elytra while in combat!"));
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (combatManager.isInCombat(player.getUniqueId())) {
            // Admins with bypass won't get killed on quit even if tagged, or you can keep it standard for everyone:
            if (player.hasPermission("nyretha.combat.bypass")) {
                combatManager.untagPlayer(player);
                return;
            }

            // Combat logger punishment: kill player so their loot drops naturally where they left
            player.setHealth(0.0);
            combatManager.untagPlayer(player);
        }
    }
}
