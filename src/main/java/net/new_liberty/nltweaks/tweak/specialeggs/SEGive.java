/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.nltweaks.tweak.specialeggs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author simplyianm
 */
public class SEGive implements CommandExecutor {

    private final SpecialEggs se;

    public SEGive(SpecialEggs se) {
        this.se = se;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("specialeggs.segive")) {
            sender.sendMessage(ChatColor.RED + "You have been automatically reported for hacking.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Specify an egg, please. (Replace spaces with _'s)");
            return true;
        }

        String egg = args[0];
        SpecialEgg s = se.getEgg(egg.replaceAll("_", " "));
        if (s == null) {
            sender.sendMessage(ChatColor.RED + "Unknown egg.");
            return true;
        }

        int amt = 1;
        if (args.length >= 2) {
            String amtStr = args[1];
            try {
                amt = Integer.valueOf(amtStr);
                if (amt > 64) {
                    sender.sendMessage(ChatColor.RED + "Calm down with the eggs. 64 eggs max!");
                    amt = 64;
                }
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "That isn't a number! Spawning 1 egg only.");
            }
        }

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        if (args.length >= 3) {
            String pName = args[2];
            player = Bukkit.getPlayer(pName);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Unknown player.");
                return true;
            }
        }

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "You didn't specify a player to give the eggs to.");
            return true;
        }

        player.getInventory().addItem(s.create(amt));
        sender.sendMessage(ChatColor.YELLOW + "You have given " + amt + " " + s.getName() + " to " + player.getName() + ".");
        return true;
    }

}
