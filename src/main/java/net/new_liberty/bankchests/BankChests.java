/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.bankchests;

import net.new_liberty.nlcore.database.DB;
import net.new_liberty.nlcore.module.Module;
import net.new_liberty.nlcore.player.DonorRank;
import net.new_liberty.nlcore.player.NLPlayer;
import net.new_liberty.nlcore.player.StaffRank;

/**
 * Handles the single big ender chest at spawn.
 */
public class BankChests extends Module {

    private BCListener l;

    private BCManager chests;

    @Override
    public void onEnable() {
        l = new BCListener(this);
        addListener(l);

        chests = new BCManager(this);

        DB.i().update("CREATE TABLE IF NOT EXISTS bankchests ("
                + "owner VARCHAR(16) NOT NULL,"
                + "contents TEXT,"
                + "rows TINYINT(2) NOT NULL DEFAULT 1,"
                + "PRIMARY KEY (owner));");

        addPermission("bankchests.admin", "Allows placement of ender chests.");
    }

    public BCManager getChests() {
        return chests;
    }

    /**
     * Gets the number of rows a player can have in their bank chest.
     *
     * @param p
     * @return
     */
    public int getAllowedRows(NLPlayer p) {
        StaffRank s = p.getStaffRank();
        switch (s) {
            case MOD:
            case ADMIN:
                return 10;
        }

        DonorRank d = p.getDonorRank();
        switch (d) {
            case PREMIUM:
                return 2;
            case HERO:
                return 3;
            case ELITE:
                return 4;
            case GUARDIAN:
                return 5;
            case CHAMPION:
                return 6;
            default:
                return 1;
        }
    }

}
