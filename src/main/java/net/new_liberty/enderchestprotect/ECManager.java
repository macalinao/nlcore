package net.new_liberty.enderchestprotect;

import com.simplyian.easydb.EasyDB;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbutils.ResultSetHandler;
import org.bukkit.Location;

/**
 * Manages Ender Chests.
 */
public class ECManager {
    private final EnderChestProtect plugin;

    public ECManager(EnderChestProtect plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates a chest with the given owner and location.
     *
     * @param owner
     * @param loc
     * @return
     */
    public EnderChest createChest(String owner, Location loc) {
        EasyDB.getDb().update("INSERT INTO enderchests (owner, world, x, y, z) VALUES (?, ?, ?, ?, ?)", owner, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        return getChest(loc);
    }

    /**
     * Loads a chest from its location.
     *
     * @param loc
     * @return
     */
    public EnderChest getChest(final Location loc) {
        return EasyDB.getDb().query("SELECT * FROM enderchests WHERE world = ? AND x = ? AND y = ? AND z = ?", new ResultSetHandler<EnderChest>() {
            @Override
            public EnderChest handle(ResultSet rs) throws SQLException {
                if (!rs.next()) {
                    return null;
                };
                EnderChest ec = new EnderChest(plugin, rs.getInt("id"));
                ec.setData(rs.getString("owner"), loc, rs.getString("contents"), rs.getTimestamp("expiry_time"));
                return ec;
            }
        }, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    /**
     * Gets all of the Ender Chests of a player.
     *
     * @param p
     * @return
     */
    public List<EnderChest> getChests(String p) {
        return EasyDB.getDb().query("SELECT * FROM enderchests WHERE owner = ?", new ResultSetHandler<List<EnderChest>>() {
            @Override
            public List<EnderChest> handle(ResultSet rs) throws SQLException {
                List<EnderChest> ret = new ArrayList<EnderChest>();
                while (rs.next()) {
                    EnderChest ec = new EnderChest(plugin, rs.getInt("id"));
                    ec.setData(rs);
                    ret.add(ec);
                }
                return ret;
            }
        }, p);
    }
}
