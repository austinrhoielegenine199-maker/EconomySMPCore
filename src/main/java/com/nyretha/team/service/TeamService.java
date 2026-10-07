package com.nyretha.team.service;

import com.nyretha.team.model.Team;
import com.nyretha.team.model.TeamMember;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeamService {

    private final JavaPlugin plugin;
    private final Map<String, Team> teamsByName = new ConcurrentHashMap<>();
    private final Map<UUID, String> playerTeams = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, Long>> pendingInvites = new ConcurrentHashMap<>();
    private String databaseUrl;

    public TeamService(JavaPlugin plugin) {
        this.plugin = plugin;
        initDatabase();
    }

    private void initDatabase() {
        File folder = new File(plugin.getDataFolder(), "core/team");
        if (!folder.exists()) folder.mkdirs();

        File dbFile = new File(folder, "database.db");
        this.databaseUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        try (Connection conn = DriverManager.getConnection(databaseUrl);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS teams (" +
                    "name VARCHAR(32) PRIMARY KEY, " +
                    "owner VARCHAR(36), " +
                    "pvp BOOLEAN, " +
                    "world VARCHAR(64), x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT);");

            stmt.execute("CREATE TABLE IF NOT EXISTS team_members (" +
                    "team_name VARCHAR(32), " +
                    "uuid VARCHAR(36), " +
                    "name VARCHAR(16), " +
                    "rank VARCHAR(16), " +
                    "can_set_home BOOLEAN, " +
                    "can_access_home BOOLEAN, " +
                    "can_manage_members BOOLEAN, " +
                    "PRIMARY KEY (team_name, uuid));");

        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to initialize team database.db: " + e.getMessage());
        }

        loadAllTeamsAsync();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }

    public void loadAllTeamsAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = getConnection()) {
                // Load Teams
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM teams;")) {
                    teamsByName.clear();
                    while (rs.next()) {
                        String name = rs.getString("name");
                        UUID owner = UUID.fromString(rs.getString("owner"));
                        Team team = new Team(name, owner);
                        team.setPvpEnabled(rs.getBoolean("pvp"));

                        String world = rs.getString("world");
                        if (world != null && Bukkit.getWorld(world) != null) {
                            Location home = new Location(
                                    Bukkit.getWorld(world),
                                    rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"),
                                    rs.getFloat("yaw"), rs.getFloat("pitch")
                            );
                            team.setHome(home);
                        }
                        teamsByName.put(name.toLowerCase(), team);
                    }
                }

                // Load Members
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM team_members;")) {
                    playerTeams.clear();
                    while (rs.next()) {
                        String teamName = rs.getString("team_name");
                        Team team = teamsByName.get(teamName.toLowerCase());
                        if (team != null) {
                            UUID uuid = UUID.fromString(rs.getString("uuid"));
                            String name = rs.getString("name");
                            String rank = rs.getString("rank");

                            TeamMember member = new TeamMember(uuid, name, rank);
                            member.setCanSetHome(rs.getBoolean("can_set_home"));
                            member.setCanAccessHome(rs.getBoolean("can_access_home"));
                            member.setCanManageMembers(rs.getBoolean("can_manage_members"));

                            team.addMember(member);
                            playerTeams.put(uuid, teamName);
                        }
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to load teams from database.db: " + e.getMessage());
            }
        });
    }

    public boolean isInTeam(Player player) {
        return playerTeams.containsKey(player.getUniqueId());
    }

    public Team getTeam(Player player) {
        String teamName = playerTeams.get(player.getUniqueId());
        return teamName != null ? teamsByName.get(teamName.toLowerCase()) : null;
    }

    public Team getTeamByName(String name) {
        return teamsByName.get(name.toLowerCase());
    }

    public boolean hasTeamHome(Player player) {
        Team team = getTeam(player);
        return team != null && team.getHome() != null;
    }

    public Location getTeamHome(Player player) {
        Team team = getTeam(player);
        return team != null ? team.getHome() : null;
    }

    public void setTeamHome(Player player, Location location) {
        Team team = getTeam(player);
        if (team == null) return;

        team.setHome(location);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "UPDATE teams SET world=?, x=?, y=?, z=?, yaw=?, pitch=? WHERE name=?;";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, location.getWorld().getName());
                stmt.setDouble(2, location.getX());
                stmt.setDouble(3, location.getY());
                stmt.setDouble(4, location.getZ());
                stmt.setFloat(5, location.getYaw());
                stmt.setFloat(6, location.getPitch());
                stmt.setString(7, team.getName());
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save team home: " + e.getMessage());
            }
        });
    }

    public void createTeam(Player owner, String name) {
        Team team = new Team(name, owner.getUniqueId());
        TeamMember ownerMember = new TeamMember(owner.getUniqueId(), owner.getName(), "OWNER");
        team.addMember(ownerMember);

        teamsByName.put(name.toLowerCase(), team);
        playerTeams.put(owner.getUniqueId(), name);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = getConnection()) {
                try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO teams(name, owner, pvp) VALUES(?, ?, ?);")) {
                    stmt.setString(1, name);
                    stmt.setString(2, owner.getUniqueId().toString());
                    stmt.setBoolean(3, false);
                    stmt.executeUpdate();
                }
                saveMemberAsync(conn, name, ownerMember);
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to create team: " + e.getMessage());
            }
        });
    }

    public void deleteTeam(Team team) {
        teamsByName.remove(team.getName().toLowerCase());
        for (UUID uuid : team.getMembers().keySet()) {
            playerTeams.remove(uuid);
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = getConnection()) {
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM teams WHERE name=?;")) {
                    stmt.setString(1, team.getName());
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM team_members WHERE team_name=?;")) {
                    stmt.setString(1, team.getName());
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to delete team: " + e.getMessage());
            }
        });
    }

    public void sendInvite(Team team, Player target) {
        pendingInvites.computeIfAbsent(target.getUniqueId(), k -> new ConcurrentHashMap<>())
                .put(team.getName().toLowerCase(), System.currentTimeMillis() + 60000); // 60s invite
    }

    public boolean hasInvite(Player player, String teamName) {
        Map<String, Long> invites = pendingInvites.get(player.getUniqueId());
        if (invites == null || !invites.containsKey(teamName.toLowerCase())) return false;

        if (System.currentTimeMillis() > invites.get(teamName.toLowerCase())) {
            invites.remove(teamName.toLowerCase());
            return false;
        }
        return true;
    }

    public void joinTeam(Player player, Team team) {
        TeamMember member = new TeamMember(player.getUniqueId(), player.getName(), "MEMBER");
        team.addMember(member);
        playerTeams.put(player.getUniqueId(), team.getName());

        Map<String, Long> invites = pendingInvites.get(player.getUniqueId());
        if (invites != null) invites.remove(team.getName().toLowerCase());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = getConnection()) {
                saveMemberAsync(conn, team.getName(), member);
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save team member: " + e.getMessage());
            }
        });
    }

    public void leaveTeam(Player player) {
        Team team = getTeam(player);
        if (team == null) return;

        team.removeMember(player.getUniqueId());
        playerTeams.remove(player.getUniqueId());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM team_members WHERE team_name=? AND uuid=?;")) {
                stmt.setString(1, team.getName());
                stmt.setString(2, player.getUniqueId().toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to remove member: " + e.getMessage());
            }
        });
    }

    private void saveMemberAsync(Connection conn, String teamName, TeamMember member) throws SQLException {
        String sql = "INSERT INTO team_members(team_name, uuid, name, rank, can_set_home, can_access_home, can_manage_members) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(team_name, uuid) DO UPDATE SET rank=excluded.rank, " +
                "can_set_home=excluded.can_set_home, can_access_home=excluded.can_access_home, " +
                "can_manage_members=excluded.can_manage_members;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, teamName);
            stmt.setString(2, member.getUuid().toString());
            stmt.setString(3, member.getName());
            stmt.setString(4, member.getRank());
            stmt.setBoolean(5, member.canSetHome());
            stmt.setBoolean(6, member.canAccessHome());
            stmt.setBoolean(7, member.canManageMembers());
            stmt.executeUpdate();
        }
    }
}
