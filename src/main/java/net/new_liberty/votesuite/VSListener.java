package net.new_liberty.votesuite;

import com.vexsoftware.votifier.model.VotifierEvent;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * The VoteSuite listener.
 */
public class VSListener implements Listener {
    private final VoteSuite plugin;

    public VSListener(VoteSuite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVotifier(VotifierEvent event) {
        Vote vote = new Vote(plugin, event.getVote());
        if (!vote.shouldRecord()) {
            plugin.getLogger().log(Level.INFO, "Received a stray vote.");
            return;
        }

        vote.log();

        // Run our commands
        vote.runCommands(plugin.getConfig().getStringList("commands"));


        // Clear and run commands if no missing services
        Voter voter = vote.getVoter();
        if (voter.getMissingServices().isEmpty()) {
            voter.clearRecentVotes();

            // Run our commands
            vote.runCommands(plugin.getConfig().getStringList("all-commands"));
        }
    }
}
