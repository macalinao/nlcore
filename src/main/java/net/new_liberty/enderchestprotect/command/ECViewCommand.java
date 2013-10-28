package net.new_liberty.enderchestprotect.command;

import net.new_liberty.enderchestprotect.EnderChest;
import net.new_liberty.enderchestprotect.EnderChestProtect;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allows viewing of a given Ender Chest.
 */
public class ECViewCommand implements CommandExecutor {
    private final EnderChestProtect plugin;

    public ECViewCommand(EnderChestProtect plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used ingame.");
            return true;
        }

        if (!sender.hasPermission("ecp.admin")) {
            sender.sendMessage(ChatColor.RED + "You aren't allowed to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /ecview <chest id>");
            return true;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "'id' must be a number.");
            return true;
        }

        EnderChest ec = plugin.getECManager().getChest(id);
        if (ec == null) {
            sender.sendMessage(ChatColor.RED + "A chest with the id " + ChatColor.YELLOW + id + ChatColor.RED + " does not exist.");
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "EC #" + ec.getId() + " owned by " + ChatColor.GREEN + ec.getOwner());
        sender.sendMessage(ChatColor.YELLOW + "Expires " + ChatColor.AQUA + ec.getExpiryTimeString());
        ((Player) sender).openInventory(ec.getInventory());
        return true;
    }
}
