/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.itemconomy.commands;

import net.new_liberty.itemconomy.BankAccount;
import net.new_liberty.itemconomy.CurrencyInventory;
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
public class ICBalance implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("itemconomy.console")) {
            sender.sendMessage(ChatColor.RED + "Not allowed...");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /icbalance <player>");
            return true;
        }

        BankAccount b = new BankAccount(args[0]);

        sender.sendMessage(ChatColor.YELLOW + args[0] + "'s balance is " + b.balance() + " emeralds.");
        return true;
    }

}
