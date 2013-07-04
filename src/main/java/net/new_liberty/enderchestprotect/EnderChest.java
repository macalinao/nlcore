package net.new_liberty.enderchestprotect;

import com.simplyian.easydb.EasyDB;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import org.apache.commons.dbutils.ResultSetHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Object to manipulate an Ender Chest in an object-oriented fashion.
 */
public class EnderChest {
    private EnderChestProtect plugin;

    private final int id;

    /**
     * If true, getter operations will reload the data from the database.
     */
    private boolean dirty = false;

    private String owner;

    private Location loc;

    private String contents;

    private Timestamp expiryTime;

    public EnderChest(EnderChestProtect plugin, int id) {
        this.plugin = plugin;
        this.id = id;
    }

    public void repopulate() {
        EasyDB.getDb().query("SELECT * FROM enderchests WHERE id = ?", new ResultSetHandler<Object>() {
            @Override
            public Object handle(ResultSet rs) throws SQLException {
                rs.next();
                setData(rs);
                return null;
            }
        }, id);
    }

    void setData(ResultSet rs) throws SQLException {
        setData(rs.getString("owner"), rs.getString("world"), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"), rs.getString("contents"), rs.getTimestamp("expiry_time"));
    }

    void setData(String owner, Location loc, String contents, Timestamp expiryTime) {
        this.owner = owner;
        this.loc = loc;
        this.contents = contents;
        this.expiryTime = expiryTime;
    }

    void setData(String owner, String world, int x, int y, int z, String contents, Timestamp expiryTime) {
        World w = Bukkit.getWorld(world);
        loc = new Location(w, x, y, z);
        setData(owner, loc, contents, expiryTime);
    }

    public int getId() {
        return id;
    }

    public String getOwner() {
        if (dirty) {
            repopulate();
            dirty = false;
        }
        return owner;
    }

    public Location getLocation() {
        if (dirty) {
            repopulate();
            dirty = false;
        }
        return loc;
    }

    public String getContents() {
        if (dirty) {
            repopulate();
            dirty = false;
        }
        return contents;
    }

    public Timestamp getExpiryTime() {
        if (dirty) {
            repopulate();
            dirty = false;
        }
        return expiryTime;
    }

    /**
     * Updates the expiry time of this Ender Chest.
     */
    public void updateExpiryTime() {
        Timestamp newTime = new Timestamp(getExpiryTime().getTime() + (plugin.getConfig().getInt("expiry-minutes", 14 * 24 * 60) * 60 * 1000));
        EasyDB.getDb().update("UPDATE enderchests SET expiry_time = ? WHERE id = ?", newTime, id);
        dirty = true;
    }

    /**
     * Opens this EnderChest for the given player.
     *
     * @param p
     */
    public void open(Player p) {
        Inventory inv = plugin.getECManager().getInventory(id);
        if (inv == null) {
            inv = plugin.getECManager().createInventory(this);
        }
        p.openInventory(inv);
    }

    /**
     * Checks if this Ender Chest contains items.
     *
     * @return
     */
    public boolean hasItems() {
        return getContents() != null;
    }

    /**
     * Saves the chest with the given inventory.
     *
     * @param inv
     */
    public void save(Inventory inv) {
        String theContents = (inv == null ? null : InventorySerializer.writeToString(inv));
        EasyDB.getDb().update("UPDATE enderchests SET contents = ? WHERE id = ?", theContents, id);
        dirty = true;
    }

    /**
     * Destroys the Ender Chest.
     */
    public void destroy() {
        EasyDB.getDb().update("DELETE FROM enderchests WHERE id = ?", id);
        loc.getBlock().setType(Material.AIR);
    }
}
