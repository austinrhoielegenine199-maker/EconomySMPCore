package com.nyretha;

import com.nyretha.core.database.DatabaseService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class NyrethaCore extends JavaPlugin implements CommandExecutor {

    private static NyrethaCore instance;
    private DatabaseService databaseService;

    @Override
    public void onEnable() {
        instance = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // 1. Extract configs cleanly into the core folder
        extractAllConfigs();

        // 2. Initialize SQLite Database
        databaseService = new DatabaseService(this);

        // 3. Register Command Executors
        registerCommands();

        getLogger().info("NyrethaCore has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (databaseService != null && databaseService.getConnection() != null) {
            // Close database connection if applicable
        }
        getLogger().info("NyrethaCore has been disabled.");
    }

    private void registerCommands() {
        // Registering core commands to this main class handler or specific executors
        if (getCommand("archiveaustxn") != null) getCommand("archiveaustxn").setExecutor(this);
        if (getCommand("core") != null) getCommand("core").setExecutor(this);
        
        // Add other commands as you build their respective classes, e.g.:
        // if (getCommand("home") != null) getCommand("home").setExecutor(new HomeExecutor());
        // if (getCommand("help") != null) getCommand("help").setExecutor(new HelpExecutor());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        // Handle /archiveaustxn restriction
        if (command.getName().equalsIgnoreCase("archiveaustxn")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            
            Player player = (Player) sender;
            // Check if player name matches exactly (case-sensitive or insensitive)
            if (!player.getName().equalsIgnoreCase("ArchiveAustxn")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }
            
            player.sendMessage(ChatColor.GREEN + "Welcome back, developer ArchiveAustxn!");
            // TODO: Put your developer command logic here
            return true;
        }

        // Handle /core reload
        if (command.getName().equalsIgnoreCase("core")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("nyretha.admin")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to reload this plugin.");
                    return true;
                }
                
                reloadAllConfigs();
                sender.sendMessage(ChatColor.GREEN + "NyrethaCore configurations have been reloaded successfully!");
                return true;
            }
            sender.sendMessage(ChatColor.YELLOW + "Usage: /core reload");
            return true;
        }

        return false;
    }

    private void extractAllConfigs() {
        saveConfigSafely("core/combat/combat.yml");
        saveConfigSafely("core/eco/economy.yml");
        saveConfigSafely("core/help/help.yml");
        saveConfigSafely("core/home/homegui.yml");
        saveConfigSafely("core/moderation/staffmoderation.yml");
        saveConfigSafely("core/ping/ping.yml");
        saveConfigSafely("core/tp/tpasystem.yml");
        saveConfigSafely("core/shop/shopgui.yml");
        saveConfigSafely("core/shop/categories/gear.yml");
        saveConfigSafely("core/shop/categories/end.yml");
        saveConfigSafely("core/shop/categories/nether.yml");
        saveConfigSafely("core/shop/categories/food.yml");
        saveConfigSafely("core/shop/categories/flakeshop.yml");
    }

    private void reloadAllConfigs() {
        // Safe reload logic for core configuration mappings
        // You can reload individual YamlConfigurations here when you link them up
    }

    private void saveConfigSafely(String resourcePath) {
        File file = new File(getDataFolder(), resourcePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try {
                // Extracts cleanly straight to dataFolder + resourcePath (which includes "core/...")
                saveResource(resourcePath, false);
            } catch (IllegalArgumentException e) {
                getLogger().warning("FAILED to find resource in jar: " + resourcePath);
            }
        }
    }

    public static NyrethaCore getInstance() {
        return instance;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }
}
