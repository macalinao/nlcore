/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.itemconomy;

/**
 *
 * @author simplyianm
 */
public interface CurrencyHolder {

    /**
     * Gets the total currency this CurrencyHolder is holding.
     *
     * @return
     */
    public int balance();

    /**
     * Adds an amount of economy to this CurrencyHolder.
     *
     * @param amt
     * @return True if the add was successful
     */
    public boolean add(int amt);

    /**
     * Removes some currency from this CurrencyHolder.
     *
     * @param amt
     * @return Overflow (non-zero value indicates failure, returned value is the
     * amount of single-value currency needed to complete a transaction
     */
    public int remove(int amt);

}
