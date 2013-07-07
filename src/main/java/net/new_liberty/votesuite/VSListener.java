package net.new_liberty.votesuite;

import com.simplyian.easydb.EasyDB;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
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
        Vote vote = event.getVote();

        String name = vote.getUsername();
        Player player = Bukkit.getPlayerExact(name);
        if (player != null) { // Prefer the online name
            name = player.getName();
        }

        String serviceId = vote.getServiceName(); // TODO add a Service object
        VoteService service = plugin.getService(serviceId);
        String serviceName;
        if (service == null) {
            serviceName = serviceId;
        } else {
            serviceName = service.getName();
        }

        String address = vote.getAddress();

        // Timestamp has a different format for each voting website, so we are
        // using our own which depends on the time it was received
        EasyDB.getDb().update("INSERT INTO votes (name, service, address) VALUES (?, ?, ?) ",
                name, serviceId, address);

        // Run our commands
        for (String cmd : plugin.getConfig().getStringList("commands")) {
            cmd = cmd.replace("%name%", name);
            cmd = cmd.replace("%service%", serviceId);
            cmd = cmd.replace("%service_name%", serviceName);
            cmd = cmd.replace("%address%", address);

            try {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            } catch (CommandException ex) {
                plugin.getLogger().log(Level.SEVERE, "An error occured when running the command '" + cmd + "'.", ex);
            }
        }
    }
}
