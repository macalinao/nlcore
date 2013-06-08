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
            Location loc;
            if (args[0].equalsIgnoreCase("list")) {
                if ((plugin.chestLocations.get(sender.getName()) != null) && (!((ArrayList) plugin.chestLocations.get(sender.getName())).isEmpty())) {
                    int i = 0;
                    sender.sendMessage(ChatColor.BLUE + "Here are the locations of your Protected EnderChests:");
                    sender.sendMessage(ChatColor.BLUE + "Chests in " + ChatColor.RED + "red " + ChatColor.BLUE + "are in the Nether");
                    for (Iterator localIterator = ((ArrayList) plugin.chestLocations.get(sender.getName())).iterator(); localIterator.hasNext();) {
                        loc = (Location) localIterator.next();
                        i++;
                        if (loc.getWorld().getName().equalsIgnoreCase("world_nether")) {
                            sender.sendMessage(ChatColor.RED.toString() + i + ". x = " + loc.getX() + ", y = " + loc.getY() + ", z = " + loc.getZ());
                        } else {
                            sender.sendMessage(ChatColor.BLUE.toString() + i + ". x = " + loc.getX() + ", y = " + loc.getY() + ", z = " + loc.getZ());
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have any Protected EnderChests!");
                }
            } else if (args[0].equalsIgnoreCase("clear")) {
                if ((plugin.chestLocations.get(sender.getName()) != null) && (!((ArrayList) plugin.chestLocations.get(sender.getName())).isEmpty())) {
                    sender.sendMessage(ChatColor.BLUE + "This will remove and clear any Protected EnderChests you have! This process is not reversable! If you want to do this, type " + ChatColor.GOLD + "/enderchest confirm");
                    sender.sendMessage(ChatColor.BLUE + "This option will only be available for the next 30 seconds");
                    clearChests.put(sender.getName(), Long.valueOf(System.currentTimeMillis()));
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have any Protected EnderChests!");
                }
            } else if (args[0].equalsIgnoreCase("confirm")) {
                if (clearChests.containsKey(sender.getName())) {
                    if (System.currentTimeMillis() - ((Long) clearChests.get(sender.getName())).longValue() <= 30000L) {
                        for (Location l : plugin.chestLocations.get(sender.getName())) {
                            File f = new File(plugin.getDataFolder(), l.toString() + ".yml");
                            l.getBlock().setType(Material.AIR);
                            plugin.destroyChest(l);
                            f.delete();
                        }
                        sender.sendMessage(ChatColor.BLUE + "Your Protected EnderChests have been successfully cleared");
                        clearChests.remove(sender.getName());
                    } else {
                        sender.sendMessage(ChatColor.RED + "Your prompt has timed out. Type /enderchest clear to try again!");
                        clearChests.remove(sender.getName());
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You have nothing to confirm!");
                }
            } else {
                sender.sendMessage(ChatColor.BLUE + "Invalid arguments! " + ChatColor.RED + "/enderchest [list/clear]");
            }
        } else if (args.length == 2) {
            if ((args[0].equalsIgnoreCase("list")) && (sender.hasPermission("nlenderchest.admin"))) {
                String playerName = args[1];
                if ((plugin.chestLocations.get(playerName) != null) && (!((ArrayList) plugin.chestLocations.get(playerName)).isEmpty())) {
                    int i = 0;
                    sender.sendMessage(ChatColor.BLUE + "Here are the locations of " + ChatColor.GOLD + playerName + "'s " + ChatColor.BLUE + "protected Ender Chests:");
                    sender.sendMessage(ChatColor.BLUE + "Chests in " + ChatColor.RED + "red " + ChatColor.BLUE + "are in the Nether");
                    for (Location loc : plugin.chestLocations.get(playerName)) {
                        i++;
                        if (loc.getWorld().getName().equalsIgnoreCase("world_nether")) {
                            sender.sendMessage(ChatColor.RED.toString() + i + ". x = " + loc.getX() + ", y = " + loc.getY() + ", z = " + loc.getZ());
                        } else {
                            sender.sendMessage(ChatColor.BLUE.toString() + i + ". x = " + loc.getX() + ", y = " + loc.getY() + ", z = " + loc.getZ());
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "That player has no Protected EnderChests! Warning, case sensitive!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
        }
        return true;
    }
}
