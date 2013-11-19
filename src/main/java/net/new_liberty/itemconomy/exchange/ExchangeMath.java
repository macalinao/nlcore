/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.itemconomy.exchange;

/**
 * Contains the methods to find buy/sell prices. Additional quantity demanded
 * per unit is expected to be a line.
 *
 * <li>P = current price</li>
 * <li>Q = quantity to buy/sell</li>
 * <li>S = slope -- change in price per unit exchanged</li>
 */
public class ExchangeMath {

    public static final double S = 0.0005;

    /**
     * Private c'tor
     */
    private ExchangeMath() {
    }

    /**
     * The buy price of a given number of emeralds.
     *
     * <p>This is the integral of S*x dx from x=P/S to P/S+Q.
     * <p>0.5Q(2P+QS)
     *
     * @param p
     * @param q
     * @return
     */
    public static double buyPrice(double p, int q) {
        return 0.5 * q * (2 * p + q * S);
    }

    /**
     * The sell price of a given number of emeralds.
     *
     * <p>This is the integral of S*x dx from x=P/S-Q to P/S.
     * <p>PQ-((Q^2)S)/2
     *
     * @param p
     * @param q
     * @return
     */
    public static double sellPrice(double p, int q) {
        return p * q - ((q * q * S) / 2);
    }

}
