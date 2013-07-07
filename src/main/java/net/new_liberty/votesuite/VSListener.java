package net.new_liberty.votesuite;

import com.vexsoftware.votifier.model.VotifierEvent;
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
    }
}
