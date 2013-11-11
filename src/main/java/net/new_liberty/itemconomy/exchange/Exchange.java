/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.itemconomy.exchange;

import java.util.ArrayList;
import java.util.List;
import net.new_liberty.itemconomy.Itemconomy;

/**
 * Represents the emerald exchange.
 */
public class Exchange {

    private final Itemconomy ic;

    private List<ExchangeSign> signs = new ArrayList<ExchangeSign>();

    public Exchange(Itemconomy ic) {
        this.ic = ic;
    }

}
