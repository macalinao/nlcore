/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.itemconomy.exchange;

import java.io.File;
import net.new_liberty.itemconomy.Itemconomy;

/**
 * Represents the emerald exchange.
 */
public class Exchange {

    private final Itemconomy ic;

    private ExchangeSigns signs;

    private double exchangeRate;

    public Exchange(Itemconomy ic) {
        this.ic = ic;

        signs = new ExchangeSigns(new File(ic.getDataFolder(), "exchange-signs.yml"));
    }

    /**
     * Gets the number of civs each emerald is worth.
     *
     * @return
     */
    public double getExchangeRate() {
        return exchangeRate;
    }

}
