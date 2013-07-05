package net.new_liberty.enderchestprotect.command;

import java.util.List;
import net.new_liberty.enderchestprotect.EnderChest;
import net.new_liberty.enderchestprotect.EnderChestProtect;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Ender Chest list command, which lists the Ender Chests of a player.
 */
public class ECListCommand implements CommandExecutor {
    private final EnderChestProtect plugin;

    public ECListCommand(EnderChestProtect plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String player;
        if (args.length == 0) {
            player = sender.getName();
        } else {
            player = args[0];
            if (player.equalsIgnoreCase(sender.getName())) {
                player = sender.getName();
            }
        }
        boolean self = !player.equals(sender.getName());

        if (!self && !sender.hasPermission("ecp.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        List<EnderChest> chests = plugin.getECManager().getChests(player);

        if (chests.isEmpty()) {
            if (self) {
                sender.sendMessage(ChatColor.RED + "You don't have any protected Ender Chests!");
            } else {
                sender.sendMessage(ChatColor.RED + player + " doesn't have any protected Ender Chests! (Name is case sensitive.)");
            }
            return true;
        }

        sender.sendMessage(ChatColor.BLUE + "Here are the locations of " + (self ? "your" : "the") + " protected Ender Chests:");
        sender.sendMessage(ChatColor.BLUE + "Chests in " + ChatColor.RED + "red " + ChatColor.BLUE + "are in the nether.");

        int i = 0;
        for (EnderChest ec : chests) {
            Location loc = ec.getLocation();
            i++;

            String locString = "x = " + loc.getX() + ", y = " + loc.getY() + ", z = " + loc.getZ();
            String expireString = ChatColor.YELLOW + "(expires " + ChatColor.AQUA + ec.getExpiryTimeString() + ")";

            String msg = Integer.toString(self ? i : ec.getId()) + ". " + locString + " " + expireString;

            if (ec.getLocation().getWorld().getName().equalsIgnoreCase("world_nether")) {
                msg = ChatColor.RED + msg;
            } else {
                msg = ChatColor.BLUE + msg;
            }

            sender.sendMessage(msg);
        }
        return true;
    }
}
