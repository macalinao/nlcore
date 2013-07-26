package net.new_liberty.votesuite;

import com.simplyian.easydb.EasyDB;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

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

    /**
     * Counts the number of votes tallied up for this player in the past 24
     * hours.
     *
     * @return
     */
    public int countDayVotes() {
        String query = "SELECT COUNT(*) FROM votes WHERE name = ? AND time > DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY)";
        Object ret = EasyDB.getDb().get(query, this, player);
        if (ret != null) {
            return ((Number) ret).intValue();
        }
        return 0;
    }

    /**
     * Gets this Voter's home.
     *
     * @return
     */
    public Location getHome() {
        String query = "SELECT * FROM votes_homes WHERE name = ?";
        return EasyDB.getDb().query(query, new ResultSetHandler<Location>() {
            @Override
            public Location handle(ResultSet rs) throws SQLException {
                if (!rs.next()) {
                    return null;
                }

                String worldStr = rs.getString("world");
                World w = Bukkit.getWorld(worldStr);
                if (w == null) {
                    return null;
                }
                double x = rs.getDouble("x");
                double y = rs.getDouble("y");
                double z = rs.getDouble("z");
                float yaw = rs.getFloat("yaw");
                float pitch = rs.getFloat("pitch");

                return new Location(w, x, y, z, yaw, pitch);
            }
        }, player);
    }

    /**
     * Sets this Voter's home.
     *
     * @param loc
     */
    public void setHome(Location loc) {
        String query = "INSERT INTO votes_homes (name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ?";
        EasyDB.getDb().update(query, player, loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(),
                loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }
}
