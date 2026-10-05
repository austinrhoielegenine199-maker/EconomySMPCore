package com.nyretha;

import com.nyretha.core.database.DatabaseService;
import com.nyretha.commands.ArchiveAustxnCommand;
import com.nyretha.commands.TeamCommand;
import com.nyretha.commands.HomeCommand;
import com.nyretha.commands.SetHomeCommand;
import com.nyretha.commands.BalCommand;
import com.nyretha.commands.PayCommand;
import com.nyretha.commands.EcoCommand;
import com.nyretha.commands.TpaCommand;
import com.nyretha.commands.TpaHereCommand;
import com.nyretha.commands.TpCommand;
import com.nyretha.commands.NvCommand;
import com.nyretha.commands.ShopCommand;
import com.nyretha.commands.PingCommand;
import com.nyretha.commands.BanCommand;
import com.nyretha.commands.TempBanCommand;
import com.nyretha.commands.MuteCommand;
import com.nyretha.commands.WarnCommand;
import com.nyretha.commands.HelpCommand;
import com.nyretha.commands.CoreCommand;

import org.bukkit.plugin.java.JavaPlugin;

public final class NyrethaCore extends JavaPlugin {

    private static NyrethaCore instance;
    private DatabaseService databaseService;

    @Override
    public void onEnable() {

        instance = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Extract configuration files
        extractAllConfigs();

        // Initialize SQLite database
        databaseService = new DatabaseService(this);

        // Register all commands
        registerCommands();

        getLogger().info("=================================");
        getLogger().info("        NyrethaCore v1.0");
        getLogger().info("        Plugin Enabled!");
        getLogger().info("=================================");
    }

    @Override
    public void onDisable() {

        if (databaseService != null &&
                databaseService.getConnection() != null) {

            try {
                databaseService.getConnection().close();
            } catch (Exception e) {
                getLogger().warning(
                        "Could not close database connection: "
                                + e.getMessage()
                );
            }
        }

        getLogger().info("NyrethaCore has been disabled.");
    }

    private void registerCommands() {

        register("archiveaustxn", new ArchiveAustxnCommand(this));
        register("team", new TeamCommand(this));
        register("home", new HomeCommand(this));
        register("sethome", new SetHomeCommand(this));
        register("bal", new BalCommand(this));
        register("pay", new PayCommand(this));
        register("eco", new EcoCommand(this));
        register("tpa", new TpaCommand(this));
        register("tpahere", new TpaHereCommand(this));
        register("tp", new TpCommand(this));
        register("nv", new NvCommand(this));
        register("shop", new ShopCommand(this));
        register("ping", new PingCommand(this));
        register("ban", new BanCommand(this));
        register("tempban", new TempBanCommand(this));
        register("mute", new MuteCommand(this));
        register("warn", new WarnCommand(this));
        register("help", new HelpCommand(this));
        register("core", new CoreCommand(this));
    }

    private void register(String name, Object executor) {

        if (getCommand(name) == null) {
            getLogger().severe(
                    "Command /" + name +
                    " is missing from plugin.yml!"
            );
            return;
        }

        if (executor instanceof org.bukkit.command.CommandExecutor) {
            getCommand(name).setExecutor(
                    (org.bukkit.command.CommandExecutor) executor
            );
        }

        if (executor instanceof org.bukkit.command.TabCompleter) {
            getCommand(name).setTabCompleter(
                    (org.bukkit.command.TabCompleter) executor
            );
        }

        getLogger().info("Registered /" + name);
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

        saveConfigSafely(
                "core/shop/categories/gear.yml"
        );

        saveConfigSafely(
                "core/shop/categories/end.yml"
        );

        saveConfigSafely(
                "core/shop/categories/nether.yml"
        );

        saveConfigSafely(
                "core/shop/categories/food.yml"
        );

        saveConfigSafely(
                "core/shop/categories/flakeshop.yml"
        );
    }

    private void saveConfigSafely(String resourcePath) {

        java.io.File file =
                new java.io.File(getDataFolder(), resourcePath);

        if (file.getParentFile() != null &&
                !file.getParentFile().exists()) {

            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {

            try {

                saveResource(resourcePath, false);

            } catch (IllegalArgumentException e) {

                getLogger().warning(
                        "FAILED to find resource in jar: "
                                + resourcePath
                );
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
