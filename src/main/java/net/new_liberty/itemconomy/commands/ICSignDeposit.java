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
public class ICSignDeposit implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("itemconomy.console")) {
            sender.sendMessage(ChatColor.RED + "Not allowed...");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /icsigndeposit <player> <amt>");
            return true;
        }

        Player p = Bukkit.getPlayerExact(args[0]);
        if (p == null) {
            sender.sendMessage(ChatColor.RED + "No such player '" + args[0] + "'!");
            return true;
        }

        int amt = 0;
        try {
            amt = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid integer!");
            return true;
        }

        CurrencyInventory c = new CurrencyInventory(p);
        int holding = c.balance();
        if (holding < amt) {
            p.sendMessage(ChatColor.RED + "You don't have " + amt + " emeralds in your inventory to deposit!");
            return true;
        }

        BankAccount b = new BankAccount(p);
        c.remove(amt);
        b.add(amt);

        p.sendMessage(ChatColor.YELLOW + "You have successfully transferred " + amt + " emeralds to your bank account.");
        return true;
    }

}
