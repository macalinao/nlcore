package net.new_liberty.votesuite;

import com.simplyian.easydb.EasyDB;
import com.vexsoftware.votifier.model.Vote;
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
        Vote vote = event.getVote();

        String name = vote.getUsername();
        Player player = Bukkit.getPlayerExact(name);
        if (player != null) { // Prefer the online name
            name = player.getName();
        }
        Voter voter = plugin.getVoter(name);

        VoteService service = plugin.getService(vote.getServiceName());
        if (service == null) {
            return; // We're not tracking this service; ignore it.
        }

        String address = vote.getAddress();

        voter.addVote(service, vote.getAddress());

        // Run our commands
        runCommands(plugin.getConfig().getStringList("commands"), name, service.getId(), service.getName(), address);


        // Clear and run commands if no missing services
        if (voter.getMissingServices().isEmpty()) {
            voter.clearRecentVotes();

            // Run our commands
            runCommands(plugin.getConfig().getStringList("all-commands"), name, service.getName(), service.getName(), address);
        }
    }

    private void runCommands(List<String> cmds, String name, String service, String serviceName, String address) {
        for (String cmd : cmds) {
            cmd = replaceVars(cmd, name, service, serviceName, address);

            try {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            } catch (CommandException ex) {
                plugin.getLogger().log(Level.SEVERE, "An error occured when running the command '" + cmd + "'.", ex);
            }
        }
    }

    /**
     * Returns a properly formatted command string.
     *
     * @param cmd
     * @param name
     * @param service
     * @param serviceName
     * @param address
     * @return
     */
    private String replaceVars(String cmd, String name, String service, String serviceName, String address) {
        cmd = cmd.replace("%name%", name);
        cmd = cmd.replace("%service%", service);
        cmd = cmd.replace("%service_name%", serviceName);
        cmd = cmd.replace("%address%", address);

        return cmd;
    }
}
