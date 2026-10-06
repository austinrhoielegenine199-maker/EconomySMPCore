package com.nyretha.team.service;

import com.nyretha.NyrethaCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TeamManager {

    public static class Team {
        private final String name;
        private final UUID owner;
        private final Set<UUID> members = new HashSet<>();
        private Location home;
        private boolean pvpEnabled = false;

        public Team(String name, UUID owner) {
            this.name = name;
            this.owner = owner;
            this.members.add(owner);
        }

        public String getName() { return name; }
        public UUID getOwner() { return owner; }
        public Set<UUID> getMembers() { return members; }
        public Location getHome() { return home; }
        public void setHome(Location home) { this.home = home; }
        public boolean isPvpEnabled() { return pvpEnabled; }
        public void setPvpEnabled(boolean pvpEnabled) { this.pvpEnabled = pvpEnabled; }
    }

    private final Map<String, Team> teamsByName = new HashMap<>();
    private final Map<UUID, Team> playerTeamMap = new HashMap<>();

    private File guiFile;
    private FileConfiguration guiConfig;
    private File teamsFile;
    private FileConfiguration teamsConfig;

    public TeamManager() {
        loadConfigs();
    }

    public void loadConfigs() {
        NyrethaCore plugin = NyrethaCore.getInstance();
        guiFile = new File(plugin.getDataFolder(), "core/team/teamgui.yml");
        if (!guiFile.exists()) {
            plugin.saveResource("core/team/teamgui.yml", false);
        }
        guiConfig = YamlConfiguration.loadConfiguration(guiFile);

        teamsFile = new File(plugin.getDataFolder(), "core/team/teams.yml");
        if (!teamsFile.exists()) {
            try {
                teamsFile.getParentFile().mkdirs();
                teamsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create teams.yml!");
            }
        }
        teamsConfig = YamlConfiguration.loadConfiguration(teamsFile);
        loadTeamsData();
    }

    public FileConfiguration getGuiConfig() { return guiConfig; }

    public boolean createTeam(String name, UUID owner) {
        if (teamsByName.containsKey(name.toLowerCase()) || playerTeamMap.containsKey(owner)) return false;

        Team team = new Team(name, owner);
        teamsByName.put(name.toLowerCase(), team);
        playerTeamMap.put(owner, team);
        saveTeamsData();
        return true;
    }

    public boolean isInTeam(UUID uuid) {
        return playerTeamMap.containsKey(uuid);
    }

    public String getTeamName(UUID uuid) {
        Team team = playerTeamMap.get(uuid);
        return team != null ? team.getName() : null;
    }

    public boolean hasTeamHome(String teamName) {
        Team team = teamsByName.get(teamName.toLowerCase());
        return team != null && team.getHome() != null;
    }

    public void setTeamHome(String teamName, Location loc) {
        Team team = teamsByName.get(teamName.toLowerCase());
        if (team != null) {
            team.setHome(loc);
            saveTeamsData();
        }
    }

    public Location getTeamHome(String teamName) {
        Team team = teamsByName.get(teamName.toLowerCase());
        return team != null ? team.getHome() : null;
    }

    public Team getTeamByPlayer(UUID uuid) {
        return playerTeamMap.get(uuid);
    }

    public void saveTeamsData() {
        teamsConfig.set("teams", null);
        for (Team team : teamsByName.values()) {
            String path = "teams." + team.getName();
            teamsConfig.set(path + ".owner", team.getOwner().toString());
            List<String> memberList = new ArrayList<>();
            for (UUID u : team.getMembers()) memberList.add(u.toString());
            teamsConfig.set(path + ".members", memberList);
            teamsConfig.set(path + ".pvp", team.isPvpEnabled());

            if (team.getHome() != null) {
                Location h = team.getHome();
                teamsConfig.set(path + ".home.world", h.getWorld().getName());
                teamsConfig.set(path + ".home.x", h.getX());
                teamsConfig.set(path + ".home.y", h.getY());
                teamsConfig.set(path + ".home.z", h.getZ());
                teamsConfig.set(path + ".home.yaw", h.getYaw());
                teamsConfig.set(path + ".home.pitch", h.getPitch());
            }
        }
        try {
            teamsConfig.save(teamsFile);
        } catch (IOException e) {
            NyrethaCore.getInstance().getLogger().severe("Could not save teams.yml!");
        }
    }

    private void loadTeamsData() {
        if (!teamsConfig.contains("teams")) return;
        var section = teamsConfig.getConfigurationSection("teams");
        if (section == null) return;

        for (String name : section.getKeys(false)) {
            UUID owner = UUID.fromString(section.getString(name + ".owner"));
            Team team = new Team(name, owner);
            team.setPvpEnabled(section.getBoolean(name + ".pvp", false));

            List<String> mems = section.getStringList(name + ".members");
            for (String s : mems) {
                UUID u = UUID.fromString(s);
                team.getMembers().add(u);
                playerTeamMap.put(u, team);
            }

            if (section.contains(name + ".home")) {
                World w = Bukkit.getWorld(section.getString(name + ".home.world", "world"));
                double x = section.getDouble(name + ".home.x");
                double y = section.getDouble(name + ".home.y");
                double z = section.getDouble(name + ".home.z");
                float yaw = (float) section.getDouble(name + ".home.yaw");
                float pitch = (float) section.getDouble(name + ".home.pitch");
                if (w != null) team.setHome(new Location(w, x, y, z, yaw, pitch));
            }

            teamsByName.put(name.toLowerCase(), team);
        }
    }
}
