/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.horses;

import com.simplyian.easydb.EasyDB;
import net.new_liberty.core.module.Module;

/**
 * Handles horses.
 */
public class Horses extends Module {

    @Override
    public String[] getDependencies() {
        return new String[]{"EasyDB"};
    }

    @Override
    public void onEnable() {
        EasyDB.getDb().update("CREATE TABLE IF NOT EXISTS horses ("
                + "id VARCHAR(36) NOT NULL,"
                + "owner VARCHAR(16) NOT NULL,"
                + "PRIMARY KEY (uuid));");
    }

}
