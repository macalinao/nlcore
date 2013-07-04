package net.new_liberty.enderchestprotect;

import com.simplyian.easydb.EasyDB;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.dbutils.ResultSetHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderChestProtect extends JavaPlugin {
    private ECManager ecManager;

    @Override
    public void onEnable() {
        if (!EasyDB.getDb().isValid()) {
            getLogger().log(Level.SEVERE, "Invalid database credentials; plugin loading halted.");
            return;
        }

        // Save the config
        saveDefaultConfig();
        reloadConfig();

        ecManager = new ECManager(this);

        EasyDB.getDb().update("CREATE TABLE IF NOT EXISTS enderchests ("
                + "id INT(10) NOT NULL AUTO_INCREMENT,"
                + "owner VARCHAR(16) NOT NULL,"
                + "world VARCHAR(255) NOT NULL,"
                + "x INT(10) NOT NULL,"
                + "y INT(10) NOT NULL,"
                + "z INT(10) NOT NULL,"
                + "contents TEXT,"
                + "expiry_time TIMESTAMP NOT NULL,"
                + "PRIMARY KEY (id));");

        getCommand("enderchest").setExecutor(new EnderChestCommand(this));
        Bukkit.getPluginManager().registerEvents(new ECPListener(this), this);
    }

    /**
     * Gets the Ender Chest manager.
     *
     * @return
     */
    public ECManager getECManager() {
        return ecManager;
    }

    /**
     * Gets the amount of chests a given player is allowed to have.
     *
     * @param p
     * @return
     */
    public int getAllowedChestCount(Player p) {
        for (int i = 15; i > 0; i--) {
            if (p.hasPermission("nlenderchest.place." + i)) {
                return i;
            }
        }
        return -1;
    }
}
