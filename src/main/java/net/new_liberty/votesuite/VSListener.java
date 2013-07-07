package net.new_liberty.votesuite;

import com.simplyian.easydb.EasyDB;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
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
        Vote vote = event.getVote();

        String name = vote.getUsername();
        Player player = Bukkit.getPlayerExact(name);
        if (player != null) { // Prefer the online name
            name = player.getName();
        }

        // Timestamp has a different format for each voting website, so we are
        // using our own which depends on the time it was received
        EasyDB.getDb().update("INSERT INTO votes (name, service, address) VALUES (?, ?, ?) ",
                name, vote.getServiceName(), vote.getAddress());
    }
}
