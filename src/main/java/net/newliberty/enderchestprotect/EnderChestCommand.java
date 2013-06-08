package net.newliberty.enderchestprotect;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EnderChestCommand implements CommandExecutor {
    private Map<String, Long> clearChests = new HashMap<String, Long>();

    private EnderChestProtect plugin;

    public EnderChestCommand(EnderChestProtect plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.BLUE + "Invalid arguments! " + ChatColor.RED + "/enderchest [list/clear]");
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                listChests(sender);
            } else if (args[0].equalsIgnoreCase("clear")) {
                if (plugin.getChests(sender.getName()).isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "You don't have any protected Ender Chests!");
                    return true;
                }

                sender.sendMessage(ChatColor.BLUE + "This will remove and clear any Protected EnderChests you have! This process is not reversable! If you want to do this, type " + ChatColor.GOLD + "/enderchest confirm");
                sender.sendMessage(ChatColor.BLUE + "This option will only be available for the next 30 seconds");
                clearChests.put(sender.getName(), Long.valueOf(System.currentTimeMillis()));
            } else if (args[0].equalsIgnoreCase("confirm")) {
                if (!clearChests.containsKey(sender.getName())) {
                    sender.sendMessage(ChatColor.RED + "You have nothing to confirm!");
                    return true;
                }

                if (System.currentTimeMillis() - ((Long) clearChests.get(sender.getName())).longValue() <= 30000L) {
                    for (EnderChest ec : plugin.getChests(sender.getName())) {
                        plugin.destroyChest(ec.getLocation());
                    }
                    sender.sendMessage(ChatColor.BLUE + "Your Protected EnderChests have been successfully cleared");
                    clearChests.remove(sender.getName());
                } else {
                    sender.sendMessage(ChatColor.RED + "Your prompt has timed out. Type /enderchest clear to try again!");
                    clearChests.remove(sender.getName());
                }
            } else {
                sender.sendMessage(ChatColor.BLUE + "Invalid arguments! " + ChatColor.RED + "/enderchest [list/clear]");
            }
        } else if (args.length == 2) {
            if (!sender.hasPermission("nlenderchest.admin")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission!");
                return true;
            }

            listChests(sender, args[1]);
        }
        return true;
    }

    private void listChests(CommandSender sender) {
        listChests(sender, null);
    }

    private void listChests(CommandSender sender, String player) {
        boolean self = player != null;
        if (player == null) {
            player = sender.getName();
        }

        if (plugin.getChests(player).isEmpty()) {
            if (self) {
                sender.sendMessage(ChatColor.RED + "You don't have any protected Ender Chests!");
            } else {
                sender.sendMessage(ChatColor.RED + player + " doesn't have any protected Ender Chests! (Name is case sensitive.)");
            }
            return;
        }

        sender.sendMessage(ChatColor.BLUE + "Here are the locations of " + (self ? "your" : "the") + " protected Ender Chests:");
        sender.sendMessage(ChatColor.BLUE + "Chests in " + ChatColor.RED + "red " + ChatColor.BLUE + "are in the nether.");

        int i = 0;
        for (EnderChest ec : plugin.getChests(player)) {
            Location loc = ec.getLocation();
            i++;
            if (ec.getLocation().getWorld().getName().equalsIgnoreCase("world_nether")) {
                sender.sendMessage(ChatColor.RED.toString() + i + ". x = " + loc.getX() + ", y = " + loc.getY() + ", z = " + loc.getZ());
            } else {
                sender.sendMessage(ChatColor.BLUE.toString() + i + ". x = " + loc.getX() + ", y = " + loc.getY() + ", z = " + loc.getZ());
            }
        }
    }
}
