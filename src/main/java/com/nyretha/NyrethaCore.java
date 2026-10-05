package com.nyretha;

import com.nyretha.combat.listener.CombatListener;
import com.nyretha.combat.service.CombatConfigService;
import com.nyretha.combat.service.CombatManager;
import com.nyretha.command.ArchiveCommand;
import com.nyretha.core.database.DatabaseService;
import com.nyretha.economy.command.EconomyCommand;
import com.nyretha.economy.service.EconomyConfigService;
import com.nyretha.help.HelpCommand;
import com.nyretha.help.HelpConfig;
import com.nyretha.home.command.HomeCommand;
import com.nyretha.home.listener.HomeMenuListener;
import com.nyretha.home.listener.HomeRenameListener;
import com.nyretha.home.model.HomeManager;
import com.nyretha.home.service.HomeConfig;
import com.nyretha.home.service.HomeGuiConfigService;
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
import com.nyretha.team.listener.TeamMenuListener;
import com.nyretha.team.service.TeamGuiConfigService;
import com.nyretha.team.service.TeamManager;
import com.nyretha.tp.command.TpCommand;
import com.nyretha.tp.listener.TpListener;
import com.nyretha.tp.service.TpConfigService;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

public final class NyrethaCore extends JavaPlugin {

    private static NyrethaCore instance;
    private DatabaseService databaseService;

    private EconomyConfigService economyConfigService;
    private HomeConfig homeConfig;
    private HomeManager homeManager;
    private HomeGuiConfigService homeGuiConfigService;
    private HelpConfig helpConfig;
    private PingConfig pingConfig;
    private TpConfigService tpConfigService;
    private ShopConfigService shopConfigService;
    private ConfigService moderationConfig;
    private ModerationService moderationService;
    private WebhookService webhookService;
    private TeamManager teamManager;
    private TeamGuiConfigService teamGuiConfigService;
    private CombatConfigService combatConfigService;
    private CombatManager combatManager;

    @Override
    public void onEnable() {
        instance = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        extractAllConfigs();

        databaseService = new DatabaseService(this);

        economyConfigService = new EconomyConfigService(this);
        economyConfigService.loadEconomyConfig();

        homeConfig = new HomeConfig();
        homeConfig.loadConfig();

        homeManager = new HomeManager();

        homeGuiConfigService = new HomeGuiConfigService();
        homeGuiConfigService.loadConfig();

        helpConfig = new HelpConfig(this);
        helpConfig.load();

        pingConfig = new PingConfig(this);
        pingConfig.load();

        tpConfigService = new TpConfigService();
        tpConfigService.loadConfig();

        shopConfigService = new ShopConfigService();
        shopConfigService.loadConfigs();

        moderationConfig = new ConfigService();
        moderationConfig.loadConfig();

        moderationService = new ModerationService();
        webhookService = new WebhookService(moderationConfig);

        teamManager = new TeamManager();

        teamGuiConfigService = new TeamGuiConfigService();
        teamGuiConfigService.loadConfig();

        combatConfigService = new CombatConfigService();
        combatConfigService.loadConfig();

        combatManager = new CombatManager(
                combatConfigService,
                this
        );

        registerCommands();
        registerListeners();

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
                new BukkitBanCommand()
        );

        register(
                "tempban",
                new BukkitTempBanCommand()
        );

        register(
                "mute",
                new BukkitMuteCommand()
        );

        register(
                "warn",
                new BukkitWarnCommand()
        );

        register(
                "help",
                new HelpCommand(helpConfig)
        );

        register(
                "core",
                new CoreCommand()
        );
    }

    private void registerListeners() {

        HelpCommand helpListener =
                new HelpCommand(helpConfig);

        getServer().getPluginManager().registerEvents(
                helpListener,
                this
        );

        getServer().getPluginManager().registerEvents(
                new HomeMenuListener(
                        homeManager,
                        homeGuiConfigService,
                        this
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                new HomeRenameListener(
                        homeManager,
                        this
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                new TeamMenuListener(
                        teamManager,
                        teamGuiConfigService
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                new TpListener(
                        tpConfigService,
                        this
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                new ShopMenuListener(
                        shopConfigService,
                        this
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                new CombatListener(
                        combatManager,
                        combatConfigService
                ),
                this
        );
    }

    private void register(
            String name,
            Object executor
    ) {

        Command command = getCommand(name);

        if (command == null) {
            getLogger().severe(
                    "Command /" + name +
                    " is missing from plugin.yml!"
            );
            return;
        }

        if (executor instanceof CommandExecutor) {
            command.setExecutor(
                    (CommandExecutor) executor
            );
        }

        if (executor instanceof TabCompleter) {
            command.setTabCompleter(
                    (TabCompleter) executor
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
                "core/home/home.yml"
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

        if (file.getParentFile() != null &&
                !file.getParentFile().exists()) {

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
                        "Resource not found: "
                                + resourcePath
                );
            }
        }
    }

    public void reloadCore() {

        extractAllConfigs();

        economyConfigService.reloadConfig();

        homeConfig.loadConfig();

        homeGuiConfigService.loadConfig();

        helpConfig.reload();

        pingConfig.reload();

        tpConfigService.loadConfig();

        shopConfigService.loadConfigs();

        moderationConfig.loadConfig();

        teamGuiConfigService.loadConfig();

        combatConfigService.loadConfig();
    }

    public static NyrethaCore getInstance() {
        return instance;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    private final class CoreCommand
            implements CommandExecutor, TabCompleter {

        @Override
        public boolean onCommand(
                CommandSender sender,
                Command command,
                String label,
                String[] args
        ) {

            if (!sender.hasPermission(
                    "nyretha.admin"
            )) {

                sender.sendMessage(
                        "§cYou do not have permission."
                );

                return true;
            }

            if (args.length == 1 &&
                    args[0].equalsIgnoreCase("reload")) {

                reloadCore();

                sender.sendMessage(
                        "§aNyrethaCore configuration reloaded."
                );

                return true;
            }

            sender.sendMessage(
                    "§eUsage: /core reload"
            );

            return true;
        }

        @Override
        public java.util.List<String> onTabComplete(
                CommandSender sender,
                Command command,
                String alias,
                String[] args
        ) {

            if (args.length == 1) {
                return java.util.List.of("reload");
            }

            return java.util.List.of();
        }
    }

    private final class BukkitBanCommand
            implements CommandExecutor {

        @Override
        public boolean onCommand(
                CommandSender sender,
                Command command,
                String label,
                String[] args
        ) {

            if (!sender.hasPermission(
                    "nyretha.staff.ban"
            )) {

                sender.sendMessage(
                        "§cYou do not have permission."
                );

                return true;
            }

            if (args.length < 1) {

                sender.sendMessage(
                        "§cUsage: /ban <player> [reason]"
                );

                return true;
            }

            String target = args[0];

            String reason =
                    args.length > 1
                            ? String.join(
                                    " ",
                                    Arrays.copyOfRange(
                                            args,
                                            1,
                                            args.length
                                    )
                            )
                            : "No reason provided";

            Player player =
                    Bukkit.getPlayerExact(target);

            String kickMessage =
                    moderationConfig.getFormattedMessage(
                            "ban",
                            Map.of(
                                    "{reason}",
                                    reason,
                                    "{duration}",
                                    "Permanent"
                            )
                    );

            Bukkit.getBanList(
                    BanList.Type.NAME
            ).addBan(
                    target,
                    reason,
                    null,
                    sender.getName()
            );

            if (player != null) {

                player.kickPlayer(
                        ChatColor.translateAlternateColorCodes(
                                '&',
                                kickMessage
                        )
                );
            }

            new BanCommand(
                    moderationConfig,
                    webhookService
            ).execute(
                    sender.getName(),
                    target,
                    reason
            );

            sender.sendMessage(
                    "§aPermanently banned §e"
                            + target
                            + "§a."
            );

            return true;
        }
    }

    private final class BukkitTempBanCommand
            implements CommandExecutor {

        @Override
        public boolean onCommand(
                CommandSender sender,
                Command command,
                String label,
                String[] args
        ) {

            if (!sender.hasPermission(
                    "nyretha.staff.tempban"
            )) {

                sender.sendMessage(
                        "§cYou do not have permission."
                );

                return true;
            }

            if (args.length < 2) {

                sender.sendMessage(
                        "§cUsage: /tempban <player> <time> [reason]"
                );

                return true;
            }

            String target = args[0];
            String duration = args[1];

            String reason =
                    args.length > 2
                            ? String.join(
                                    " ",
                                    Arrays.copyOfRange(
                                            args,
                                            2,
                                            args.length
                                    )
                            )
                            : "No reason provided";

            long millis =
                    com.nyretha.moderation.util.TimeParser
                            .parseToMillis(duration);

            if (millis <= 0) {

                sender.sendMessage(
                        "§cInvalid time. Example: 10m, 2h, 7d, 1w"
                );

                return true;
            }

            String kickMessage =
                    moderationConfig.getFormattedMessage(
                            "tempban",
                            Map.of(
                                    "{reason}",
                                    reason,
                                    "{duration}",
                                    duration
                            )
                    );

            java.util.Date expires =
                    new java.util.Date(
                            System.currentTimeMillis()
                                    + millis
                    );

            Bukkit.getBanList(
                    BanList.Type.NAME
            ).addBan(
                    target,
                    reason,
                    expires,
                    sender.getName()
            );

            Player player =
                    Bukkit.getPlayerExact(target);

            if (player != null) {

                player.kickPlayer(
                        ChatColor.translateAlternateColorCodes(
                                '&',
                                kickMessage
                        )
                );
            }

            new TempBanCommand(
                    moderationConfig,
                    webhookService
            ).execute(
                    sender.getName(),
                    target,
                    duration,
                    reason
            );

            sender.sendMessage(
                    "§aTemporarily banned §e"
                            + target
                            + " §afor §e"
                            + duration
                            + "§a."
            );

            return true;
        }
    }

    private final class BukkitMuteCommand
            implements CommandExecutor {

        @Override
        public boolean onCommand(
                CommandSender sender,
                Command command,
                String label,
                String[] args
        ) {

            if (!sender.hasPermission(
                    "nyretha.staff.mute"
            )) {

                sender.sendMessage(
                        "§cYou do not have permission."
                );

                return true;
            }

            if (args.length < 2) {

                sender.sendMessage(
                        "§cUsage: /mute <player> <time> [reason]"
                );

                return true;
            }

            String target = args[0];
            String duration = args[1];

            String reason =
                    args.length > 2
                            ? String.join(
                                    " ",
                                    Arrays.copyOfRange(
                                            args,
                                            2,
                                            args.length
                                    )
                            )
                            : "No reason provided";

            long millis =
                    com.nyretha.moderation.util.TimeParser
                            .parseToMillis(duration);

            if (millis <= 0) {

                sender.sendMessage(
                        "§cInvalid mute time."
                );

                return true;
            }

            new MuteCommand().execute(
                    sender.getName(),
                    target,
                    duration,
                    reason
            );

            sender.sendMessage(
                    "§aMuted §e"
                            + target
                            + " §afor §e"
                            + duration
                            + "§a."
            );

            return true;
        }
    }

    private final class BukkitWarnCommand
            implements CommandExecutor {

        @Override
        public boolean onCommand(
                CommandSender sender,
                Command command,
                String label,
                String[] args
        ) {

            if (!sender.hasPermission(
                    "nyretha.staff.warn"
            )) {

                sender.sendMessage(
                        "§cYou do not have permission."
                );

                return true;
            }

            if (args.length < 1) {

                sender.sendMessage(
                        "§cUsage: /warn <player> [reason]"
                );

                return true;
            }

            String target = args[0];

            String reason =
                    args.length > 1
                            ? String.join(
                                    " ",
                                    Arrays.copyOfRange(
                                            args,
                                            1,
                                            args.length
                                    )
                            )
                            : "No reason provided";

            new WarnCommand(
                    moderationService
            ).execute(
                    sender.getName(),
                    target,
                    reason
            );

            Player player =
                    Bukkit.getPlayerExact(target);

            if (player != null) {

                player.sendMessage(
                        "§c§lWARNING §7You have been warned by §e"
                                + sender.getName()
                                + "§7. Reason: §f"
                                + reason
                );
            }

            sender.sendMessage(
                    "§aWarned §e"
                            + target
                            + "§a."
            );

            return true;
        }
    }
}
