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
        Bukkit.getPluginManager().registerEvents(new VSListener(this), this);

        getLogger().log(Level.INFO, "Plugin loaded.");
    }
}
