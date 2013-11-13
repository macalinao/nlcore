/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.bankchests;

import java.util.HashMap;
import java.util.Map;
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

}
