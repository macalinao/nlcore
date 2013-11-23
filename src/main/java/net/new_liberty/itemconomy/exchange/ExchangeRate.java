/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.itemconomy.exchange;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import net.new_liberty.nlcore.database.DB;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Saves the exchange rate periodically.
 */
public class ExchangeRate extends BukkitRunnable {

    private final Exchange x;

    private final File file;

    private YamlConfiguration data;

    private double rate;

    public ExchangeRate(Exchange x) {
        this.x = x;

        file = new File(x.getIc().getDataFolder(), "exchange.yml");
    }

    @Override
    public void run() {
        save();
    }

    /**
     * Gets the current emerald exchange rate.
     *
     * @return
     */
    public double getRate() {
        return rate;
    }

    /**
     * Loads the data.
     */
    public void load() {
        data = YamlConfiguration.loadConfiguration(file);
        rate = data.getDouble("exchange-rate", 1);
    }

    /**
     * Saves the exchange rate.
     */
    public void save() {
        data.set("exchange-rate", x.getExchangeRate());
        try {
            data.save(file);
        } catch (IOException ex) {
            x.getIc().getLogger().log(Level.SEVERE, "Could not save exchange rate to data file!", ex);
        }
        DB.i().update("INSERT INTO icexchange (rate) VALUES (?)", rate);
    }

    /**
     * Alters the rate by the given difference.
     *
     * @param diff
     */
    public void alterRate(double diff) {
        rate += diff;
    }

}
