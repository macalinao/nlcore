package net.new_liberty.votesuite.command;

import net.new_liberty.votesuite.VoteSuite;
import net.new_liberty.votesuite.Voter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Sets the player's vote home.
 */
public class VSetHomeCommand implements CommandExecutor {
    private final VoteSuite plugin;

    public VSetHomeCommand(VoteSuite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This is only usable ingame.");
            return true;
        }

        Player p = (Player) sender;
        Location home = p.getLocation();
        Voter v = plugin.getVoter(p.getName());

        v.setHome(home);
        p.sendMessage(ChatColor.YELLOW + "Home set.");
        return true;
    }
}
