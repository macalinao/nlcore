/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.bankchests;

import com.simplyian.easydb.EasyDB;
import net.new_liberty.nlcore.module.Module;

/**
 * Handles the single big ender chest at spawn.
 */
public class BankChests extends Module {

    @Override
    public String[] getDependencies() {
        return new String[]{"EasyDB"};
    }

    @Override
    public void onEnable() {
        EasyDB.getDb().update("CREATE TABLE IF NOT EXISTS ecb_inventories ("
                + "owner VARCHAR(16) NOT NULL,"
                + "contents TEXT,"
                + "PRIMARY KEY (owner));");
    }

}
