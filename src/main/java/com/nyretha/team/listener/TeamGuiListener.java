package com.nyretha.team.listener;

import com.nyretha.home.listener.HomeGuiListener;
import com.nyretha.team.service.TeamManager;
import com.nyretha.team.service.TeamManager.Team;
import com.nyretha.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class TeamGuiListener implements Listener {

    private final TeamManager teamManager;

    public TeamGuiListener(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    public static class TeamGuiHolder implements InventoryHolder {
        private final int page;
        public TeamGuiHolder(int page) { this.page = page; }
        public int getPage() { return page; }
        @Override public Inventory getInventory() { return null; }
    }

    public static void openTeamGui(Player player, TeamManager teamManager, int page) {
        FileConfiguration config = teamManager.getGuiConfig();
        Team team = teamManager.getTeamByPlayer(player.getUniqueId());

        if (team == null) {
            player.sendMessage(ColorUtils.color("&cYou are not in a team!"));
            return;
        }

        String title = ColorUtils.color(config.getString("Title", "ᴛᴇᴀᴍ (Page %page%)").replace("%page%", String.valueOf(page)));
        int size = config.getInt("size", 54);

        Inventory inv = Bukkit.createInventory(new TeamGuiHolder(page), size, title);

        // Fill Team Members
        List<UUID> members = new ArrayList<>(team.getMembers());
        int slotIndex = 0;
        int startIndex = (page - 1) * 45;

        for (int i = startIndex; i < members.size() && slotIndex < 45; i++) {
            OfflinePlayer member = Bukkit.getOfflinePlayer(members.get(i));
            boolean isOnline = member.isOnline();

            String color = config.getString("member." + (isOnline ? "online-color" : "offline-color"), "#00FC88");
            String statusText = config.getString("member." + (isOnline ? "online-text" : "offline-text"), isOnline ? "Online" : "Offline");
            String leaderTag = member.getUniqueId().equals(team.getOwner()) ? config.getString("member.leader-tag", " [Leader]") : "";

            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ColorUtils.color(config.getString("items.member.name", "%status_color%■ #00FC88%player%")
                        .replace("%status_color%", color)
                        .replace("%player%", member.getName() + leaderTag)));

                List<String> lore = new ArrayList<>();
                for (String l : config.getStringList("items.member.lore")) {
                    lore.add(ColorUtils.color(l.replace("%status%", statusText)));
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(slotIndex++, item);
        }

        // Set Bottom Controls
        setItemFromConfig(inv, config, "items.search");
        setItemFromConfig(inv, config, "items.sort");
        setItemFromConfig(inv, config, "items.back");
        setItemFromConfig(inv, config, "items.info", "%team_name%", team.getName());
        setItemFromConfig(inv, config, "items.next");

        // Team Home
        if (team.getHome() != null) {
            setItemFromConfig(inv, config, "items.home", "lore-set");
        } else {
            setItemFromConfig(inv, config, "items.home", "lore-unset");
        }

        // PvP Button
        String pvpStatus = team.isPvpEnabled() ? config.getString("items.pvp.on-text", "ON") : config.getString("items.pvp.off-text", "OFF");
        setItemFromConfig(inv, config, "items.pvp", "%status%", pvpStatus);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getInventory().getHolder() instanceof TeamGuiHolder holder) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            FileConfiguration config = teamManager.getGuiConfig();
            Team team = teamManager.getTeamByPlayer(player.getUniqueId());
            if (team == null) return;

            if (slot == config.getInt("items.pvp.slot", 53)) {
                if (!team.getOwner().equals(player.getUniqueId())) {
                    player.sendMessage(ColorUtils.color("&cOnly team owners can toggle PvP!"));
                    return;
                }
                team.setPvpEnabled(!team.isPvpEnabled());
                teamManager.saveTeamsData();
                openTeamGui(player, teamManager, holder.getPage());
            } else if (slot == config.getInt("items.home.slot", 52)) {
                if (team.getHome() != null) {
                    player.closeInventory();
                    HomeGuiListener.startTeleportCountdown(player, team.getHome(), config);
                } else {
                    player.sendMessage(ColorUtils.color(config.getString("messages.no-home", "&cYour team does not have a home.")));
                }
            } else if (slot == config.getInt("items.next.slot", 50)) {
                openTeamGui(player, teamManager, holder.getPage() + 1);
            } else if (slot == config.getInt("items.back.slot", 48) && holder.getPage() > 1) {
                openTeamGui(player, teamManager, holder.getPage() - 1);
            }
        }
    }

    private static void setItemFromConfig(Inventory inv, FileConfiguration config, String path, String... replacements) {
        if (!config.getBoolean(path + ".enabled", true)) return;

        int slot = config.getInt(path + ".slot");
        String matStr = config.getString(path + ".material", "STONE");
        String name = config.getString(path + ".name", "");

        List<String> lore = config.contains(path + "." + (replacements.length > 0 && replacements[0].startsWith("lore-") ? replacements[0] : "lore"))
                ? config.getStringList(path + "." + (replacements.length > 0 && replacements[0].startsWith("lore-") ? replacements[0] : "lore"))
                : new ArrayList<>();

        for (int i = 0; i < replacements.length - 1; i += 2) {
            name = name.replace(replacements[i], replacements[i + 1]);
            List<String> updated = new ArrayList<>();
            for (String l : lore) updated.add(l.replace(replacements[i], replacements[i + 1]));
            lore = updated;
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
        inv.setItem(slot, item);
    }
}
