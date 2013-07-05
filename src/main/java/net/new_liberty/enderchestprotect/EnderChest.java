package net.new_liberty.enderchestprotect;

import com.simplyian.easydb.EasyDB;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import org.apache.commons.dbutils.ResultSetHandler;
import org.bukkit.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Object to manipulate an Ender Chest in an object-oriented fashion.
 */
public class EnderChest {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMMM d, yyyy hh:mm aaa");

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
        checkDirty();
        return owner;
    }

    public Location getLocation() {
        checkDirty();
        return loc;
    }

    public String getContents() {
        checkDirty();
        return contents;
    }

    public Timestamp getExpiryTime() {
        checkDirty();
        return expiryTime;
    }

    /**
     * Checks if this Ender Chest's protection is expired.
     *
     * @return
     */
    public boolean isExpired() {
        return getExpiryTime().before(new Timestamp(System.currentTimeMillis()));
    }

    /**
     * Updates the expiry time of this Ender Chest.
     *
     * @return The new expiry time.
     */
    public Timestamp updateExpiryTime() {
        Timestamp newTime = new Timestamp(System.currentTimeMillis() + (plugin.getConfig().getInt("expiry-minutes", 14 * 24 * 60) * 60 * 1000));
        EasyDB.getDb().update("UPDATE enderchests SET expiry_time = ? WHERE id = ?", newTime, id);
        dirty = true;
        return newTime;
    }

    public String getExpiryInfoMessage() {
        return ChatColor.YELLOW + "This chest's protection expires on " + ChatColor.BLUE + DATE_FORMAT.format(getExpiryTime()) + " " + ChatColor.GREEN + "(" + ((getExpiryTime().getTime() - System.currentTimeMillis()) / (60 * 1000)) + " minutes from now)";
    }

    /**
     * Checks if the player can access the chest.
     *
     * @param p
     * @return
     */
    public boolean canAccess(Player p) {
        return getOwner().equals(p.getName()) || p.hasPermission("nlenderchest.admin") || isExpired();
    }

    /**
     * Gets an Inventory for this chest.
     *
     * @return
     */
    public Inventory getInventory() {
        Inventory inv = plugin.getECManager().getInventory(id);
        if (inv == null) {
            inv = plugin.getECManager().createInventory(this);
        }
        return inv;
    }

    /**
     * Checks if this Ender Chest contains items.
     *
     * @return
     */
    public boolean hasItems() {
        return getContents() != null || "".equals(getContents());
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

    /**
     * Performs a dirty check and repopulates if dirty.
     */
    private void checkDirty() {
        if (dirty) {
            repopulate();
            dirty = false;
        }
    }
}
