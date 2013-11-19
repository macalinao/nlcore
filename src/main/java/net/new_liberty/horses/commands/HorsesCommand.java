/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.horses.commands;

import java.lang.String;
import java.util.List;
import java.util.List;
import java.util.UUID;
import net.new_liberty.horses.Horses;
import net.new_liberty.horses.OwnedHorse;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Lists a player's horses.
 */
public class HorsesCommand implements CommandExecutor {

    private final Horses h;

    public HorsesCommand(Horses h) {
        this.h = h;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String owner = null;
        if (sender instanceof Player) {
            owner = sender.getName();
        }

        if (sender.hasPermission("nlhorses.admin") && args.length > 0) {
            owner = args[0];
        }

        if (owner == null) {
            sender.sendMessage(ChatColor.RED + "Please specify a player to view the horses of.");
            return true;
        }

        sender.sendMessage("=== " + ChatColor.GOLD + "[" + ChatColor.GREEN + owner + "'s Horses" + ChatColor.GOLD + "] " + ChatColor.RESET + "===");

        List<OwnedHorse> horses = h.getHorses().getHorses(owner);
        if (horses.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You don't own any horses.");
            return true;
        }

        for (OwnedHorse o : horses) {
            sender.sendMessage(ChatColor.YELLOW + "- " + o.getCallName());
        }

        return true;
    }

}
