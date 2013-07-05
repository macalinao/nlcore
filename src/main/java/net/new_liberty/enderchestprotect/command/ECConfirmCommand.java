package net.new_liberty.enderchestprotect.command;

import java.util.Map;
import net.new_liberty.enderchestprotect.ClearChestTimer;
import net.new_liberty.enderchestprotect.EnderChestProtect;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Ender Chest confirm command, used in conjunction with /ecclear.
 */
public class ECConfirmCommand implements CommandExecutor {
    private final EnderChestProtect plugin;

    public ECConfirmCommand(EnderChestProtect plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Map<String, ClearChestTimer> clearChests = plugin.getClearChests();
        final ClearChestTimer timer = clearChests.get(sender.getName());

        if (timer == null) {
            sender.sendMessage(ChatColor.RED + "You have nothing to confirm!");
            return true;
        }

        if (timer.isExpired()) {
            sender.sendMessage(ChatColor.RED + "Your prompt has timed out. Type /enderchest clear to try again!");
            clearChests.remove(sender.getName());
            return true;
        }

        timer.clearChests();
        sender.sendMessage(ChatColor.YELLOW + "Your protected Ender Chests have been successfully cleared.");
        clearChests.remove(sender.getName());
        return true;
    }
}
