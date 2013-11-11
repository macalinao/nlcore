/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.itemconomy.exchange;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Represents a sign in the emerald exchange.
 */
public class ExchangeSign {

    private final Exchange exchange;

    private final Block block;

    private final boolean buy;

    private final int amt;

    public ExchangeSign(Exchange exchange, Block block, boolean buy, int amt) {
        this.exchange = exchange;
        this.block = block;
        this.buy = buy;
        this.amt = amt;
    }

    public Block getBlock() {
        return block;
    }

    public boolean isBuy() {
        return buy;
    }

    public boolean isSell() {
        return !buy;
    }

    public int getAmt() {
        return amt;
    }

    /**
     * The price of exchanging with this sign.
     *
     * @return
     */
    public double price() {
        return exchange.getExchangeRate() * amt;
    }

    /**
     * Updates this ExchangeSign
     *
     * @return False if this sign is invalid (is not a sign)
     */
    public boolean update() {
        if (block.getType() != Material.SIGN) {
            return false;
        }

        Sign s = (Sign) block.getState();
        s.setLine(0, ChatColor.BLUE + "[" + (buy ? "Buy" : "Sell") + "]");
        s.setLine(1, ChatColor.RED.toString() + amt + " Emerald" + (amt == 1 ? "" : "s"));
        s.setLine(2, ChatColor.RED.toString() + price() + " civs");

        return true;
    }

}
