/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.itemconomy.commands;

import com.google.common.base.Joiner;
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
public class ICSignBuy implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("itemconomy.console")) {
            sender.sendMessage(ChatColor.RED + "Not allowed...");
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /icsignbuy <player> <price> <name> <cmd>");
            return true;
        }

        Player p = Bukkit.getPlayerExact(args[0]);
        if (p == null) {
            sender.sendMessage(ChatColor.RED + "No such player '" + args[0] + "'!");
            return true;
        }

        int price = 0;
        try {
            price = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid integer price!");
            return true;
        }

        String name = args[2].replaceAll("_", " ");

        CurrencyInventory i = new CurrencyInventory(p);
        int balance = i.balance();
        if (balance < price) {
            p.sendMessage(ChatColor.RED + "You don't have enough currency to buy this! (Requires " + price + "; you only have " + balance + ")");
            return true;
        }

        int of = i.remove(price);
        if (of != 0) {
            p.sendMessage(ChatColor.RED + "You don't have enough emeralds to buy this! Turn your emerald blocks into emeralds and try again. (Need " + of + " more emeralds)");
            return true;
        }

        String[] cmdArray = new String[args.length - 2];
        System.arraycopy(args, 2, cmdArray, 0, cmdArray.length);
        String cmd = Joiner.on(' ').join(cmdArray);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);

        p.sendMessage(ChatColor.YELLOW + "You have bought " + name + " for " + price + " emeralds.");
        return true;
    }

}
