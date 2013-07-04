package net.new_liberty.enderchestprotect;

import com.simplyian.easydb.EasyDB;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.dbutils.ResultSetHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderChestProtect extends JavaPlugin {
    @Override
    public void onEnable() {
        if (!EasyDB.getDb().isValid()) {
            getLogger().log(Level.SEVERE, "Invalid database credentials; plugin loading halted.");
            return;
        }

        // Save the config
        saveDefaultConfig();
        reloadConfig();

        EasyDB.getDb().update("CREATE TABLE IF NOT EXISTS enderchests ("
                + "id INT(10) NOT NULL AUTO_INCREMENT,"
                + "owner VARCHAR(16) NOT NULL,"
                + "world VARCHAR(255) NOT NULL,"
                + "x INT(10) NOT NULL,"
                + "y INT(10) NOT NULL,"
                + "z INT(10) NOT NULL,"
                + "contents TEXT,"
                + "expiry_time TIMESTAMP NOT NULL,"
                + "PRIMARY KEY (id));");

        getCommand("enderchest").setExecutor(new EnderChestCommand(this));
        Bukkit.getPluginManager().registerEvents(new ECPListener(this), this);
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
                rs.next();

                return new EnderChest(EnderChestProtect.this, rs.getInt("id"), rs.getString("owner"), loc, rs.getString("contents"), rs.getTimestamp("expiry_date"));
            }
        }, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
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

    public List<EnderChest> getChests(Player p) {
        return getChests(p.getName());
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
                    String worldStr = rs.getString("world");
                    World world = Bukkit.getWorld(worldStr);
                    Location loc = new Location(world, rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                    ret.add(new EnderChest(EnderChestProtect.this, rs.getInt("id"), rs.getString("owner"), loc, rs.getString("contents"), rs.getTimestamp("expiry_date")));
                }
                return ret;
            }
        }, p);
    }

    /**
     * Gets the amount of chests a given player is allowed to have.
     *
     * @param p
     * @return
     */
    public int getAllowedChestCount(Player p) {
        for (int i = 15; i > 0; i--) {
            if (p.hasPermission("nlenderchest.place." + i)) {
                return i;
            }
        }
        return -1;
    }
}
