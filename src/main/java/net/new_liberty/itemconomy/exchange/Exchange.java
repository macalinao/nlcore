/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.itemconomy.exchange;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.new_liberty.itemconomy.Itemconomy;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Represents the emerald exchange.
 */
public class Exchange {

    private final Itemconomy ic;

    private final ExchangeSigns signs;

    private final ExchangeSignListener esl;

    private final ExchangeRate rate;

    /**
     * Markup on the asking price. This keeps people from immediately trading
     * when the price is favorable.
     */
    private double askMarkup = 0.02;

    public Exchange(Itemconomy ic) {
        this.ic = ic;

        signs = new ExchangeSigns(this, new File(ic.getDataFolder(), "exchange-signs.yml"));
        esl = new ExchangeSignListener(this);
        rate = new ExchangeRate(this);
        rate.runTaskTimer(ic.getPlugin(), 20 * 60, 20 * 60);

        ic.addListener(esl);
    }

    public void load() {
        rate.load();
        signs.load();
    }

    public void save() {
        rate.save();
        signs.save();
    }

    public Itemconomy getIc() {
        return ic;
    }

    /**
     * Gets the number of civs each emerald can be sold for.
     *
     * @return
     */
    public double getExchangeRate() {
        return rate.getRate();
    }

    /**
     * Gets the asking price.
     *
     * @return
     */
    public double getAskingPrice() {
        return rate.getRate() * (1.0 + askMarkup);
    }

    /**
     * Updates the rate of exchange in the exchange.
     *
     * @param e
     */
    public void updateRate(ExchangeSign e) {
        double diff = e.getAmt() * ExchangeMath.S;
        if (e.isSell()) {
            diff *= -1;
        }
        rate.alterRate(diff);
    }

    public ExchangeSigns getSigns() {
        return signs;
    }

}
