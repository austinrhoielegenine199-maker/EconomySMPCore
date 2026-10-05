package com.nyretha;

import com.nyretha.command.ArchiveCommand;
import com.nyretha.core.database.DatabaseService;
import com.nyretha.economy.command.EconomyCommand;
import com.nyretha.economy.service.EconomyConfigService;
import com.nyretha.help.HelpCommand;
import com.nyretha.help.HelpConfig;
import com.nyretha.home.command.HomeCommand;
import com.nyretha.home.model.HomeManager;
import com.nyretha.home.service.HomeConfig;
import com.nyretha.moderation.command.BanCommand;
import com.nyretha.moderation.command.MuteCommand;
import com.nyretha.moderation.command.TempBanCommand;
import com.nyretha.moderation.command.WarnCommand;
import com.nyretha.moderation.service.ConfigService;
import com.nyretha.moderation.service.ModerationService;
import com.nyretha.moderation.service.WebhookService;
import com.nyretha.nv.NvCommand;
import com.nyretha.ping.PingCommand;
import com.nyretha.ping.PingConfig;
import com.nyretha.shop.command.ShopCommand;
import com.nyretha.shop.listener.ShopMenuListener;
import com.nyretha.shop.service.ShopConfigService;
import com.nyretha.team.command.TeamCommand;
import com.nyretha.team.service.TeamManager;
import com.nyretha.tp.command.TpCommand;
import com.nyretha.tp.listener.TpListener;
import com.nyretha.tp.service.TpConfigService;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class NyrethaCore extends JavaPlugin {

    private static NyrethaCore instance;

    private DatabaseService databaseService;
    private EconomyConfigService economyConfigService;

    private HomeConfig homeConfig;
    private HomeManager homeManager;

    private HelpConfig helpConfig;

    private PingConfig pingConfig;

    private TpConfigService tpConfigService;

    private ShopConfigService shopConfigService;

    private ConfigService moderationConfig;
    private ModerationService moderationService;
    private WebhookService webhookService;

    private TeamManager teamManager;

    @Override
    public void onEnable() {

        instance = this;

        getDataFolder().mkdirs();

        extractAllConfigs();

        databaseService = new DatabaseService(this);

        economyConfigService = new EconomyConfigService(this);

        homeManager = new HomeManager();
        homeConfig = new HomeConfig(this);

        helpConfig = new HelpConfig(this);

        pingConfig = new PingConfig(this);

        tpConfigService = new TpConfigService();
        tpConfigService.loadConfig();

        shopConfigService = new ShopConfigService();
        shopConfigService.loadConfigs();

        moderationConfig = new ConfigService(this);
        moderationConfig.loadConfig();

        moderationService = new ModerationService();

        webhookService = new WebhookService(moderationConfig);

        teamManager = new TeamManager();

        registerCommands();

        getServer().getPluginManager().registerEvents(
                new HelpCommand(helpConfig),
                this
        );

        getServer().getPluginManager().registerEvents(
                new TpListener(tpConfigService, this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new ShopMenuListener(shopConfigService, this),
                this
        );

        getLogger().info("=================================");
        getLogger().info("        NyrethaCore v1.0");
        getLogger().info("        Plugin Enabled!");
        getLogger().info("=================================");
    }

    @Override
    public void onDisable() {

        if (databaseService != null) {
            databaseService.closeConnection();
        }

        getLogger().info("NyrethaCore has been disabled.");
    }

    private void registerCommands() {

        register(
                "archiveaustxn",
                new ArchiveCommand()
        );

        register(
                "team",
                new TeamCommand(teamManager)
        );

        HomeCommand homeCommand =
                new HomeCommand(
                        homeManager,
                        homeConfig,
                        this
                );

        register("home", homeCommand);
        register("sethome", homeCommand);

        EconomyCommand economyCommand =
                new EconomyCommand(
                        this,
                        economyConfigService
                );

        register("bal", economyCommand);
        register("pay", economyCommand);
        register("eco", economyCommand);

        TpCommand tpCommand =
                new TpCommand(
                        tpConfigService,
                        this
                );

        register("tpa", tpCommand);
        register("tpahere", tpCommand);
        register("tp", tpCommand);

        register(
                "nv",
                new NvCommand()
        );

        register(
                "shop",
                new ShopCommand(
                        shopConfigService,
                        getDataFolder()
                )
        );

        register(
                "ping",
                new PingCommand(pingConfig)
        );

        register(
                "ban",
                new BanCommand(
                        moderationConfig,
                        webhookService
                )
        );

        register(
                "tempban",
                new TempBanCommand(
                        moderationConfig,
                        webhookService,
                        this
                )
        );

        register(
                "mute",
                new MuteCommand(
                        moderationService
                )
        );

        register(
                "warn",
                new WarnCommand(
                        moderationService
                )
        );

        register(
                "help",
                new HelpCommand(helpConfig)
        );

        register(
                "core",
                new CoreCommand(this)
        );
    }

    private void register(
            String name,
            Object executor
    ) {

        if (getCommand(name) == null) {

            getLogger().severe(
                    "Command /" + name +
                    " is missing from plugin.yml!"
            );

            return;
        }

        if (executor instanceof CommandExecutor commandExecutor) {

            getCommand(name).setExecutor(
                    commandExecutor
            );
        }

        if (executor instanceof TabCompleter tabCompleter) {

            getCommand(name).setTabCompleter(
                    tabCompleter
            );
        }

        getLogger().info(
                "Registered /" + name
        );
    }

    private void extractAllConfigs() {

        saveConfigSafely(
                "core/combat/combat.yml"
        );

        saveConfigSafely(
                "core/eco/economy.yml"
        );

        saveConfigSafely(
                "core/help/help.yml"
        );

        saveConfigSafely(
                "core/home/homegui.yml"
        );

        saveConfigSafely(
                "core/moderation/staffmoderation.yml"
        );

        saveConfigSafely(
                "core/ping/ping.yml"
        );

        saveConfigSafely(
                "core/tp/tpasystem.yml"
        );

        saveConfigSafely(
                "core/shop/shopgui.yml"
        );

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

    private void saveConfigSafely(
            String resourcePath
    ) {

        File file =
                new File(
                        getDataFolder(),
                        resourcePath
                );

        if (file.getParentFile() != null) {

            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {

            try {

                saveResource(
                        resourcePath,
                        false
                );

            } catch (IllegalArgumentException e) {

                getLogger().warning(
                        "FAILED to find resource in jar: "
                                + resourcePath
                );
            }
        }
    }

    public void reloadCore() {

        extractAllConfigs();

        economyConfigService.reloadConfig();

        homeConfig.loadConfig();

        helpConfig.reload();

        pingConfig.reload();

        tpConfigService.loadConfig();

        shopConfigService.loadConfigs();

        moderationConfig.loadConfig();
    }

    public DatabaseService getDatabaseService() {

        return databaseService;
    }

    public static NyrethaCore getInstance() {

        return instance;
    }
}
