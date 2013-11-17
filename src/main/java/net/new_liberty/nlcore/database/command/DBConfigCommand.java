package net.new_liberty.nlcore.database.command;

import com.google.common.base.Joiner;
import net.new_liberty.nlcore.database.EasyDBPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * DBConfig commmand.
 */
public class DBConfigCommand implements CommandExecutor {
    private final EasyDBPlugin plugin;

    public DBConfigCommand(EasyDBPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("easydb.config")) {
            sender.sendMessage(ChatColor.RED + "You aren't allowed to use this command.");
            return true;
        }

        if (args.length == 0) {
            return false;
        }

        // Get the field
        String field = args[0].toLowerCase();
        if (!EasyDBPlugin.FIELDS.contains(field)) {
            sender.sendMessage(ChatColor.RED + "That field does not exist. Available fields: " + Joiner.on(", ").join(EasyDBPlugin.FIELDS));
            return true;
        }

        if (args.length == 1) { // Get

            String value = plugin.getConfig().getString("db." + field);
            sender.sendMessage(ChatColor.GREEN + field + ": " + ChatColor.YELLOW + value);

        } else { // Set
            String value = args[1];
            plugin.getConfig().set("db." + field, value);
            plugin.saveConfig();
            sender.sendMessage(ChatColor.GREEN + field + " has been set to " + ChatColor.YELLOW + value);
        }

        return true;
    }
}
