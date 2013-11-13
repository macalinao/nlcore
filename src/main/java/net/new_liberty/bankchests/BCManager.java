/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.bankchests;

import com.simplyian.easydb.EasyDB;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.new_liberty.enderchestprotect.EnderChest;
import org.apache.commons.dbutils.ResultSetHandler;
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
    private Map<Integer, Inventory> inventories = new HashMap<Integer, Inventory>();

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
        BankChest b = EasyDB.getDb().query("SELECT * FROM bankchests WHERE owner = ?", new ResultSetHandler<BankChest>() {
            @Override
            public BankChest handle(ResultSet rs) throws SQLException {
                if (!rs.next()) {
                    return null;
                }

                BankChest bc = new BankChest(owner);
                bc.setContents(rs.getString("contents"));
                return bc;
            }

        }, owner);

        if (b != null) {
            return b;
        }

        EasyDB.getDb().update("INSERT INTO bankchests (owner) VALUES (?)", owner);
        return new BankChest(owner);
    }

}
