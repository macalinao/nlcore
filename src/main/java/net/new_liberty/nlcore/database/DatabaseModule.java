package net.new_liberty.nlcore.database;

import net.new_liberty.nlcore.database.command.DBConfigCommand;
import net.new_liberty.nlcore.database.command.DBReloadCommand;
import net.new_liberty.nlcore.database.event.DBConfigReloadEvent;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import net.new_liberty.nlcore.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Database module.
 */
public class DatabaseModule extends Module {

    public static final List<String> FIELDS = Arrays.asList("host", "port", "user", "pass", "name");

    private DB db;

    @Override
    public void onEnable() {
        reloadDb();
        plugin.saveConfig();

        addCommand("dbconfig", new DBConfigCommand(this));
        addCommand("dbreload", new DBReloadCommand(this));

        if (!db.isValid()) {
            getLogger().log(Level.WARNING, "Database credentials are invalid. Please check your credentials and run /dbreload.");
        } else {
            getLogger().log(Level.INFO, "Connected to database at " + db.getSource().getServerName() + ":" + db.getSource().getPort() + " successfully.");
        }

        DB.setInstance(db);
    }

    @Override
    public void onDisable() {
        DB.setInstance(null);
    }

    /**
     * Loads the database.
     */
    public void reloadDb() {
        plugin.reloadConfig();
        ConfigurationSection s = plugin.getConfig().getConfigurationSection("db");
        if (s == null) {
            s = plugin.getConfig().createSection("db");
        }

        String dbHost = s.getString("host");
        int dbPort = s.getInt("port");
        String dbUser = s.getString("user");
        String dbPass = s.getString("pass", "");
        String dbName = s.getString("name");
        db = new DB(this, dbUser, dbPass, dbHost, dbPort, dbName);

        Bukkit.getPluginManager().callEvent(new DBConfigReloadEvent(db.isValid()));
    }

    /**
     * Gets the database.
     *
     * @return
     */
    public DB getDb() {
        return db;
    }

}
