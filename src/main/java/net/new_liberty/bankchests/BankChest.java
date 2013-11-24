/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.bankchests;

import net.new_liberty.nlcore.database.DB;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.new_liberty.util.InventorySerializer;
import org.apache.commons.dbutils.ResultSetHandler;
import org.bukkit.inventory.Inventory;

/**
 * Represents a player's ender chest inventory.
 */
public class BankChest {

    private final BCManager bcm;

    private final String owner;

    private String contents = null;

    private int rows = 1;

    public BankChest(BCManager bcm, String owner) {
        this.bcm = bcm;
        this.owner = owner;
    }

    public void populate() {
        DB.i().query("SELECT * FROM bankchests WHERE owner = ?", new ResultSetHandler<Object>() {
            @Override
            public Object handle(ResultSet rs) throws SQLException {
                rs.next();
                setContents(rs.getString("contents"));
                rows = rs.getInt("rows");
                return null;
            }

        }, owner);
    }

    public String getOwner() {
        return owner;
    }

    public String getContents() {
        return contents;
    }

    void setContents(String contents) {
        this.contents = contents;
    }

    public int getRows() {
        return rows;
    }

    /**
     * Updates the number of rows this bank chest has in the database.
     *
     * @param rows
     */
    public void updateRows(int rows) {
        this.rows = rows;
        DB.i().update("UPDATE bankchests SET rows = ? WHERE owner = ?", rows, owner);
    }

    public Inventory getInventory() {
        return bcm.getInventory(this);
    }

    /**
     * Saves the chest.
     *
     * @param inv
     */
    public void save() {
        Inventory inv = getInventory();
        String theContents = (inv == null ? null : InventorySerializer.writeToString(inv));
        DB.i().update("UPDATE bankchests SET contents = ? WHERE owner = ?", theContents, owner);
    }

}
