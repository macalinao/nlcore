package net.new_liberty.enderchestprotect;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EnderChestCommand implements CommandExecutor {
    private Map<String, ClearChestTimer> clearChests = new HashMap<String, ClearChestTimer>();

    private EnderChestProtect plugin;

    public EnderChestCommand(EnderChestProtect plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.BLUE + "Invalid arguments! " + ChatColor.RED + "/enderchest [list/clear]");
            return true;
        }

        String action = args[0];

        if (action.equalsIgnoreCase("list")) {
            if (args.length == 2) {
                listChests(sender, args[1]);
            } else {
                listChests(sender);
            }
        } else if (args[0].equalsIgnoreCase("clear")) {
            if (args.length == 2) {
                clearChests(sender, args[1]);
            } else {
                clearChests(sender);
            }

        } else if (args[0].equalsIgnoreCase("confirm")) {
            confirmChests(sender);

        } else {
            sender.sendMessage(ChatColor.BLUE + "Invalid arguments! " + ChatColor.RED + "/enderchest [list/clear]");
        }
        return true;
    }

    private void listChests(CommandSender sender) {
        listChests(sender, null);
    }

    private void listChests(CommandSender sender, String player) {
        if (!sender.getName().equals(player)) {
            if (!sender.hasPermission("nlenderchest.admin")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission!");
                return;
            }
        }

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

    private void clearChests(CommandSender sender) {
        clearChests(sender, sender.getName());
    }

    private void clearChests(CommandSender sender, String player) {
        if (!sender.getName().equals(player)) {
            if (!sender.hasPermission("nlenderchest.admin")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission!");
                return;
            }
        }

        if (plugin.getChests(player).isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You don't have any protected Ender Chests!");
            return;
        }

        sender.sendMessage(ChatColor.BLUE + "This will remove and clear any protected Ender Chests you have! This process is not reversible! If you want to do this, type " + ChatColor.GOLD + "/enderchest confirm");
        sender.sendMessage(ChatColor.BLUE + "This option will only be available for the next 30 seconds.");
        clearChests.put(sender.getName(), new ClearChestTimer(player));
    }

    private void confirmChests(CommandSender sender) {
        ClearChestTimer timer = clearChests.get(sender.getName());

        if (timer == null) {
            sender.sendMessage(ChatColor.RED + "You have nothing to confirm!");
            return;
        }

        if (timer.isExpired()) {
            sender.sendMessage(ChatColor.RED + "Your prompt has timed out. Type /enderchest clear to try again!");
            clearChests.remove(sender.getName());
            return;
        }

        timer.clearChests();
        sender.sendMessage(ChatColor.BLUE + "Your protected Ender Chests have been successfully cleared.");
        clearChests.remove(sender.getName());
    }

    private class ClearChestTimer {
        private final String player;

        private final long expire;

        public ClearChestTimer(String player) {
            this.player = player;
            this.expire = System.currentTimeMillis() + 30000L;
        }

        public String getPlayer() {
            return player;
        }

        public long getExpire() {
            return expire;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= expire;
        }

        public void clearChests() {
            for (EnderChest ec : plugin.getChests(player)) {
                plugin.destroyChest(ec.getLocation());
            }
        }
    }
}
