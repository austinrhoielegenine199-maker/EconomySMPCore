package com.nyretha.home.command;

import com.nyretha.home.listener.HomeRenameListener;
import com.nyretha.home.model.HomeManager;
import com.nyretha.home.service.HomeConfig;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeCommand implements CommandExecutor {
    private final HomeManager homeManager;
    private final HomeConfig homeConfig;
    private final Plugin plugin;
    private final Map<UUID, BukkitTask> activeTeleports = new HashMap<>();
    private final Map<UUID, Location> teleportPositions = new HashMap<>();

    public HomeCommand(HomeManager homeManager, HomeConfig homeConfig, Plugin plugin) {
        this.homeManager = homeManager;
        this.homeConfig = homeConfig;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        String cmdName = command.getName().toLowerCase();

        if (cmdName.equals("sethome")) {
            String homeName = "home";
            if (args.length > 0) {
                String arg = args[0];
                try {
                    int slotNum = Integer.parseInt(arg);
                    if (slotNum < 1 || slotNum > 5) {
                        player.sendMessage("§cHome slots must be between 1 and 5!");
                        return true;
                    }
                    homeName = String.valueOf(slotNum);
                } catch (NumberFormatException e) {
                    homeName = arg;
                }
            }

            homeManager.setHome(player.getUniqueId(), homeName, player.getLocation());
            player.sendMessage("§aHome §e" + homeName + " §aset successfully!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            return true;
        }

        if (cmdName.equals("home")) {
            String homeName = "home";
            if (args.length > 0) {
                String arg = args[0];
                try {
                    int slotNum = Integer.parseInt(arg);
                    if (slotNum >= 1 && slotNum <= 5) {
                        homeName = String.valueOf(slotNum);
                    } else {
                        homeName = arg;
                    }
                } catch (NumberFormatException e) {
                    homeName = arg;
                }
            }

            Location targetLoc = homeManager.getHome(player.getUniqueId(), homeName);

            if (targetLoc == null) {
                player.sendMessage("§cHome §e" + homeName + " §cnot found.");
                return true;
            }

            int countdownSeconds = homeConfig.getTeleportCountdown();
            if (countdownSeconds <= 0) {
                player.teleport(targetLoc);
                String msg = homeConfig.getMessage("teleported");
                if (msg != null && !msg.isEmpty()) player.sendMessage(msg);
                return true;
            }

            if (activeTeleports.containsKey(player.getUniqueId())) {
                player.sendMessage("§cYou are already teleporting!");
                return true;
            }

            teleportPositions.put(player.getUniqueId(), player.getLocation().clone());
            player.sendMessage(homeConfig.getMessage("countdown").replace("%seconds%", String.valueOf(countdownSeconds)));

            BukkitTask task = new BukkitRunnable() {
                int timeLeft = countdownSeconds;

                @Override
                public void run() {
                    Location startLoc = teleportPositions.get(player.getUniqueId());
                    if (startLoc == null || player.getLocation().distanceSquared(startLoc) > 0.23) {
                        activeTeleports.remove(player.getUniqueId());
                        teleportPositions.remove(player.getUniqueId());
                        player.sendMessage(homeConfig.getMessage("cancelled"));
                        cancel();
                        return;
                    }

                    timeLeft--;
                    if (timeLeft <= 0) {
                        activeTeleports.remove(player.getUniqueId());
                        teleportPositions.remove(player.getUniqueId());
                        player.teleport(targetLoc);
                        String msg = homeConfig.getMessage("teleported");
                        if (msg != null && !msg.isEmpty()) player.sendMessage(msg);
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 20L, 20L);

            activeTeleports.put(player.getUniqueId(), task);
            return true;
        }

        return false;
    }
}
