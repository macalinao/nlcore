package net.new_liberty.votesuite;

import com.simplyian.easydb.EasyDB;
import java.util.List;
import java.util.Set;
import org.apache.commons.dbutils.handlers.ColumnListHandler;

/**
 * Represents someone who votes.
 */
public class Voter {
    /**
     * The VoteSuite plugin.
     */
    private final VoteSuite plugin;

    /**
     * The player this Voter represents.
     */
    private final String player;

    /**
     * C'tor
     *
     * @param player
     */
    public Voter(VoteSuite plugin, String player) {
        this.plugin = plugin;
        this.player = player;
    }

    /**
     * Adds a vote for this Voter.
     *
     * @param svc
     * @param address
     */
    public void addVote(VoteService svc, String address) {
        // Timestamp has a different format for each voting website, so we are
        // using our own which depends on the time it was received
        EasyDB.getDb().update("INSERT INTO votes (name, service, address) VALUES (?, ?, ?) ",
                player, svc.getId(), address);

        // Insert vote into our recent votes db
        EasyDB.getDb().update("INSERT IGNORE INTO votes_recent (name, service) VALUES (?, ?) ",
                player, svc.getId());
    }

    /**
     * Gets the vote services this voter is missing.
     *
     * @return
     */
    public Set<VoteService> getMissingServices() {
        List<String> voteSvcIds = EasyDB.getDb().query("SELECT service FROM votes_recent WHERE name = ?", new ColumnListHandler<String>(), player);
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
        return missingServices;
    }

    /**
     * Clears the recent votes of this Voter.
     */
    public void clearRecentVotes() {
        EasyDB.getDb().update("DELETE FROM votes_recent WHERE name = ?", player);
    }
}
