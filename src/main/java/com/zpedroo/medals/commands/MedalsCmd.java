package com.zpedroo.medals.commands;

import com.zpedroo.medals.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MedalsCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) { return true; }
        Player player = (Player) sender;
        Main.get().getMedalsMenu().openMedalsInventory(player);
        return false;
    }
}