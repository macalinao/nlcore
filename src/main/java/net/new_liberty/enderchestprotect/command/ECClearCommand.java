package net.new_liberty.enderchestprotect.command;

import net.new_liberty.enderchestprotect.ClearChestTimer;
import net.new_liberty.enderchestprotect.EnderChestProtect;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Clears Ender Chests.
 */
public class ECClearCommand implements CommandExecutor {
    private final EnderChestProtect plugin;

    public ECClearCommand(EnderChestProtect plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String player = sender.getName();
        if (args.length > 0) {
            player = args[0];
        }

        if (!sender.getName().equals(player)) {
            if (!sender.hasPermission("ecp.admin")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
        }

        if (plugin.getECManager().getChests(player).isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You don't have any protected Ender Chests!");
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "This will remove and clear any protected Ender Chests you have! This process is not reversible! If you want to do this, type " + ChatColor.AQUA + "/ecconfirm");
        sender.sendMessage(ChatColor.YELLOW + "This option will only be available for the next 30 seconds.");
        plugin.getClearChests().put(sender.getName(), new ClearChestTimer(plugin, player));

        return true;
    }
}
