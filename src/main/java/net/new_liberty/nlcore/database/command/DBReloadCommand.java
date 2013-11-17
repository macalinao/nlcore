package net.new_liberty.nlcore.database.command;

import net.new_liberty.nlcore.database.EasyDBPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author simplyianm
 */
public class DBReloadCommand implements CommandExecutor {
    private final EasyDBPlugin plugin;

    public DBReloadCommand(EasyDBPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("easydb.reload")) {
            sender.sendMessage(ChatColor.RED + "You aren't allowed to use this command.");
            return true;
        }

        plugin.reloadDb();
        sender.sendMessage(ChatColor.GREEN + "Database reloaded.");
        (new BukkitRunnable() {
            @Override
            public void run() {
                String message;
                if (plugin.getDb().isValid()) {
                    message = ChatColor.GREEN + "Connection valid.";
                } else {
                    message = ChatColor.RED + "Connection invalid! Check your database credentials.";
                }
                sender.sendMessage(message);
            }
        }).runTaskAsynchronously(plugin);
        return true;
    }
}
