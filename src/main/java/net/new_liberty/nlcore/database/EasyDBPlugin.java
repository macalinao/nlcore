package net.new_liberty.nlcore.database;

import net.new_liberty.nlcore.database.command.DBConfigCommand;
import net.new_liberty.nlcore.database.command.DBReloadCommand;
import net.new_liberty.nlcore.database.event.DBConfigReloadEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * EasyDBPlugin Main class.
 */
public class EasyDBPlugin extends JavaPlugin {

    public static final List<String> FIELDS = Arrays.asList("host", "port", "user", "pass", "name");

    private Database db;

    @Override
    public void onEnable() {
        // Make sure the config has been made
        saveDefaultConfig();

        EasyDB.setInstance(this);
        reloadDb();

        getCommand("dbconfig").setExecutor(new DBConfigCommand(this));
        getCommand("dbreload").setExecutor(new DBReloadCommand(this));

        if (!db.isValid()) {
            getLogger().log(Level.WARNING, "Database credentials are invalid. Please check your credentials and run /dbreload.");
        } else {
            getLogger().log(Level.INFO, "Connected to database at " + db.getSource().getServerName() + ":" + db.getSource().getPort() + " successfully.");
        }
        getLogger().log(Level.INFO, "Plugin loaded.");
    }

    @Override
    public void onDisable() {
        EasyDB.setInstance(null);
        getLogger().log(Level.INFO, "Plugin unloaded.");
    }

    /**
     * Loads the database.
     */
    public void reloadDb() {
        reloadConfig();
        ConfigurationSection s = getConfig().getConfigurationSection("db");
        if (s == null) {
            s = getConfig().createSection("db");
        }

        String dbHost = s.getString("host");
        int dbPort = s.getInt("port");
        String dbUser = s.getString("user");
        String dbPass = s.getString("pass", "");
        String dbName = s.getString("name");
        db = new Database(this, dbUser, dbPass, dbHost, dbPort, dbName);

        Bukkit.getPluginManager().callEvent(new DBConfigReloadEvent(db.isValid()));
    }

    /**
     * Gets the database.
     *
     * @return
     */
    public Database getDb() {
        return db;
    }

}
