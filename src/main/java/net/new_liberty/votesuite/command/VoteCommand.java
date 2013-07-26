package net.new_liberty.votesuite.command;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.new_liberty.votesuite.VoteService;
import net.new_liberty.votesuite.VoteSuite;
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

        Set<VoteService> services = plugin.getVoter(player.getName()).getMissingServices();
        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_RED + "New Liberty" + ChatColor.GRAY + "]" + ChatColor.YELLOW + " Voting Help");
        if (services.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "You have already voted for everything today! Thanks for voting, and make sure to vote tomorrow!");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Go to " + ChatColor.WHITE + "http://vote.nl-mc.com/" + ChatColor.YELLOW + " to receive your voting rewards.");
            player.sendMessage(ChatColor.GREEN + "Sites you haven't voted on yet:");

            List<String> serviceNames = new ArrayList<String>();
            for (VoteService service : services) {
                serviceNames.add(service.getName());
            }
            player.sendMessage(ChatColor.AQUA + Joiner.on(", ").join(serviceNames));
        }
        return true;
    }
}
