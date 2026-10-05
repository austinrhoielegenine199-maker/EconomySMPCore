package com.nyretha;

import com.nyretha.command.ArchiveCommand;
import com.nyretha.database.DatabaseService;
import com.nyretha.economy.command.EconomyCommand;
import com.nyretha.help.HelpCommand;
import com.nyretha.home.command.HomeCommand;
import com.nyretha.moderation.command.BanCommand;
import com.nyretha.moderation.command.MuteCommand;
import com.nyretha.moderation.command.TempBanCommand;
import com.nyretha.moderation.command.WarnCommand;
import com.nyretha.nv.NvCommand;
import com.nyretha.ping.PingCommand;
import com.nyretha.shop.command.ShopCommand;
import com.nyretha.team.command.TeamCommand;
import com.nyretha.tp.command.TpCommand;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class NyrethaCore extends JavaPlugin {

    private static NyrethaCore instance;
    private DatabaseService databaseService;

    @Override
    public void onEnable() {

        instance = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        extractAllConfigs();

        databaseService = new DatabaseService(this);

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

        register("archiveaustxn", new ArchiveCommand(this));

        register("team", new TeamCommand(this));

        register("home", new HomeCommand(this));

        register("bal", new EconomyCommand(this));
        register("pay", new EconomyCommand(this));
        register("eco", new EconomyCommand(this));

        register("nv", new NvCommand(this));

        register("shop", new ShopCommand(this));

        register("ping", new PingCommand(this));

        register("ban", new BanCommand(this));

        register("tempban", new TempBanCommand(this));

        register("mute", new MuteCommand(this));

        register("warn", new WarnCommand(this));

        register("help", new HelpCommand(this));

        register("tp", new TpCommand(this));
    }

    private void register(String name, Object executor) {

        if (getCommand(name) == null) {
            getLogger().severe(
                    "Command /" + name +
                    " is missing from plugin.yml!"
            );
            return;
        }

        if (executor instanceof CommandExecutor) {
            getCommand(name).setExecutor(
                    (CommandExecutor) executor
            );
        }

        if (executor instanceof TabCompleter) {
            getCommand(name).setTabCompleter(
                    (TabCompleter) executor
            );
        }

        getLogger().info("Registered /" + name);
    }

    private void extractAllConfigs() {

        saveConfigSafely("core/combat/combat.yml");

        saveConfigSafely("core/eco/economy.yml");

        saveConfigSafely("core/help/help.yml");

        saveConfigSafely("core/home/homegui.yml");

        saveConfigSafely(
                "core/moderation/staffmoderation.yml"
        );

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

        File file =
                new File(getDataFolder(), resourcePath);

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
