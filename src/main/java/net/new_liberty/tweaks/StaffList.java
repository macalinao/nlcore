package net.new_liberty.tweaks;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import net.new_liberty.core.module.Module;
import net.new_liberty.core.player.NLPlayer;
import net.new_liberty.core.player.StaffRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles /list and notifications of helpers logging in
 */
public class StaffList extends Module implements CommandExecutor {

    @Override
    public void onEnable() {
        plugin.getCommand("list").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        Player[] players = Bukkit.getOnlinePlayers();

        List<String> helpers = new ArrayList<String>();
        List<String> mods = new ArrayList<String>();
        List<String> admins = new ArrayList<String>();

        for (Player p : players) {
            StaffRank r = (new NLPlayer(p)).getStaffRank();
            switch (r) {
                case ADMIN:
                    if (canSee(sender, p)) {
                        admins.add(p.getName());
                    }
                    break;
                case MOD:
                    if (canSee(sender, p)) {
                        mods.add(p.getName());
                    }
                    break;
                case HELPER:
                    if (canSee(sender, p)) {
                        helpers.add(p.getName());
                    }
                    break;
            }
        }
        Joiner j = Joiner.on(' ');

        sender.sendMessage(ChatColor.GOLD + "---------------[" + ChatColor.AQUA + "New Liberty Staff" + ChatColor.GOLD + "]----------------");
        sender.sendMessage(ChatColor.DARK_AQUA + "There are (" + ChatColor.GOLD + players.length + ChatColor.DARK_AQUA + "/" + ChatColor.GOLD + Bukkit.getServer().getMaxPlayers() + ChatColor.DARK_AQUA + ") players online.");
        sender.sendMessage(ChatColor.DARK_AQUA + "Online Admins: " + ChatColor.RED + j.join(admins));
        sender.sendMessage(ChatColor.DARK_AQUA + "Online Mods: " + ChatColor.DARK_PURPLE + j.join(mods));
        sender.sendMessage(ChatColor.DARK_AQUA + "Online Helpers: " + ChatColor.YELLOW + j.join(helpers));
        sender.sendMessage(ChatColor.GOLD + "-----------------------------------------------");
        return true;
    }

    private boolean canSee(CommandSender sender, Player p) {
        if (sender instanceof Player && !((Player) sender).canSee(p)) {
            return false;
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        if (e.getPlayer().hasPermission("stafflist.helper")) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "[Helper] " + ChatColor.GRAY + e.getPlayer().getName() + " joined New Liberty");
        }
    }

}
