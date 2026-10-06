package com.nyretha.home.listener;

import com.nyretha.NyrethaCore;
import com.nyretha.home.model.HomeManager;
import com.nyretha.home.service.HomeConfig;
import com.nyretha.team.service.TeamManager;
import com.nyretha.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class HomeGuiListener implements Listener {

    private final NyrethaCore plugin;
    private final HomeManager homeManager;
    private final HomeConfig homeConfig;
    private final TeamManager teamManager;

    private static final Map<UUID, BukkitTask> pendingTeleports = new HashMap<>();
    private static final Map<UUID, Location> startLocations = new HashMap<>();

    public HomeGuiListener(NyrethaCore plugin, HomeManager homeManager, HomeConfig homeConfig, TeamManager teamManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
        this.homeConfig = homeConfig;
        this.teamManager = teamManager;
    }

    public static class HomeHolder implements InventoryHolder {
        @Override public Inventory getInventory() { return null; }
    }

    public static class DeleteHolder implements InventoryHolder {
        private final String homeName;
        private final int homeNum;

        public DeleteHolder(String homeName, int homeNum) {
            this.homeName = homeName;
            this.homeNum = homeNum;
        }

        public String getHomeName() { return homeName; }
        public int getHomeNum() { return homeNum; }
        @Override public Inventory getInventory() { return null; }
    }

    public static void openHomeGui(Player player, HomeConfig homeConfig, HomeManager homeManager, TeamManager teamManager) {
        FileConfiguration config = homeConfig.getGuiConfig();
        String title = ColorUtils.color(config.getString("gui.title", "ʜᴏᴍᴇѕ"));
        int rows = config.getInt("gui.rows", 4);

        Inventory inv = Bukkit.createInventory(new HomeHolder(), rows * 9, title);
        Map<String, Location> userHomes = homeManager.getHomes(player.getUniqueId());

        List<Integer> bedSlots = config.getIntegerList("slots.beds");
        List<Integer> dyeSlots = config.getIntegerList("slots.dyes");

        for (int i = 0; i < 5; i++) {
            int bedSlot = bedSlots.size() > i ? bedSlots.get(i) : 12 + i;
            int dyeSlot = dyeSlots.size() > i ? dyeSlots.get(i) : 21 + i;
            int homeNumber = i + 1;
            String homeKey = "home" + homeNumber;

            boolean hasPerm = player.hasPermission("nyretha.homes." + homeNumber) || homeNumber == 1;

            if (!hasPerm) {
                inv.setItem(bedSlot, createConfigItem(config, "items.no-permission-bed", "{number}", String.valueOf(homeNumber)));
                inv.setItem(dyeSlot, createConfigItem(config, "items.no-permission-dye", "{number}", String.valueOf(homeNumber)));
            } else if (userHomes.containsKey(homeKey)) {
                inv.setItem(bedSlot, createConfigItem(config, "items.existing-bed", "{number}", String.valueOf(homeNumber), "{name}", ""));
                inv.setItem(dyeSlot, createConfigItem(config, "items.existing-dye", "{number}", String.valueOf(homeNumber), "{name}", ""));
            } else {
                inv.setItem(bedSlot, createConfigItem(config, "items.empty-bed", "{number}", String.valueOf(homeNumber)));
                inv.setItem(dyeSlot, createConfigItem(config, "items.empty-dye", "{number}", String.valueOf(homeNumber)));
            }
        }

        // Team Banner & Dye slots
        int teamBannerSlot = config.getInt("slots.team-banner", 10);
        int teamDyeSlot = config.getInt("slots.team-dye", 19);

        var team = teamManager.getTeamByPlayer(player.getUniqueId());
        if (team == null) {
            inv.setItem(teamBannerSlot, createConfigItem(config, "items.team-banner-no-team"));
            inv.setItem(teamDyeSlot, createConfigItem(config, "items.team-dye-no-team"));
        } else {
            Location teamHome = teamManager.getTeamHome(team.getName());
            if (teamHome != null) {
                inv.setItem(teamBannerSlot, createConfigItem(config, "items.team-banner-has-home"));
                boolean isOwner = team.getOwner().equals(player.getUniqueId());
                inv.setItem(teamDyeSlot, createConfigItem(config, isOwner ? "items.team-dye-has-home-manageable" : "items.team-dye-has-home-no-permission"));
            } else {
                inv.setItem(teamBannerSlot, createConfigItem(config, "items.team-banner-no-home"));
                inv.setItem(teamDyeSlot, createConfigItem(config, "items.team-dye-no-home"));
            }
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getInventory().getHolder() instanceof HomeHolder) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            FileConfiguration config = homeConfig.getGuiConfig();

            List<Integer> bedSlots = config.getIntegerList("slots.beds");
            List<Integer> dyeSlots = config.getIntegerList("slots.dyes");

            for (int i = 0; i < 5; i++) {
                int homeNum = i + 1;
                String homeKey = "home" + homeNum;

                if (bedSlots.size() > i && slot == bedSlots.get(i)) {
                    Location loc = homeManager.getHome(player.getUniqueId(), homeKey);
                    if (loc != null) {
                        player.closeInventory();
                        startTeleportCountdown(player, loc, homeConfig.getGuiConfig());
                    } else {
                        homeManager.setHome(player.getUniqueId(), homeKey, player.getLocation());
                        homeConfig.saveHome(player.getUniqueId(), homeKey, player.getLocation());
                        player.sendMessage(ColorUtils.color("&aHome " + homeNum + " set successfully!"));
                        openHomeGui(player, homeConfig, homeManager, teamManager);
                    }
                    return;
                }

                if (dyeSlots.size() > i && slot == dyeSlots.get(i)) {
                    if (homeManager.getHome(player.getUniqueId(), homeKey) != null) {
                        openDeleteConfirmGui(player, homeKey, homeNum);
                    }
                    return;
                }
            }

            int teamBannerSlot = config.getInt("slots.team-banner", 10);
            if (slot == teamBannerSlot) {
                var team = teamManager.getTeamByPlayer(player.getUniqueId());
                if (team != null) {
                    Location tHome = teamManager.getTeamHome(team.getName());
                    if (tHome != null) {
                        player.closeInventory();
                        startTeleportCountdown(player, tHome, homeConfig.getGuiConfig());
                    } else {
                        player.performCommand("team sethome");
                    }
                }
            }
        } else if (event.getInventory().getHolder() instanceof DeleteHolder holder) {
            event.setCancelled(true);
            int slot = event.getRawSlot();

            if (slot == 11) { // Cancel
                openHomeGui(player, homeConfig, homeManager, teamManager);
            } else if (slot == 15) { // Confirm Delete
                homeManager.deleteHome(player.getUniqueId(), holder.getHomeName());
                homeConfig.removeHome(player.getUniqueId(), holder.getHomeName());
                player.sendMessage(ColorUtils.color("&eHome deleted successfully."));
                openHomeGui(player, homeConfig, homeManager, teamManager);
            }
        }
    }

    private void openDeleteConfirmGui(Player player, String homeName, int homeNum) {
        FileConfiguration config = homeConfig.getGuiConfig();
        String title = ColorUtils.color(config.getString("delete-confirm.title", "Confirm Delete"));
        int rows = config.getInt("delete-confirm.rows", 3);

        Inventory inv = Bukkit.createInventory(new DeleteHolder(homeName, homeNum), rows * 9, title);

        inv.setItem(config.getInt("delete-confirm.cancel.slot", 11), createConfigItem(config, "delete-confirm.cancel"));
        inv.setItem(config.getInt("delete-confirm.home-display.slot", 13), createConfigItem(config, "delete-confirm.home-display", "{number}", String.valueOf(homeNum), "{name}", ""));
        inv.setItem(config.getInt("delete-confirm.confirm.slot", 15), createConfigItem(config, "delete-confirm.confirm"));

        player.openInventory(inv);
    }

    public static void startTeleportCountdown(Player player, Location targetLoc, FileConfiguration config) {
        int delay = config.getInt("teleport.countdown-seconds", 5);
        if (delay <= 0) {
            player.teleport(targetLoc);
            player.sendMessage(ColorUtils.color(config.getString("teleport.teleported", "&7Teleported.")));
            return;
        }

        UUID uuid = player.getUniqueId();
        cancelTeleport(uuid, false);

        startLocations.put(uuid, player.getLocation());

        BukkitTask task = new BukkitRunnable() {
            int secondsLeft = delay;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancelTeleport(uuid, false);
                    return;
                }

                if (secondsLeft <= 0) {
                    player.teleport(targetLoc);
                    player.sendMessage(ColorUtils.color(config.getString("teleport.teleported", "&7Teleported.")));
                    cancelTeleport(uuid, false);
                    return;
                }

                String msg = config.getString("teleport.countdown", "&7Teleporting in %seconds%s")
                        .replace("%seconds%", String.valueOf(secondsLeft));
                player.sendMessage(ColorUtils.color(msg));

                secondsLeft--;
            }
        }.runTaskTimer(NyrethaCore.getInstance(), 0L, 20L);

        pendingTeleports.put(uuid, task);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (pendingTeleports.containsKey(uuid)) {
            Location from = startLocations.get(uuid);
            Location to = event.getTo();
            if (from != null && to != null && (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ())) {
                cancelTeleport(uuid, true);
                FileConfiguration config = homeConfig.getGuiConfig();
                player.sendMessage(ColorUtils.color(config.getString("teleport.cancelled", "&cTeleport cancelled because you moved.")));
            }
        }
    }

    public static void cancelTeleport(UUID uuid, boolean cancelTask) {
        if (cancelTask && pendingTeleports.containsKey(uuid)) {
            pendingTeleports.get(uuid).cancel();
        }
        pendingTeleports.remove(uuid);
        startLocations.remove(uuid);
    }

    private static ItemStack createConfigItem(FileConfiguration config, String path, String... replacements) {
        String matStr = config.getString(path + ".material", "STONE");
        String name = config.getString(path + ".name", "");
        List<String> lore = config.getStringList(path + ".lore");

        for (int i = 0; i < replacements.length; i += 2) {
            name = name.replace(replacements[i], replacements[i + 1]);
            List<String> updatedLore = new ArrayList<>();
            for (String line : lore) {
                updatedLore.add(line.replace(replacements[i], replacements[i + 1]));
            }
            lore = updatedLore;
        }

        ItemStack item = new ItemStack(Material.matchMaterial(matStr.toUpperCase()) != null ? Material.matchMaterial(matStr.toUpperCase()) : Material.STONE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtils.color(name));
            List<String> coloredLore = new ArrayList<>();
            for (String l : lore) coloredLore.add(ColorUtils.color(l));
            meta.setLore(coloredLore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
