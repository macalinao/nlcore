package net.new_liberty.enderchestprotect;

import com.simplyian.easydb.EasyDB;
import java.sql.Timestamp;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Object to manipulate an Ender Chest in an object-oriented fashion. These
 * objects should not be stored.
 */
public class EnderChest {
    private EnderChestProtect plugin;

    private final int id;

    private final String owner;

    private final Location loc;

    private final String inventory;

    private final Timestamp expiry;

    public EnderChest(EnderChestProtect plugin, int id, String owner, Location loc, String inventory, Timestamp expiry) {
        this.plugin = plugin;
        this.id = id;
        this.owner = owner;
        this.loc = loc;
        this.inventory = inventory;
        this.expiry = expiry;
    }

    public String getOwner() {
        return owner;
    }

    public Location getLocation() {
        return loc;
    }

    public Timestamp getExpiry() {
        return expiry;
    }

    /**
     * Updates the expiry time of this Ender Chest.
     */
    public void updateExpiry() {
        Timestamp newTime = new Timestamp(expiry.getTime() + (plugin.getConfig().getInt("expiry-minutes", 14 * 24 * 60) * 60 * 1000));
        EasyDB.getDb().update("UPDATE enderchests SET expiry_time = ? WHERE id = ?", newTime, id);
    }

    public String getLocationString() {
        return loc.getWorld().getName() + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
    }

    public void open(Player p) {
        Inventory inv = Bukkit.createInventory(p, 27, "ProtectedEnderChest");
        if (inventory != null) {
            try {
                InventorySerializer.loadFromString(inventory, inv);
            } catch (InvalidConfigurationException ex) {
                plugin.getLogger().log(Level.SEVERE, "Corrupted Ender Chest at " + getLocationString() + "! Fix soon or " + owner + " will be mad!");
            }
        }
        p.openInventory(inv);
    }

    /**
     * Checks if this Ender Chest contains items.
     *
     * @return
     */
    public boolean hasItems() {
        return inventory != null;
    }

    /**
     * Saves the chest with no contents.
     */
    public void save() {
        save(null);
    }

    /**
     * Saves the chest with the given inventory.
     *
     * @param inv
     */
    public void save(Inventory inv) {
        String contents = (inv == null ? null : InventorySerializer.writeToString(inv));
        EasyDB.getDb().update("UPDATE enderchests SET contents = ? WHERE id = ?", contents, id);
    }

    /**
     * Destroys the Ender Chest.
     */
    public void destroy() {
        EasyDB.getDb().update("DELETE FROM enderchests WHERE id = ?", id);
        loc.getBlock().setType(Material.AIR);
    }
}
