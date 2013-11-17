package net.new_liberty.votesuite;

import net.new_liberty.nlcore.database.EasyDB;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

/**
 * Represents a vote.
 */
public class Vote {
    private final VoteSuite plugin;

    private final String name;

    private final VoteService service;

    private final String address;

    public Vote(VoteSuite plugin, com.vexsoftware.votifier.model.Vote vote) {
        this.plugin = plugin;
        Player player = Bukkit.getPlayerExact(vote.getUsername());
        if (player != null) { // Prefer the online name
            name = player.getName();
        } else {
            name = vote.getUsername();
        }
        service = plugin.getService(vote.getServiceName());
        address = vote.getAddress();
    }

    public String getName() {
        return name;
    }

    public Voter getVoter() {
        return plugin.getVoter(name);
    }

    public VoteService getService() {
        return service;
    }

    public String getAddress() {
        return address;
    }

    public boolean shouldRecord() {
        return service != null;
    }

    /**
     * Logs the vote to both the console and the database.
     */
    public void log() {
        plugin.getLogger().log(Level.INFO, "Received vote by " + name + " from " + service, this);

        // Timestamp has a different format for each voting website, so we are
        // using our own which depends on the time it was received
        EasyDB.getDb().update("INSERT INTO votes (name, service, address) VALUES (?, ?, ?) ",
                name, service.getId(), address);

        // Insert vote into our recent votes db
        EasyDB.getDb().update("INSERT IGNORE INTO votes_recent (name, service) VALUES (?, ?) ",
                name, service.getId());
    }

    public void runCommands(List<String> cmds) {
        for (String cmd : cmds) {
            cmd = cmd.replace("%name%", name);
            cmd = cmd.replace("%service%", service.getId());
            cmd = cmd.replace("%service_name%", service.getName());
            cmd = cmd.replace("%address%", address);

            try {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            } catch (CommandException ex) {
                plugin.getLogger().log(Level.SEVERE, "An error occured when running the command '" + cmd + "'.", ex);
            }
        }
    }
}
