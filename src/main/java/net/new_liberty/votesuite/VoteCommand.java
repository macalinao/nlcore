package net.new_liberty.votesuite;

import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The "/vote" command.
 */
public class VoteCommand implements CommandExecutor {
    private final VoteSuite plugin;

    public VoteCommand(VoteSuite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You're not a player, so this command is irrelevant.");
            return true;
        }
        Player player = (Player) sender;

        Set<VoteService> services = plugin.getMissingServices(player.getName());
        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_RED + "New Liberty" + ChatColor.GRAY + "]" + ChatColor.YELLOW + " Voting Help");
        if (services.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "You have already voted for everything today! Thanks for voting, and make sure to vote tomorrow!");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Click on all of the links below or go to " + ChatColor.WHITE + "http://vote.nl-mc.com/" + ChatColor.YELLOW + " to receive your voting rewards.");
            for (VoteService service : services) {
                player.sendMessage(ChatColor.YELLOW + service.getName() + ": " + ChatColor.WHITE + service.getUrl());
            }
        }
        return true;
    }
}
