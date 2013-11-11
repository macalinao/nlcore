/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.itemconomy;

import com.simplyian.easydb.EasyDB;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.new_liberty.nlcore.module.Module;
import net.new_liberty.itemconomy.commands.ICBalance;
import net.new_liberty.itemconomy.commands.ICSignBalance;
import net.new_liberty.itemconomy.commands.ICSignBuy;
import net.new_liberty.itemconomy.commands.ICSignDeposit;
import net.new_liberty.itemconomy.commands.ICSignWithdraw;
import org.bukkit.Bukkit;
import org.bukkit.Material;

/**
 * Our item based economy, based on emeralds.
 *
 * @author simplyianm
 */
public class Itemconomy extends Module {

    /**
     * Represents the change of price per emerald bought/sold.
     */
    public static final double PRICE_CHANGE = 0.005;

    private static Itemconomy _instance;

    private Map<Material, Integer> currency = new EnumMap<Material, Integer>(Material.class);

    @Override
    public void onEnable() {
        _instance = this;

        addPermission("itemconomy.admin", "Admin permission.");
        addPermission("itemconomy.console", "Console-only permission used for signs.");

        // Our currency - TODO config
        currency.put(Material.EMERALD, 1);
        currency.put(Material.EMERALD_BLOCK, 9);

        // Database stuff
        if (Bukkit.getPluginManager().getPlugin("EasyDB") != null) {
            EasyDB.getDb().update("CREATE TABLE IF NOT EXISTS icbank ("
                    + "id INT(10) NOT NULL AUTO_INCREMENT,"
                    + "player varchar(16) NOT NULL,"
                    + "balance INT(10) NOT NULL,"
                    + "PRIMARY KEY (id));");

            EasyDB.getDb().update("CREATE TABLE IF NOT EXISTS icmarket ("
                    + "date TIMESTAMP NOT NULL,"
                    + "value INT(10) NOT NULL," // This'll be the value multiplied by 1000.
                    + "PRIMARY KEY (date));");
        }

        // Commands
        plugin.getCommand("icbalance").setExecutor(new ICBalance());
        plugin.getCommand("icgrant").setExecutor(new ICBalance());

        plugin.getCommand("icsignbalance").setExecutor(new ICSignBalance());
        plugin.getCommand("icsignbuy").setExecutor(new ICSignBuy());
        plugin.getCommand("icsigndeposit").setExecutor(new ICSignDeposit());
        plugin.getCommand("icsignwithdraw").setExecutor(new ICSignWithdraw());
    }

    @Override
    public void onDisable() {
        _instance = null;
    }

    /**
     * Gets the Itemconomy instance.
     *
     * @return
     */
    public static Itemconomy i() {
        return _instance;
    }

    /**
     * Gets a set of all the currency.
     *
     * @return
     */
    public Set<Entry<Material, Integer>> currencySet() {
        return currency.entrySet();
    }

    /**
     * Gets the value of a certain material in this economy.
     *
     * @param m
     * @return
     */
    public int getValue(Material m) {
        Integer r = currency.get(m);
        return (r == null) ? 0 : r;
    }

}
