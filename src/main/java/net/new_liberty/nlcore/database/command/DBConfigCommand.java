package net.new_liberty.nlcore.database.command;

import com.google.common.base.Joiner;
import net.new_liberty.nlcore.database.DatabaseModule;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * DBConfig commmand.
 */
public class DBConfigCommand implements CommandExecutor {

    private final DatabaseModule dm;

    public DBConfigCommand(DatabaseModule plugin) {
        this.dm = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ranks.staff.admin")) {
            sender.sendMessage(ChatColor.RED + "You aren't allowed to use this command.");
            return true;
        }

        if (args.length == 0) {
            return false;
        }

        // Get the field
        String field = args[0].toLowerCase();
        if (!DatabaseModule.FIELDS.contains(field)) {
            sender.sendMessage(ChatColor.RED + "That field does not exist. Available fields: " + Joiner.on(", ").join(DatabaseModule.FIELDS));
            return true;
        }

        if (args.length == 1) { // Get

            String value = dm.getPlugin().getConfig().getString("db." + field);
            sender.sendMessage(ChatColor.GREEN + field + ": " + ChatColor.YELLOW + value);

        } else { // Set
            String value = args[1];
            dm.getPlugin().getConfig().set("db." + field, value);
            dm.getPlugin().saveConfig();
            sender.sendMessage(ChatColor.GREEN + field + " has been set to " + ChatColor.YELLOW + value);
        }

        return true;
    }

}
