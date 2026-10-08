package com.nyretha;

import com.nyretha.command.CoreCommand;
import com.nyretha.flakes.command.FlakesCommand;
import com.nyretha.flakes.service.FlakesService;
import com.nyretha.home.command.HomeCommand;
import com.nyretha.home.listener.HomeListener;
import com.nyretha.home.service.HomeService;
import com.nyretha.home.service.HomeTeleportService;
import com.nyretha.pay.command.PayCommand;
import com.nyretha.pay.listener.PayListener;
import com.nyretha.pay.service.PayService;
import com.nyretha.rtp.command.RTPCommand;
import com.nyretha.rtp.listener.RTPListener;
import com.nyretha.sell.command.SellCommand;
import com.nyretha.sell.listener.SellListener;
import com.nyretha.sell.manager.PriceManager;
import com.nyretha.sell.manager.SellManager;
import com.nyretha.sell.manager.WorthManager;
import com.nyretha.shop.flakes.FlakesManager;
import com.nyretha.shop.utils.EconomyManager;
import com.nyretha.shop.utils.LangManager;
import com.nyretha.team.command.TeamCommand;
import com.nyretha.team.listener.TeamListener;
import com.nyretha.team.service.TeamService;
import com.nyretha.tools.command.ToolsCommand;
import com.nyretha.tools.listener.ToolListener;
import com.nyretha.tpa.command.TPACommand;
import com.nyretha.tpa.command.TPAcceptCommand;
import com.nyretha.tpa.listener.TPAListener;
import com.nyretha.tpa.service.TPAService;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin {

    private static Core instance;

    private TeamService teamService;
    private HomeService homeService;
    private HomeTeleportService homeTeleportService;
    private FlakesService flakesService;
    private FlakesManager flakesManager;
    private LangManager langManager;
    private PayService payService;
    private EconomyManager economyManager;
    private WorthManager worthManager;
    private SellManager sellManager;
    private PriceManager priceManager;
    private TPAService tpaService;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResourceFiles();

        this.teamService = new TeamService(this);
        this.homeService = new HomeService(this);
        this.homeTeleportService = new HomeTeleportService(this);
        this.flakesService = new FlakesService(this);
        this.flakesManager = new FlakesManager(this);
        this.langManager = new LangManager(this);
        this.payService = new PayService(this);
        this.economyManager = new EconomyManager(this);
        this.worthManager = new WorthManager(this);
        this.sellManager = new SellManager(worthManager, economyManager);

        // --- LINE 67 PROPERLY INSTANTIATED ---
        this.priceManager = new PriceManager(this);

        this.tpaService = new TPAService(this);

        registerCommands();
        registerListeners();

        getLogger().info("NyrethaCore enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (flakesManager != null) {
            flakesManager.saveSync();
        }
        getLogger().info("NyrethaCore disabled!");
    }

    private void registerCommands() {
        if (getCommand("core") != null) getCommand("core").setExecutor(new CoreCommand());
        if (getCommand("home") != null) getCommand("home").setExecutor(new HomeCommand(homeService, teamService));
        if (getCommand("pay") != null) getCommand("pay").setExecutor(new PayCommand(payService, economyManager));
        if (getCommand("flakes") != null) getCommand("flakes").setExecutor(new FlakesCommand(flakesService));
        if (getCommand("rtp") != null) getCommand("rtp").setExecutor(new RTPCommand());
        if (getCommand("sell") != null) getCommand("sell").setExecutor(new SellCommand(sellManager));
        if (getCommand("team") != null) getCommand("team").setExecutor(new TeamCommand(teamService));
        if (getCommand("tools") != null) getCommand("tools").setExecutor(new ToolsCommand());
        if (getCommand("tpa") != null) getCommand("tpa").setExecutor(new TPACommand(tpaService));
        if (getCommand("tpaccept") != null) getCommand("tpaccept").setExecutor(new TPAcceptCommand(tpaService));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new HomeListener(homeService, homeTeleportService, teamService), this);
        getServer().getPluginManager().registerEvents(new PayListener(payService), this);
        getServer().getPluginManager().registerEvents(new RTPListener(), this);
        getServer().getPluginManager().registerEvents(new SellListener(sellManager), this);
        getServer().getPluginManager().registerEvents(new TeamListener(teamService), this);
        getServer().getPluginManager().registerEvents(new ToolListener(), this);
        getServer().getPluginManager().registerEvents(new TPAListener(tpaService), this);
    }

    private void saveResourceFiles() {
        String[] resources = {
            "core/flakes/config.yml", "core/flakes/lang.yml",
            "core/home/config.yml", "core/home/lang.yml",
            "core/pay/config.yml", "core/pay/lang.yml",
            "core/rtp/config.yml", "core/rtp/lang.yml",
            "core/sell/config.yml", "core/sell/lang.yml", "core/sell/prices.yml", "core/sell/sellaxe.yml",
            "core/shop/config.yml", "core/shop/lang.yml",
            "core/team/config.yml", "core/team/lang.yml",
            "core/tools/gui.yml", "core/tools/lang.yml",
            "core/tpa/config.yml", "core/tpa/lang.yml"
        };
        for (String res : resources) {
            saveResource(res, false);
        }
    }

    public static Core getInstance() { return instance; }
    public TeamService getTeamService() { return teamService; }
    public HomeService getHomeService() { return homeService; }
    public TPAService getTpaService() { return tpaService; }
    public PriceManager getPriceManager() { return priceManager; }
    public EconomyManager getEconomyManager() { return economyManager; }
    public Economy getEconomy() { return economyManager != null ? economyManager.getEconomy() : null; }
    public FlakesManager getFlakesManager() { return flakesManager; }
    public LangManager getLangManager() { return langManager; }
}
