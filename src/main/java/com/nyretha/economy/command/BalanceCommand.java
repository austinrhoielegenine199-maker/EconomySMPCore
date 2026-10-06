package com.nyretha.economy.command;

import com.nyretha.economy.service.EconomyConfigService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BalanceCommand implements CommandExecutor {

    private final EconomyConfigService economyConfigService;

    public BalanceCommand(EconomyConfigService economyConfigService) {
        this.economyConfigService = economyConfigService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Your balance command logic here
        return true;
    }
}
