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
 * Teleports you home.
 */
public class VHomeCommand implements CommandExecutor {
    private final VoteSuite plugin;

    public VHomeCommand(VoteSuite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You can't have a home idiot");
            return true;
        }

        Player p = (Player) sender;
        Voter v = plugin.getVoter(p.getName());

        int votes = v.countDayVotes();
        int required = plugin.getConfig().getInt("votes-home", 3);
        if (votes < required) {
            p.sendMessage(ChatColor.RED + "You need to vote "
                    + ChatColor.YELLOW + (required - votes) + ChatColor.RED
                    + " more times to use this command. Type " + ChatColor.YELLOW
                    + "/vote " + ChatColor.RED + " for more info.");
            return true;
        }

        Location home = v.getHome();
        if (home == null) {
            p.sendMessage(ChatColor.RED + "Error: you currently don't have a home set.");
        } else {
            p.teleport(home);
        }
        return true;
    }
}
