package com.nyretha.flakes.command;

import com.nyretha.flakes.gui.FlakeViewGUI;
import com.nyretha.flakes.service.FlakesService;
import com.nyretha.shop.utils.HexColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlakesCommand implements CommandExecutor {

    private final FlakesService flakesService;

    public FlakesCommand(FlakesService flakesService) {
        this.flakesService = flakesService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        if (args.length == 0) {
            FlakeViewGUI.openGUI(player, flakesService, 1);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "bal" -> {
                Player target = args.length > 1 ? Bukkit.getPlayerExact(args[1]) : player;
                if (target == null) {
                    player.sendMessage(HexColor.format("&cPlayer not found"));
                    return true;
                }
                int bal = flakesService.getFlakes(target.getUniqueId());
                player.sendMessage(HexColor.format("&#A303F9ꜰʟᴀᴋᴇꜱ &7» &fBalance: &#A303F9" + bal + " flakes"));
            }
            case "pay" -> {
                if (args.length < 3) {
                    player.sendMessage(HexColor.format("&cUsage: /flakes pay <player> <amount>"));
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    player.sendMessage(HexColor.format("&cPlayer not found"));
                    return true;
                }
                if (target.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(HexColor.format("&cYou can´t give ur self flakes!"));
                    return true;
                }
                try {
                    int amount = Integer.parseInt(args[2]);
                    if (amount <= 0 || flakesService.getFlakes(player.getUniqueId()) < amount) {
                        player.sendMessage(HexColor.format("&cYou have not enough flakes!"));
                        return true;
                    }
                    flakesService.removeFlakes(player.getUniqueId(), amount);
                    flakesService.addFlakes(target.getUniqueId(), amount);

                    player.sendMessage(HexColor.format("&fYou sent &#A303F9" + amount + " flakes &fto &#00FF00" + target.getName()));
                    target.sendMessage(HexColor.format("&fYou received &#A303F9" + amount + " flakes &ffrom &#00FF00" + player.getName()));
                } catch (NumberFormatException e) {
                    player.sendMessage(HexColor.format("&cInvaild number!"));
                }
            }
            case "give" -> {
                if (!player.hasPermission("flakes.admin") || args.length < 3) return true;
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target != null) {
                    int amount = Integer.parseInt(args[2]);
                    flakesService.addFlakes(target.getUniqueId(), amount);
                    player.sendMessage(HexColor.format("&fYou gave &#A303F9" + amount + " flakes &fto &#00FF00" + target.getName() + "!"));
                }
            }
            case "take" -> {
                if (!player.hasPermission("flakes.admin") || args.length < 3) return true;
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target != null) {
                    int amount = Integer.parseInt(args[2]);
                    flakesService.removeFlakes(target.getUniqueId(), amount);
                    player.sendMessage(HexColor.format("&fYou took &#A303F9" + amount + " flakes &ffrom &#00FF00" + target.getName() + "!"));
                }
            }
            case "set" -> {
                if (!player.hasPermission("flakes.admin") || args.length < 3) return true;
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target != null) {
                    int amount = Integer.parseInt(args[2]);
                    flakesService.setFlakes(target.getUniqueId(), amount);
                    player.sendMessage(HexColor.format("&fYou set the flakes of &#00FF00" + target.getName() + " &fto &#A303F9" + amount + "&f!"));
                }
            }
            default -> player.sendMessage(HexColor.format("&cUnknown sub-command."));
        }

        return true;
    }
}
