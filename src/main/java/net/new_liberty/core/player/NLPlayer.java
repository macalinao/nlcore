/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.core.player;

import net.new_liberty.core.itemconomy.BankAccount;
import net.new_liberty.core.itemconomy.CurrencyInventory;
import org.bukkit.entity.Player;

/**
 * Player object for convenience.
 */
public class NLPlayer {

    private final Player p;

    public NLPlayer(Player p) {
        this.p = p;
    }

    public DonorRank getDonorRank() {
        DonorRank highest = null;
        for (DonorRank r : DonorRank.values()) {
            if (p.hasPermission(r.getPermission())) {
                highest = r;
            }
        }
        return highest;
    }

    public StaffRank getStaffRank() {
        StaffRank highest = null;
        for (StaffRank r : StaffRank.values()) {
            if (p.hasPermission(r.getPermission())) {
                highest = r;
            }
        }
        return highest;
    }

    /**
     * Gets this player's emerald account.
     *
     * @return
     */
    public BankAccount getEmeraldAccount() {
        return new BankAccount(p);
    }

    /**
     * Gets this player's emerald inventory.
     *
     * @return
     */
    public CurrencyInventory getEmeraldInventory() {
        return new CurrencyInventory(p);
    }

}
