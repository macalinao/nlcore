/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.nlcore.player;

import net.new_liberty.itemconomy.BankAccount;
import net.new_liberty.itemconomy.CurrencyInventory;
import net.new_liberty.nlcore.NLCore;
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
     * Gets this player's balance.
     *
     * @return
     */
    public double balance() {
        return NLCore.i().getEconomy().getBalance(p.getName());
    }

    /**
     * Deposits civs into this player's account.
     *
     * @param amt
     */
    public void deposit(double amt) {
        NLCore.i().getEconomy().depositPlayer(p.getName(), amt);
    }

    /**
     * Withdraws civs from this player's account.
     *
     * @param amt
     */
    public void withdraw(double amt) {
        NLCore.i().getEconomy().withdrawPlayer(p.getName(), amt);
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

    /**
     * Gets the capacity of this player's emerald account.
     *
     * @return
     */
    public int getEmeraldAccountCapacity() {
        switch (getDonorRank()) {
            case PREMIUM:
                return 1000;
            case HERO:
                return 2000;
            case ELITE:
                return 5000;
            case GUARDIAN:
                return 20000;
            case CHAMPION:
                return 10000000;
            default:
                return 500;
        }
    }

}
