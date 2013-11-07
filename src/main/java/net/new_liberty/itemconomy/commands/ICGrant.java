/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.itemconomy.commands;

import net.new_liberty.itemconomy.BankAccount;
import net.new_liberty.itemconomy.CurrencyInventory;
import net.new_liberty.nlcore.player.NLPlayer;
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
public class ICGrant implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("itemconomy.admin")) {
            sender.sendMessage(ChatColor.RED + "Not allowed...");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /icgrant <player> <amt>");
            return true;
        }

        BankAccount b = new BankAccount(args[0]);

        int amt = 0;
        try {
            amt = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid integer!");
            return true;
        }

        b.add(amt);

        sender.sendMessage(ChatColor.YELLOW + args[0] + " has been granted " + b.balance() + " emeralds.");
        return true;
    }

}
