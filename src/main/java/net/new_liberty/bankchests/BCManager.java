/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.bankchests;

import net.new_liberty.nlcore.database.Database;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.new_liberty.enderchestprotect.EnderChest;
import net.new_liberty.util.InventorySerializer;
import org.apache.commons.dbutils.ResultSetHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author simplyianm
 */
public class BCManager {

    private final BankChests e;

    /**
     * The bank inventories on the server.
     */
    private Map<String, Inventory> inventories = new HashMap<String, Inventory>();

    public BCManager(BankChests e) {
        this.e = e;
    }

    /**
     * Gets a player's bank chest.
     *
     * @param owner
     * @return
     */
    public BankChest getChest(final String owner) {
        BankChest b = Database.i().query("SELECT * FROM bankchests WHERE owner = ?", new ResultSetHandler<BankChest>() {
            @Override
            public BankChest handle(ResultSet rs) throws SQLException {
                if (!rs.next()) {
                    return null;
                }

                BankChest bc = new BankChest(BCManager.this, owner);
                bc.setContents(rs.getString("contents"));
                return bc;
            }

        }, owner);

        if (b != null) {
            return b;
        }

        Database.i().update("INSERT INTO bankchests (owner) VALUES (?)", owner);
        return new BankChest(this, owner);
    }

    /**
     * Gets the inventory of a BankChest.
     *
     * @param chest
     * @return
     */
    public Inventory getInventory(BankChest chest) {
        Inventory inv = inventories.get(chest.getOwner());
        if (inv != null) {
            return inv;
        }

        inv = Bukkit.createInventory(null, 9, chest.getOwner() + "'s bank chest");
        if (chest.getContents() != null) {
            try {
                InventorySerializer.loadFromString(chest.getContents(), inv);
            } catch (InvalidConfigurationException ex) {
                e.getLogger().log(Level.SEVERE, "Corrupted bank - " + chest.getOwner() + "!", ex);
            }
        }
        inventories.put(chest.getOwner(), inv);

        return inv;
    }

}
