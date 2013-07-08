package net.new_liberty.votesuite;

import com.simplyian.easydb.EasyDB;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        runCommands(plugin.getConfig().getStringList("commands"), name, serviceId, serviceName, address);

        // Our recent votes cache
        EasyDB.getDb().update("INSERT IGNORE INTO votes_recent (name, service) VALUES (?, ?) ",
                name, serviceId, address);

        // Generate a Set of missing services
        List<String> voteSvcIds = EasyDB.getDb().query("SELECT service FROM votes_recent WHERE name = ?", new ColumnListHandler<String>(), name);
        Set<VoteService> missingServices = plugin.getServices();
        for (String voteSvcId : voteSvcIds) {
            VoteService rm = null;
            for (VoteService svc : missingServices) {
                if (svc.getId().equals(voteSvcId)) {
                    rm = svc;
                    break;
                }
            }
            missingServices.remove(rm);
        }

        // Clear and run commands if empty
        if (missingServices.isEmpty()) {
            EasyDB.getDb().update("DELETE FROM votes_recent WHERE name = ?", name);

            // Run our commands
            runCommands(plugin.getConfig().getStringList("all-commands"), name, serviceId, serviceName, address);
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
