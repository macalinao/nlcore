/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.core.player;

/**
 * Represents a staff rank.
 */
public enum StaffRank {

    HELPER, MOD, ADMIN;

    public String getPermission() {
        return "ranks.staff." + name().toLowerCase();
    }

}
