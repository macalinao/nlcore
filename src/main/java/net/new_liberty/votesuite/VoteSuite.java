package net.new_liberty.votesuite;

import com.simplyian.easydb.EasyDB;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * VoteSuite main class.
 */
public class VoteSuite extends JavaPlugin {
    @Override
    public void onEnable() {
        if (!EasyDB.getDb().isValid()) {
            getLogger().log(Level.WARNING, "EasyDB isn't connected; plugin loading halted.");
            return;
        }

        EasyDB.getDb().update("CREATE TABLE IF NOT EXISTS votes ("
                + "id INT(10) NOT NULL AUTO_INCREMENT,"
                + "name varchar(16) NOT NULL,"
                + "service varchar(50) NOT NULL,"
                + "address varchar(50) NOT NULL,"
                + "time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (id));");

        Bukkit.getPluginManager().registerEvents(new VSListener(this), this);

        getLogger().log(Level.INFO, "Plugin loaded.");
    }
}
