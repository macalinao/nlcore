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

    private final ExchangeSigns signs;

    private final ExchangeSignListener esl;

    private double exchangeRate;

    /**
     * Markup on the asking price. This keeps people from immediately trading
     * when the price is favorable.
     */
    private double askMarkup = 0.02;

    public Exchange(Itemconomy ic) {
        this.ic = ic;

        signs = new ExchangeSigns(this, new File(ic.getDataFolder(), "exchange-signs.yml"));
        esl = new ExchangeSignListener(this);

        ic.addListener(esl);
    }

    /**
     * Gets the number of civs each emerald can be sold for.
     *
     * @return
     */
    public double getExchangeRate() {
        return exchangeRate;
    }

    /**
     * Gets the asking price.
     *
     * @return
     */
    public double getAskingPrice() {
        return exchangeRate * (1.0 + askMarkup);
    }

    public ExchangeSigns getSigns() {
        return signs;
    }

}
