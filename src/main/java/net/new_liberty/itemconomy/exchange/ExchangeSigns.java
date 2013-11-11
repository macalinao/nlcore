/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.new_liberty.itemconomy.exchange;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.new_liberty.itemconomy.Itemconomy;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author simplyianm
 */
public class ExchangeSigns {

    private final File file;

    private Map<Location, ExchangeSign> signs;

    public ExchangeSigns(File file) {
        this.file = file;
    }

    public void load() {
        signs = new HashMap<Location, ExchangeSign>();

        YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
        List<Map<String, Object>> smap = (List<Map<String, Object>>) conf.getList("signs");
        for (Map<String, Object> esign : smap) {
            Location loc = (Location) esign.get("loc");
            boolean buy = (Boolean) esign.get("buy");
            int amt = (Integer) esign.get("amt");
            signs.put(loc, new ExchangeSign(loc.getBlock(), buy, amt));
        }
    }

    public void save() {
        List<Map<String, Object>> smap = new ArrayList<Map<String, Object>>();

        for (ExchangeSign s : signs.values()) {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("loc", s.getBlock().getLocation());
            m.put("buy", s.isBuy());
            m.put("amt", s.getAmt());
            smap.add(m);
        }

        YamlConfiguration conf = new YamlConfiguration();
        conf.set("signs", smap);
        try {
            conf.save(file);
        } catch (IOException ex) {
            Itemconomy.i().getLogger().log(Level.SEVERE, "Could not save exchange signs!", ex);
        }
    }

}
