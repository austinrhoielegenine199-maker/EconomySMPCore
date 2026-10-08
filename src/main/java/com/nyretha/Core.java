package com.nyretha;

import com.nyretha.command.CoreCommand;
import com.nyretha.flakes.command.FlakesCommand;
import com.nyretha.flakes.listener.FlakeListener;
import com.nyretha.home.command.HomeCommand;
import com.nyretha.home.listener.HomeListener;
import com.nyretha.pay.command.PayCommand;
import com.nyretha.pay.listener.PayListener;
import com.nyretha.rtp.command.RTPCommand;
import com.nyretha.rtp.listener.RTPListener;
import com.nyretha.sell.command.SellCommand;
import com.nyretha.sell.listener.SellListener;
import com.nyretha.sell.manager.PriceManager;
import com.nyretha.team.command.TeamCommand;
import com.nyretha.team.listener.TeamListener;
import com.nyretha.tools.command.ToolsCommand;
import com.nyretha.tools.listener.ToolListener;
import com.nyretha.tpa.command.TPACommand;
import com.nyretha.tpa.command.TPAcceptCommand;
import com.nyretha.tpa.listener.TPAListener;
import com.nyretha.tpa.service.TPAService;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin {

    private static Core instance;
    private TPAService tpaService;
    private PriceManager priceManager;

    @Override
    public void onEnable() {
        instance = this;

        // Save default embedded configuration resources
        saveDefaultConfig();
        saveResourceFiles();

        // Initialize Managers & Services
        this.tpaService = new TPAService(this);
        this.priceManager = new PriceManager(this);

        // Register Commands
        registerCommands();

        // Register Listeners
        registerListeners();

        getLogger().info("EconomySMPCore has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("EconomySMPCore has been disabled!");
    }

    private void registerCommands() {
        if (getCommand("core") != null) getCommand("core").setExecutor(new CoreCommand());
        if (getCommand("home") != null) getCommand("home").setExecutor(new HomeCommand());
        if (getCommand("pay") != null) getCommand("pay").setExecutor(new PayCommand());
        if (getCommand("flakes") != null) getCommand("flakes").setExecutor(new FlakesCommand());
        if (getCommand("rtp") != null) getCommand("rtp").setExecutor(new RTPCommand());
        if (getCommand("sell") != null) getCommand("sell").setExecutor(new SellCommand());
        if (getCommand("team") != null) getCommand("team").setExecutor(new TeamCommand());
        if (getCommand("tools") != null) getCommand("tools").setExecutor(new ToolsCommand());
        if (getCommand("tpa") != null) getCommand("tpa").setExecutor(new TPACommand(tpaService));
        if (getCommand("tpaccept") != null) getCommand("tpaccept").setExecutor(new TPAcceptCommand(tpaService));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new HomeListener(), this);
        getServer().getPluginManager().registerEvents(new PayListener(), this);
        getServer().getPluginManager().registerEvents(new FlakeListener(), this);
        getServer().getPluginManager().registerEvents(new RTPListener(), this);
        getServer().getPluginManager().registerEvents(new SellListener(), this);
        getServer().getPluginManager().registerEvents(new TeamListener(), this);
        getServer().getPluginManager().registerEvents(new ToolListener(), this);
        getServer().getPluginManager().registerEvents(new TPAListener(tpaService), this);
    }

    private void saveResourceFiles() {
        String[] resources = {
            "core/flakes/config.yml",
            "core/flakes/lang.yml",
            "core/home/config.yml",
            "core/home/lang.yml",
            "core/pay/config.yml",
            "core/pay/lang.yml",
            "core/rtp/config.yml",
            "core/rtp/lang.yml",
            "core/sell/config.yml",
            "core/sell/lang.yml",
            "core/sell/prices.yml",
            "core/sell/sellaxe.yml",
            "core/shop/config.yml",
            "core/shop/lang.yml",
            "core/team/config.yml",
            "core/team/lang.yml",
            "core/tools/gui.yml",
            "core/tools/lang.yml",
            "core/tpa/config.yml",
            "core/tpa/lang.yml",
            "core/tpa/gui/tpa-accept.yml",
            "core/tpa/gui/tpa-here-accept.yml",
            "core/tpa/gui/tpa-here-send.yml",
            "core/tpa/gui/tpa-send.yml"
        };

        for (String resource : resources) {
            saveResource(resource, false);
        }
    }

    public static Core getInstance() {
        return instance;
    }

    public TPAService getTpaService() {
        return tpaService;
    }

    public PriceManager getPriceManager() {
        return priceManager;
    }
}
